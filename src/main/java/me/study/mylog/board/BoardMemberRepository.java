package me.study.mylog.board;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

    @EntityGraph(attributePaths = "board")
    List<BoardMember> findAllByUserEmail(String userEmail);
}
