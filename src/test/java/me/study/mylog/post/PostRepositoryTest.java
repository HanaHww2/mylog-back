package me.study.mylog.post;

import me.study.mylog.post.domain.Post;
import me.study.mylog.users.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest /*SpringBoot에서의 테스트 코드는 메모리 DB인 H2를 기본적으로 사용한다. 따로 설정할 필요가 없다.*/
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    void cleanup() {
        /*
        * 테스트 코드에 영향을 끼치지 않기 위해
        * 테스트 메소드가 끝날 때마다 repository 전체를 비우는 코드
        * */
        postRepository.deleteAll();
    }

    @Test
    @DisplayName(value = "기록_저장이_잘_수행되는지_검증")
    void checkSaveLog() {
        // given
        postRepository.save(Post.builder()
                .title("테스트 게시글")
                .content("테스트 본문")
                .user(userRepository.findByEmail("test@example.com").get())
                .build());
        // when
        List<Post> postList = postRepository.findAll();

        // then
        Post post = postList.get(0);
        assert(post.getTitle()).equals("테스트 게시글");
        assert(post.getContent()).equals("테스트 본문");
    }

    @Test
    @DisplayName(value="엔티티에_시간_저장이_잘_수행되는지_검증")
    void checkSaveByBaseTimeEntity() {
        //given
        LocalDateTime now = LocalDateTime.now();
        postRepository.save(Post.builder()
                .title("테스트 게시글")
                .content("테스트 본문")
                .user(userRepository.findByEmail("test@example.com").get())
                .build());
        //when
        List<Post> postList = postRepository.findAll();

        //then
        Post post = postList.get(0);
        Assertions.assertThat(post.getCreatedDate()).isAfterOrEqualTo(now);
        Assertions.assertThat(post.getModifiedDate()).isAfterOrEqualTo(now);
    }
}