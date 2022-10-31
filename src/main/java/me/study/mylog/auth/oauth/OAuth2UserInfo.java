package me.study.mylog.auth.oauth;

import me.study.mylog.users.domain.AuthProviderType;
import me.study.mylog.users.domain.RoleType;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.domain.UserStatus;

import java.util.Map;
import java.util.UUID;

public abstract class OAuth2UserInfo {
  Map<String, Object> attributes;

  public OAuth2UserInfo(Map<String, Object> attributes) {
      this.attributes = attributes;
  }

  public Map<String, Object> getAttributes() {
      return attributes;
  }

  public abstract AuthProviderType getProviderType();

  public abstract String getId();

  public abstract String getName();

  public abstract String getEmail();

  public abstract String getImageUrl();

  public User toEntity(String tempName) {

    return User.builder()
            .name(tempName) // 유니크 속성을 위해 선검증 후, 중복이 있다면 난수를 붙인 값
            .nickname(getName())
            .email(getEmail())
            .imageUrl(getImageUrl())
            .role(RoleType.USER)
            .status(UserStatus.Normal)
            .authProviderType(getProviderType())
            .build();
  }
}