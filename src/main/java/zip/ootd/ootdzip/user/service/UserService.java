package zip.ootd.ootdzip.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import zip.ootd.ootdzip.oauth.data.KakaoOAuthTokenRes;
import zip.ootd.ootdzip.oauth.data.TokenInfo;
import zip.ootd.ootdzip.oauth.domain.OAuthProvider;
import zip.ootd.ootdzip.oauth.domain.UserAuthenticationToken;
import zip.ootd.ootdzip.oauth.domain.UserOAuth;
import zip.ootd.ootdzip.oauth.repository.UserOAuthRepository;
import zip.ootd.ootdzip.oauth.service.KakaoOAuthUtils;
import zip.ootd.ootdzip.security.JwtUtils;
import zip.ootd.ootdzip.user.data.UserLoginReq;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;
import zip.ootd.ootdzip.user.repository.UserRepository;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserOAuthRepository userOAuthRepository;
    private final JwtUtils jwtUtils;
    private final KakaoOAuthUtils kakaoOauthUtils;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public TokenInfo login(UserLoginReq request) {
        if (!request.getOauthProvider().equalsIgnoreCase("KAKAO")) {
            throw new IllegalArgumentException("Not implemented Oauth provider: " + request.getOauthProvider());
        }
        KakaoOAuthTokenRes response = kakaoOauthUtils.requestTokenByAuthorizationCode(request.getAuthorizationCode(), request.getRedirectUri());
        Long memberId = kakaoOauthUtils.requestAccessTokenMemberId(response.getAccess_token()); // 카카오 멤버 ID
        UserOAuth userOAuth = userOAuthRepository.findUserOAuthByOAuthProviderAndOAuthUserId(OAuthProvider.KAKAO, String.valueOf(memberId));
        User user;
        if (userOAuth == null) { // User 추가, 카카오 로그인 연동
            user = User.getDefault();
            user = userRepository.save(user);
            userOAuth = new UserOAuth(user, OAuthProvider.KAKAO, String.valueOf(memberId));
            userOAuthRepository.save(userOAuth);
        } else { // 이미 존재할 경우 불러오기
            user = userOAuth.getUser();
        }
        UserAuthenticationToken authenticationToken = new UserAuthenticationToken(new UserAuthenticationToken.UserDetails(user.getId()));
        // TODO: 리프레시 토큰 DB에 저장
        return jwtUtils.buildTokenInfo(authenticationToken);
    }

}
