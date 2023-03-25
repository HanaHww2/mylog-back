package me.study.mylog.comment.repository;

import me.study.mylog.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    Page<Comment> findAllByBoardId(Long boardId, Pageable pageable);
}
