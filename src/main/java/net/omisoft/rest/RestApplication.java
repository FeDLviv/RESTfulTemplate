package net.omisoft.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cache.annotation.EnableCaching;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableCaching
public class RestApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RestApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        springApplication.run(args);
    }

}
