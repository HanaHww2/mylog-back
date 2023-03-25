package me.study.mylog.board.repository;

import me.study.mylog.board.entity.BoardMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

    List<BoardMember> findAllByUserId(Long userId);
    @EntityGraph(attributePaths = "board")
    List<BoardMember> findAllByUserEmail(String userEmail);

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
    Optional<BoardMember> findByBoardIdAndUserId(Long boardId, Long userId);
}
