package zip.ootd.ootdzip.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"zip.ootd.ootdzip"})
public class OpenFeignConfig {
}
