package me.study.mylog;

import me.study.mylog.auth.config.AuthProperties;
import me.study.mylog.common.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@EnableConfigurationProperties({AuthProperties.class, CorsProperties.class})
@SpringBootApplication
public class MyLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyLogApplication.class, args);
    }

}
