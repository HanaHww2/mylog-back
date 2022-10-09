package me.study.mylog.board;

import me.study.mylog.users.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {
    //List<Board> findByUser(User user);

    //@EntityGraph(attributePaths = "categories") LEFT JOIN fetch b.categories
    @Query("SELECT b FROM Board b JOIN fetch b.boardMembers m WHERE m.user = :user ")
    List<Board> findByUser(User user);
    boolean existsBoardByUri(String uri);
}
