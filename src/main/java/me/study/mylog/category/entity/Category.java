package me.study.mylog.category.entity;

import lombok.*;
import me.study.mylog.board.entity.Board;
import me.study.mylog.category.dto.CategoryModifyRequest;
import me.study.mylog.common.domain.BaseTimeEntity;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100)//, nullable = false)
    private String uri;

//    @Column(length = 100)
//    private String icon;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "USER_ID")
//    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PARENT_ID")
    private Category parent;

    @OneToMany(mappedBy="parent", fetch = FetchType.EAGER)
    private List<Category> children;

    public void modifyValue(CategoryModifyRequest dto) {
        this.name = dto.name();
    }

//    @OneToMany(mappedBy = "category")
//    private List<Post> postList;

}
