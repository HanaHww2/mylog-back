package me.study.mylog.users.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import me.study.mylog.board.domain.BoardMember;
import me.study.mylog.common.domain.BaseTimeEntity;
import org.hibernate.annotations.CollectionId;

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

    @Column(unique = true, length = 100) // 유니크... 같은 메일이면서 다른 소셜 로그인 활용하는 경우라면?
    private String email;

    @Column(unique = true) // 유니크... 같은 메일이면서 다른 소셜 로그인 활용하는 경우라면?
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

    @Column
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private AuthProviderType authProviderType;

    private UserStatus status; // 사용자 상태 - 수정 필요
    private String githubUrl;
    private String blogUrl;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<BoardMember> boardList = new ArrayList<>();

//    @PrePersist
//    public void prePersist() {
//        this.status = this.status == null ? UserStatus.Normal : this.status;
//    }

    public User update(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        return this;
    }

    public String getRoleCode() {
        return this.role.getCode();
    }

}