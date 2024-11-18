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
 * 테스트 환경에서 사용되는 임베디드 Redis입니다.
 * 테스트가 외부 Redis 서버에 의존하지 않고 실행될 수 있도록 합니다.
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
        String[] shell;
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            String command = String.format("netstat -ano | findstr LISTENING | findstr %d", port);
            shell = new String[] {"cmd", "/c", command};
        } else {
            String command = String.format("netstat -nat | grep LISTEN | grep %d", port);
            shell = new String[] {"/bin/sh", "-c", command};
        }
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
