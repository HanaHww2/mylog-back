package me.study.mylog.board.repository;

import me.study.mylog.board.entity.Board;
import me.study.mylog.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BoardRepository extends JpaRepository<Board, Long> {

    //@EntityGraph(attributePaths = "categories") LEFT JOIN fetch b.categories
    @Query("SELECT b FROM Board b JOIN fetch b.boardMembers m WHERE m.user = :user ")
    List<Board> findByUser(User user);

    Optional<Board> findByUri(String boardUri);

    boolean existsBoardByUri(String boardUri);

}
