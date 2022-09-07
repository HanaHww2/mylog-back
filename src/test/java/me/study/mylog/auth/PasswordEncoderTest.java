package me.study.mylog.auth;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {
    Logger logger = LoggerFactory.getLogger(PasswordEncoderTest.class);
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void passwordEncode() throws Exception{
        logger.debug(">>>>>>>>>>>>>>>>>>>encoded: {}", passwordEncoder.encode("1234"));
        boolean matches = passwordEncoder.matches("1234", passwordEncoder.encode("1234"));
        logger.debug(">>>>>>>>>>>>>>>>>>>result: {}", matches);
    }
}
