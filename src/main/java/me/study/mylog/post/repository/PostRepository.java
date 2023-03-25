package me.study.mylog.post.repository;

import me.study.mylog.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByBoardId(Long boardId, Pageable pageable);
    Page<Post> findAllByCategoryId(Long categoryId, Pageable pageable);

    Optional<Post> findByIdAndBoardId(Long postId, Long postId1);
}
