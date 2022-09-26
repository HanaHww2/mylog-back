package me.study.mylog;

import me.study.mylog.auth.properties.AuthProperties;
import me.study.mylog.common.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;


@EnableConfigurationProperties({AuthProperties.class, CorsProperties.class})
@SpringBootApplication
public class MyLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyLogApplication.class, args);
    }

}
