package me.study.mylog.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.study.mylog.auth.config.AuthProperties;
import me.study.mylog.auth.utils.CookieUtil;
import me.study.mylog.auth.utils.JwtUtil;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthProperties authProperties;
    private final JwtUtil jwtUtil;
    private final OAuth2CookieAuthorizationRequestRepository OAuth2AuthorizationRequestWithCookieRepository;

    //oauth2인증이 성공적으로 이뤄졌을 때 실행된다
    //token을 포함한 uri을 생성 후 인증요청 쿠키를 비워주고 redirect 한다.

    // DONE 리다이렉트 로직에 대해서 고민해보기
    // 리다이렉트를 수행하는 전략에서는 바디에 값을 담을 수 없음
    // 리다이렉트 페이지가 마지막 종착이 되므로, 바디는 그 페이지에서 가져오게 됨
    // 프론트에서 fetch/axios 등을 이용하면 리다이렉트가 자동으로 수행되지 않게 되므로, href 등을 이용하여 로직을 수행
    // 보편적인 경우를 고려하여 토큰은 쿠키에 담아 활용하는 전략으로 결정!
    // forwarding 고민 -> 스프링 서버 내에서 수행되는 로직이 추가로 필요한 경우라면 forwarding 전략을 활용할 수 있을 듯하다.

    // if 리다이렉트 없이 단순히 바디에 값을 담는다면,
//        ObjectMapper objectMapper = new ObjectMapper();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("accessToken", accessToken);
//        response.getWriter().write(objectMapper.writeValueAsString(map)); // 바디에 담기는지 확인

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //token을 생성하고 이를 포함한 프론트엔드로의 uri를 생성한다.
    //@SneakyThrows
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        Optional<String> redirectUri = CookieUtil.getCookie(request,
                        OAuth2AuthorizationRequestWithCookieRepository.REDIRECT_URI_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Unauthorized Redirect URI");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        jwtUtil.createRefreshToken(authentication, response);
        String accessToken = jwtUtil.createAccessToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .build().toUriString();
    }

    //인증정보를 요청한 uri 내역을 쿠키에서 삭제한다.
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequestWithCookieRepository.removeAuthorizationRequestCookies(request, response);
    }

    //application.properties에 등록해놓은 Redirect uri가 맞는지 확인한다. (app.redirect-uris)
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return authProperties.getOauth2()
                .getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);

                    // Only validate client host and port, especially when various client exist
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}