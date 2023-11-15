package zip.ootd.ootdzip.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.ResponseStatus;

import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.oauth.data.TokenInfo;
import zip.ootd.ootdzip.oauth.domain.OauthProvider;
import zip.ootd.ootdzip.oauth.domain.RefreshToken;
import zip.ootd.ootdzip.oauth.domain.UserAuthenticationToken;
import zip.ootd.ootdzip.oauth.domain.UserOauth;
import zip.ootd.ootdzip.oauth.repository.RefreshTokenRepository;
import zip.ootd.ootdzip.oauth.repository.UserOauthRepository;
import zip.ootd.ootdzip.oauth.service.SocialOAuth;
import zip.ootd.ootdzip.security.JwtUtils;
import zip.ootd.ootdzip.user.data.UserLoginReq;
import zip.ootd.ootdzip.user.data.UserRegisterReq;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserOauthRepository userOAuthRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final List<SocialOAuth> socialOAuths;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public TokenInfo login(UserLoginReq request) {
        OauthProvider oAuthProvider = request.getOauthProvider();
        SocialOAuth socialOAuth = findSocialOauthByType(oAuthProvider);

        // request 정보로 카카오 멤버 ID 가져오기
        String memberId = socialOAuth.getSocialIdBy(request.getAuthorizationCode(),
                request.getRedirectUri()); // 카카오 멤버 ID
        Optional<UserOauth> foundUserOauth = userOAuthRepository.findUserOauthByOauthProviderAndOauthUserId(
                OauthProvider.KAKAO, memberId);
        User user;
        if (foundUserOauth.isEmpty()) { // 새로운 멤버 ID일 경우 User 추가, 카카오 로그인 연동
            user = User.getDefault();
            user = userRepository.save(user);
            UserOauth userOauth = new UserOauth(user, OauthProvider.KAKAO, memberId);
            userOAuthRepository.save(userOauth);
        } else { // 카카오 멤버 ID가 이미 존재할 경우 불러오기
            user = foundUserOauth.get().getUser();
        }
        UserAuthenticationToken authenticationToken = new UserAuthenticationToken(
                new UserAuthenticationToken.UserDetails(user.getId()));
        TokenInfo tokenInfo = jwtUtils.buildTokenInfo(authenticationToken);
        // 리프레시 토큰 DB에 저장
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.save(new RefreshToken(user,
                tokenInfo.getRefreshToken(),
                now.plusSeconds(tokenInfo.getRefreshTokenExpiresIn()),
                false));
        return tokenInfo;
    }

    private SocialOAuth findSocialOauthByType(OauthProvider oAuthProvider) {
        return socialOAuths.stream()
                .filter(s -> s.type() == oAuthProvider)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NONE_SOCIAL_ERROR));
    }

    @Transactional
    public void register(UserRegisterReq request) {
        String idString = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> found = userRepository.findById(Long.parseLong(idString));
        User user = found.orElseThrow(() -> new IllegalStateException("404")); // TODO : 적절한 Exception 정의해서 사용
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

    @Transactional
    public TokenInfo refresh(String refreshToken) {
        // refresh token white-list로 관리
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() ->
                new IllegalStateException("401")); // TODO : 적절한 Exception 정의해서 사용
        Authentication decoded = jwtUtils.decode(token.getToken());
        if (decoded == null) { // 잘못되었거나 기한이 지난 jwt
            throw new IllegalStateException("403"); // TODO : 적절한 Exception 정의해서 사용
        }
        refreshTokenRepository.delete(token); // 이전 refresh token invalidate
        User user = token.getUser();
        TokenInfo tokenInfo = jwtUtils.buildTokenInfo(decoded);
        // 리프레시 토큰 DB에 저장
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.save(new RefreshToken(user,
                tokenInfo.getRefreshToken(),
                now.plusSeconds(tokenInfo.getRefreshTokenExpiresIn()),
                false));
        return tokenInfo;
    }

    @Transactional
    public boolean follow(Long userId, Long followerId) {
        User user = userRepository.findById(userId).orElseThrow();
        User follower = userRepository.findById(followerId).orElseThrow();
        return user.addFollower(follower);
    }

    @Transactional
    public boolean unfollow(Long userId, Long followerId) {
        User user = userRepository.findById(userId).orElseThrow();
        User follower = userRepository.findById(followerId).orElseThrow();
        return user.removeFollower(follower);
    }

    @Transactional
    public Set<User> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFollowers();
    }

    @Transactional
    public Set<User> getFollowings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFollowings();
    }

    public boolean checkId(String name) {
        return userRepository.findByName(name).isPresent();
    }

    public User getAuthenticatiedUser() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new CustomException(ErrorCode.NOT_AUTHENTICATED_ERROR);
        }

        Optional<User> user = userRepository.findById(Long.valueOf(authentication.getName()));
        return user.orElseThrow();
    }
}
