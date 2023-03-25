package me.study.mylog.post.entity;

import lombok.*;
import me.study.mylog.board.entity.Board;
import me.study.mylog.category.entity.Category;
import me.study.mylog.common.domain.BaseEntity;
import me.study.mylog.common.jpaConverter.StringSetConverter;
import me.study.mylog.post.dto.ModifyPostRequest;
import me.study.mylog.post.dto.PostMainResponse;
import me.study.mylog.upload.domain.ImageFile;
import me.study.mylog.users.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/*
* 기본생성자의 접근 권한을 protected로 제한
* 프로젝트 코드상에서 기본생성자로 생성하는 것은 막되, JPA에서 Entity 클래스를 생성하는것은 허용하기 위함
* */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    @Column(name = "TAGS", length = 500)
    @Convert(converter = StringSetConverter.class)
    private Set<String> hashtagList = Collections.EMPTY_SET;

    // 불필요한 이미지 삭제 스케쥴링시 활용 가능할 듯
    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<ImageFile> imageFileList = new ArrayList<>();

    @Builder.Default
    private Long views = 0L;

    @Column(name="USER_ID")
    private Long userId;
    @Column(name="BOARD_ID")
    private Long boardId;
    @Column(name="CATE_ID")
    private Long categoryId;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    private User user;
    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", insertable=false, updatable=false)
    private Board board;
    @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "CATE_ID", insertable=false, updatable=false) // default는 CATEGORY_ID
    private Category category;

    @Builder.Default
    @Column(name="IS_DELETED")
    private Boolean isDeleted = Boolean.FALSE;

    public void softDelete() { this.isDeleted = Boolean.TRUE; }

    public Long addViewCount() {
        return ++this.views;
    };

    public void modifyValue(ModifyPostRequest dto, Long userId) {
        this.categoryId = dto.getCategoryId();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.hashtagList = dto.getTagList();
        //TODO super.lastModifiedBy = userId;
    }

    public PostMainResponse toDto() {
        return PostMainResponse.builder()
                //.authorName(this.)
                .boardId(this.boardId)
                .categoryId(this.categoryId)
                .title(this.title)
                .content(this.content)
                .hashtagList(this.hashtagList)
                .modifiedAt(this.getModifiedAt())
                .build();
    }

//    @Builder /* 빌더패턴 클래스 생성, 생성자 상단에 선언시 생성자에 포함된 필드만 빌더에 포함 */
//    public Post(String title, String content, User user, Board board, Category category) {
//        this.title = title;
//        this.content = content;
//        this.user = user;
//        this.board = board;
//        this.category = category;
//    }
}