package cz.slanina.ts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.server.core.EvoInflectorLinkRelationProvider;

@SuppressWarnings("unused")
@SpringBootApplication
public class TemperatureServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemperatureServiceApplication.class, args);
    }

    @Bean
    EvoInflectorLinkRelationProvider relationProvider() {
        return new EvoInflectorLinkRelationProvider();
    }
}
