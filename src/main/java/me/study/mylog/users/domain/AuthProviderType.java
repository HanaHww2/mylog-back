package me.study.mylog.users.domain;

import lombok.Getter;

@Getter
public enum AuthProviderType {
    LOCAL, GOOGLE, NAVER, KAKAO, GITHUB
}