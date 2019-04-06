package pl.allegro.tech.leaders.hackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class HackathonServer {

    public static void main(String[] args) {
        System.getProperties().setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(HackathonServer.class, args);
    }
}

