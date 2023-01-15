package me.study.mylog.users.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import me.study.mylog.board.entity.BoardMember;
import me.study.mylog.common.domain.BaseTimeEntity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유니크... 같은 메일이면서 다른 소셜 로그인 활용하는 경우는 없도록 한다.
    @Column(unique = true, length = 100)
    private String email;

    @Column(unique = true)
    private String oauthId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @NotNull // @NotEmpty 사용 불가
    private RoleType role;

    @Enumerated(EnumType.STRING)
    private AuthProviderType authProviderType;

    private UserStatus status;

    private String imageUrl;
    private String githubUrl;
    private String blogUrl;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<BoardMember> boardList = new ArrayList<>();

    // 사용자 상태 - 수정 필요
    @PrePersist
    public void prePersist() {
        this.status = this.status == null ? UserStatus.NORMAL : this.status;
    }

    public User update(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        return this;
    }

    public String getRoleCode() {
        return this.role.getCode();
    }

}