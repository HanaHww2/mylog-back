package me.study.mylog.category.repository;

import me.study.mylog.category.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByBoardIdOrderByCreatedAt(Long boardId);

    @EntityGraph(attributePaths = "children")
    List<Category> findByBoardId(Long boardId);
    Optional<Category> findByIdAndBoardId(Long categoryId, Long boardId);
}
