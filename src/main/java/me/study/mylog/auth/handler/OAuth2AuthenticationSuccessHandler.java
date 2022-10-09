package me.study.mylog.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.study.mylog.auth.properties.AuthProperties;
import me.study.mylog.auth.security.OAuth2CookieAuthorizationRequestRepository;
import me.study.mylog.auth.utils.CookieUtil;
import me.study.mylog.auth.utils.JwtUtil;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.security.core.Authentication;
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
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthProperties authProperties;
    private final JwtUtil jwtUtil;
//    private final OAuth2CookieAuthorizationRequestRepository OAuth2AuthorizationRequestWithCookieRepository;


    //oauth2인증이 성공적으로 이뤄졌을 때 실행된다
    //token을 포함한 uri을 생성 후 인증요청 쿠키를 비워주고 redirect 한다.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        //clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //token을 생성하고 이를 포함한 프론트엔드로의 uri를 생성한다.
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        Optional<String> redirectUri = Optional.ofNullable(request.getParameter("redirect_uri"));
//        Optional<String> redirectUri = CookieUtil.getCookie(request,
//                        OAuth2AuthorizationRequestWithCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
//                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());


        jwtUtil.createRefreshToken(authentication, response);
        String accessToken = jwtUtil.createAccessToken(authentication);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("accessToken", accessToken);

        try {
            String accessTokenPayLoad = objectMapper.writeValueAsString(tempMap);
            response.getOutputStream().print(accessTokenPayLoad);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetUrl;
//        return UriComponentsBuilder.fromUriString(targetUrl)
//                .queryParam("accessToken", accessToken)
//                .build().toUriString();
    }

    //인증정보 요청 내역을 쿠키에서 삭제한다.
//    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
//        OAuth2AuthorizationRequestWithCookieRepository.removeAuthorizationRequestCookies(request, response);
//    }

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