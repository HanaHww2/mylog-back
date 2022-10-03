package me.study.mylog.auth.oauth;



import me.study.mylog.users.domain.AuthProviderType;
import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProviderType authProvider, Map<String, Object> attributes) {
        switch (authProvider) {
            case GOOGLE: return ofGoogle(attributes);
            //case GITHUB: return new GithubOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .imageUrl((String) attributes.get("picture"))
                .attributes(attributes)
                .build();
    }

}
