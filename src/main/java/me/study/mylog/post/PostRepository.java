package me.study.mylog.post;

import me.study.mylog.post.domain.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByBoardId(Long boardId, Pageable pageable);
    List<Post> findAllByCategoryId(Long categoryId, Pageable pageable);

}
