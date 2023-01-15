package me.study.mylog.category.entity;

import lombok.*;
import me.study.mylog.board.entity.Board;
import me.study.mylog.category.dto.CategoryModifyRequest;
import me.study.mylog.common.domain.BaseTimeEntity;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

//    @Builder /* 빌더패턴 클래스 생성, 생성자 상단에 선언시 생성자에 포함된 필드만 빌더에 포함 */
//    public Category(String name, User user, Board board) {
//        this.name = name;
//        this.board = board;
//        this.user = user;
//    }
}
