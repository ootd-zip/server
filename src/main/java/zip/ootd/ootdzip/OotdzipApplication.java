package zip.ootd.ootdzip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        servers = {
                @Server(url = "/", description = "Default Server Url")
        }
)
@EnableCaching
@SpringBootApplication
public class OotdzipApplication {
    public static void main(String[] args) {
        SpringApplication.run(OotdzipApplication.class, args);
    }
}
