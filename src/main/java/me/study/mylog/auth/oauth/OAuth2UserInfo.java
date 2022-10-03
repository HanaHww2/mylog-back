package me.study.mylog.auth.oauth;

import lombok.Builder;
import me.study.mylog.users.domain.RoleType;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.domain.UserStatus;

import java.util.Map;

public class OAuth2UserInfo {

    Map<String, Object> attributes;
    String nameAttributeKey;
    String id;
    String name;
    String email;
    String imageUrl;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes () { return attributes; }

    public String getId () { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public String getImageUrl() { return imageUrl; }

    @Builder
    public OAuth2UserInfo(Map<String, Object> attributes, String nameAttributeKey, String name,
                           String email, String imageUrl) {
        this.attributes = attributes;
        this.nameAttributeKey= nameAttributeKey;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .imageUrl(imageUrl)
                .role(RoleType.USER)
                .status(UserStatus.Normal)
                .build();
    }

//    /* 구현체에 객체 생성 처리를 맡긴다. 팩토리 패턴이라고 볼 수 있나? */
//    public abstract void createUserInfo(AuthProvider authProvider);
//    public OAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
//        this.attributes = attributes;
//        createUserInfo(authProvider);
//    }
}
