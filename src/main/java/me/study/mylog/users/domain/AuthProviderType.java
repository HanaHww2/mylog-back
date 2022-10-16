package me.study.mylog.users.domain;

import lombok.Getter;

@Getter
public enum AuthProviderType {
    DEFAULT, GOOGLE, NAVER, KAKAO, GITHUB
}