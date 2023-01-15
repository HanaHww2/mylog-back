package me.study.mylog.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.util.DatabaseCleanUp;
import me.study.mylog.util.InitDBService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@AutoConfigureRestDocs
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@SpringBootTest
class CategoryIntegrationTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final InitDBService initDBService;
    private final DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp() {
        log.debug("---------------- setUP 실행 ------------------");
        initDBService.initDBForIntegration();

    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.afterPropertiesSet();
        databaseCleanUp.execute();
    }

    @Test
    void getCategoriesByBoard() {
    }

    @Test
    void getCategoryDetailByCategoryId() {
    }

    @Test
    void createNewCategory() {
    }

    @Test
    void modifyCategory() {
    }
}