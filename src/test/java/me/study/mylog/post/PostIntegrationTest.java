package me.study.mylog.post;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.post.service.PostReadService;
import me.study.mylog.util.DatabaseCleanUp;
import me.study.mylog.util.InitDBService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@SpringBootTest
public class PostIntegrationTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final InitDBService initDBService;
    private final DatabaseCleanUp databaseCleanUp;

    private final PostReadService postReadService;

    @BeforeEach
    void setUp() {
        log.debug("---------------- setUP 실행 ------------------");
        initDBService.initDBForIntegration();

//        // com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
//        // No serializer found for class me.study.userservice.user.dto.UserDto$UserDtoBuilder and no properties discovered to create BeanSerializer
//        // (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)
//        // 아래의 코드로 해결, serializable을 상속받지 않아서 생기는 오류 같음.
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.afterPropertiesSet();
        databaseCleanUp.execute();
    }

    @DisplayName("조건에_맞춰_postList를_조회해_반환하는지_검증")
    @Test
    public void chkGetPostListWell() throws Exception {

        mockMvc.perform(get("/api/v1/posts/all?page=1&size=3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content.length()").value(3))
                .andExpect(jsonPath("$.result.content[0].content", containsString("content")))
                .andExpect(jsonPath("$.result.content[0].title", containsString("title")))
                .andDo(print());
    }
}
