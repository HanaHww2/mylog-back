package me.study.mylog.post.domain;

import lombok.*;
import me.study.mylog.board.Board;
import me.study.mylog.category.Category;
import me.study.mylog.common.domain.BaseTimeEntity;
import me.study.mylog.upload.domain.ImageFile;
import me.study.mylog.users.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
* 기본생성자의 접근 권한을 protected로 제한
* 프로젝트 코드상에서 기본생성자로 생성하는 것은 막되, JPA에서 Entity 클래스를 생성하는것은 허용하기 위함
* */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 500)
    private String hashtagList;

    @Builder.Default
    private Integer readingCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATE_ID") // default는 CATEGORY_ID
    private Category category;

    // 불필요한 이미지 삭제 스케쥴링시 활용 가능할 듯
    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<ImageFile> imageFileList = new ArrayList<>();

    public Integer addReadingCount() {
        return ++this.readingCount;
    };

//    @Builder /* 빌더패턴 클래스 생성, 생성자 상단에 선언시 생성자에 포함된 필드만 빌더에 포함 */
//    public Post(String title, String content, User user, Board board, Category category) {
//        this.title = title;
//        this.content = content;
//        this.user = user;
//        this.board = board;
//        this.category = category;
//    }
}