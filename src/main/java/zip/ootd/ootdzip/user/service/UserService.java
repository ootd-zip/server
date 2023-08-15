package zip.ootd.ootdzip.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import zip.ootd.ootdzip.oauth.data.KakaoOauthTokenRes;
import zip.ootd.ootdzip.oauth.data.TokenInfo;
import zip.ootd.ootdzip.oauth.domain.OauthProvider;
import zip.ootd.ootdzip.oauth.domain.RefreshToken;
import zip.ootd.ootdzip.oauth.domain.UserAuthenticationToken;
import zip.ootd.ootdzip.oauth.domain.UserOauth;
import zip.ootd.ootdzip.oauth.repository.RefreshTokenRepository;
import zip.ootd.ootdzip.oauth.repository.UserOauthRepository;
import zip.ootd.ootdzip.oauth.service.KakaoOAuthUtils;
import zip.ootd.ootdzip.security.JwtUtils;
import zip.ootd.ootdzip.user.data.UserLoginReq;
import zip.ootd.ootdzip.user.data.UserRegisterReq;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserOauthRepository userOAuthRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final KakaoOAuthUtils kakaoOauthUtils;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public TokenInfo login(UserLoginReq request) {
        if (!request.getOauthProvider().equalsIgnoreCase("KAKAO")) {
            throw new IllegalArgumentException("Not implemented Oauth provider: " + request.getOauthProvider());
        }
        // request 정보로 카카오 멤버 ID 가져오기
        KakaoOauthTokenRes response = kakaoOauthUtils.requestTokenByAuthorizationCode(request.getAuthorizationCode(), request.getRedirectUri());
        Long memberId = kakaoOauthUtils.requestAccessTokenMemberId(response.getAccess_token()); // 카카오 멤버 ID
        UserOauth userOAuth = userOAuthRepository.findUserOauthByOauthProviderAndOauthUserId(OauthProvider.KAKAO, String.valueOf(memberId));
        User user;
        if (userOAuth == null) { // 새로운 멤버 ID일 경우 User 추가, 카카오 로그인 연동
            user = User.getDefault();
            user = userRepository.save(user);
            userOAuth = new UserOauth(user, OauthProvider.KAKAO, String.valueOf(memberId));
            userOAuthRepository.save(userOAuth);
        } else { // 카카오 멤버 ID가 이미 존재할 경우 불러오기
            user = userOAuth.getUser();
        }
        UserAuthenticationToken authenticationToken = new UserAuthenticationToken(new UserAuthenticationToken.UserDetails(user.getId()));
        TokenInfo tokenInfo = jwtUtils.buildTokenInfo(authenticationToken);
        // 리프레시 토큰 DB에 저장
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.save(new RefreshToken(user,
                tokenInfo.getRefreshToken(),
                now.plusSeconds(tokenInfo.getRefreshTokenExpiresIn()),
                false));
        return tokenInfo;
    }

    @Transactional
    public void register(UserRegisterReq request) {
        String idString = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> u = userRepository.findById(Long.parseLong(idString));
        User user = u.orElseThrow(() -> new IllegalStateException("404")); // TODO : 적절한 Exception 정의해서 사용
        if (user.getIsDeleted()) {
            throw new IllegalStateException("404"); // TODO : 적절한 Exception 정의해서 사용
        }
        if (user.getIsCompleted()) {
            throw new IllegalStateException("409"); // TODO : 적절한 Exception 정의해서 사용
        }
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setBirthdate(request.getBirthdate());
        user.setHeight(request.getHeight());
        user.setShowHeight(request.getShowHeight());
        user.setWeight(request.getWeight());
        user.setShowHeight(request.getShowHeight());
        user.setIsCompleted(true);
        userRepository.save(user);
    }
}
