package me.study.mylog.board.entity;

import lombok.*;
import me.study.mylog.board.dto.ModifyBoardRequest;
import me.study.mylog.category.entity.Category;
import me.study.mylog.common.domain.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    private String uri;

    @Column(length = 100)
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private BoardType boardType;

    private Long views;

//    @OneToOne(fetch = FetchType.LAZY)
//    private User user;

    @OneToMany(mappedBy="board", fetch = FetchType.LAZY) // Lazy 전략(디폴트)
    private List<Category> categories;

    @Builder.Default
    @OneToMany(mappedBy = "board")
    private List<BoardMember> boardMembers = new ArrayList<>();

    @Builder /* 빌더패턴 클래스 생성, 생성자 상단에 선언시 생성자에 포함된 필드만 빌더에 포함 */
    public Board(String name, String uri) {
        this.name = name;
        this.uri = uri;
//        this.user = user;
    }

    public void modifyValues(ModifyBoardRequest modifyBoardRequest) {
        this.name = modifyBoardRequest.name();
        this.uri = "/" + modifyBoardRequest.name();
        this.icon = modifyBoardRequest.icon();
        this.boardType = modifyBoardRequest.boardType();
    }
}
