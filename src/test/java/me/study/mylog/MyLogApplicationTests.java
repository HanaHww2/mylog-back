package me.study.mylog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest /*SpringBoot에서의 테스트 코드는 메모리 DB인 H2를 기본적으로 사용한다. 따로 설정할 필요가 없다.*/
class MyLogApplicationTests {

    @Test
    void contextLoads() {
    }

}
