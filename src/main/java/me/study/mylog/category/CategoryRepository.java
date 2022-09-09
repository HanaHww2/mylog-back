package me.study.mylog.category;

import me.study.mylog.users.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);

    @EntityGraph(attributePaths = "children")
    List<Category> findByBoardId(Long boardId);

    List<Category> findByUserEmail(String userEmail);
}
