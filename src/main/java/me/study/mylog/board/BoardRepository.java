package me.study.mylog.board;

import me.study.mylog.users.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByUser(User user);

    @EntityGraph(attributePaths = "categories")
   // @Query("SELECT b, u FROM Board b JOIN b.user u WHERE u.email = :userEmail JOIN fetch b.categories")
    List<Board> findByUserEmail(String userEmail);
    boolean existsBoardByUri(String uri);
}
