package me.study.mylog.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.study.mylog.common.domain.BaseEntity;
import me.study.mylog.users.domain.User;

import javax.persistence.*;

// GroupMember
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class BoardMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private BoardMemberType boardMemberType;

    @Column(length = 50)
    private String nickname;

    @PrePersist
    public void prePersist() {
        this.boardMemberType = this.boardMemberType == null ? BoardMemberType.GENERAL : this.boardMemberType;
    }

    @Builder /* 빌더패턴 클래스 생성, 생성자 상단에 선언시 생성자에 포함된 필드만 빌더에 포함 */
    public BoardMember(User user, String nickname, Board board, BoardMemberType boardMemberType) {
        this.user = user;
        this.board = board; //setBoardRelation(board)
        this.boardMemberType = boardMemberType;
        if (nickname==null) nickname = user.getName();
        this.nickname = nickname;
    }

}
