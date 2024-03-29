package webServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = { "webServer", "database", "integration"})
public class main {

    public static void main(String[] args) {
        SpringApplication.run(main.class, args);
    }

}
