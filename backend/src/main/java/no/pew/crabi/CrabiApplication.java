package no.pew.crabi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"no.pew.crabi", "org.openapitools.configuration"})
public class CrabiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrabiApplication.class, args);
    }

}
