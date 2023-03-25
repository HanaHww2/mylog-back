package me.study.mylog.comment.entity;

import lombok.*;
import me.study.mylog.board.entity.Board;
import me.study.mylog.comment.dto.ModifyCommentRequest;
import me.study.mylog.common.domain.BaseEntity;
import me.study.mylog.post.entity.Post;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String writerName;

    @Column(name="USER_ID")
    private Long userId;
    @Column(name="BOARD_ID")
    private Long boardId;
    @Column(name="POST_ID")
    private Long postId;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    private User user;
    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", insertable=false, updatable=false)
    private Board board;
    @ManyToOne
    @JoinColumn(name = "POST_ID", insertable=false, updatable=false)
    private Post post;

    @Column(name="PARENT_ID")
    private Long parentCommentId;
    @ManyToOne(targetEntity = Comment.class, fetch = FetchType.LAZY)
    @JoinColumn(name="PARENT_ID", insertable=false, updatable=false)
    private Comment parentComment;

    @Builder.Default
    @Column(name="IS_DELETED")
    private Boolean isDeleted = Boolean.FALSE;

    // TODO
//    @Builder.Default
//    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY)
//    private List<ImageFile> imageFileList = new ArrayList<>();


    public void softDelete() { this.isDeleted = Boolean.TRUE; }

    public void modifyValue(ModifyCommentRequest dto) {
        this.content = dto.getContent();
        this.parentCommentId = dto.getParentCommentId();
        //TODO super.lastModifiedBy = userId;
    }

}