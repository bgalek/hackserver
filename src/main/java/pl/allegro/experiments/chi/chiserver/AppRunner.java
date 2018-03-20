package pl.allegro.experiments.chi.chiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class AppRunner {
    public static void main(String[] args) {
        SpringApplication.run(AppRunner.class, args);
    }
}
