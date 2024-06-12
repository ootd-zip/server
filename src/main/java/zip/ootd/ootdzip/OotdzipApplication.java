package zip.ootd.ootdzip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class OotdzipApplication {
    public static void main(String[] args) {
        SpringApplication.run(OotdzipApplication.class, args);
    }
}
