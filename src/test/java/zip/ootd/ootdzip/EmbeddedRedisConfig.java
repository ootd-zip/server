package zip.ootd.ootdzip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

/**
 * 테스트를 위한 redis 입니다.
 * 해당 Redis 는 윈도우 환경은 사용하지 못합니다.
 * 레디스 포트가 겹칠시 명령어를 통해 새로운 포트를 찾는데 이 과정에서 맥/유닉스 에서 사용되는 명령어를 쓰기 때문입니다.
 * testcontainer 를 사용하는방법도 있지만, 실행마다 redis 이미지를 풀땡겨야하므로 시간이 오래걸려 사용하지 않았습니다.
 */
@DisplayName("Embedded Redis 설정")
@Slf4j
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    private void start() throws IOException {
        int port = isRedisRunning() ? findAvailablePort() : redisPort;
        redisServer = new RedisServer(port);
        redisServer.start();
    }

    @PreDestroy
    private void stop() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    private boolean isRedisRunning() throws IOException {
        return isRunning(executeGrepProcessCommand(redisPort));
    }

    public int findAvailablePort() throws IOException {
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);
            if (!isRunning(process)) {
                return port;
            }
        }
        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }

    private Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
        String[] shell = {"/bin/sh", "-c", command};
        return Runtime.getRuntime().exec(shell);
    }

    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();
        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return !pidInfo.isEmpty();
    }
}