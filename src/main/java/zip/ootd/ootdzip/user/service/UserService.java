package zip.ootd.ootdzip.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.StyleRepository;
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
import zip.ootd.ootdzip.user.controller.response.ProfileRes;
import zip.ootd.ootdzip.user.controller.response.UserInfoForMyPageRes;
import zip.ootd.ootdzip.user.data.TokenUserInfoRes;
import zip.ootd.ootdzip.user.data.UserLoginReq;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserStyle;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.user.service.request.UserInfoForMyPageSvcReq;
import zip.ootd.ootdzip.user.service.request.UserRegisterSvcReq;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserOauthRepository userOAuthRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final List<SocialOAuth> socialOAuths;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final StyleRepository styleRepository;

    @Transactional
    public TokenInfo login(UserLoginReq request) {
        OauthProvider oAuthProvider = request.getOauthProvider();
        SocialOAuth socialOAuth = findSocialOauthByType(oAuthProvider);

        // request 정보로 소셜로그인 멤버 ID 가져오기
        String memberId = socialOAuth.getSocialIdBy(request.getAuthorizationCode(),
                request.getRedirectUri()); // 소셜로그인 멤버 ID
        Optional<UserOauth> foundUserOauth = userOAuthRepository.findUserOauthByOauthProviderAndOauthUserId(
                oAuthProvider, memberId);
        User user;
        if (foundUserOauth.isEmpty()) { // 새로운 멤버 ID일 경우 User 추가
            user = User.getDefault();
            user = userRepository.save(user);
            UserOauth userOauth = new UserOauth(user, oAuthProvider, memberId);
            userOAuthRepository.save(userOauth);
        } else { // 소셜로그인 멤버 ID가 이미 존재할 경우 불러오기
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

    public TokenUserInfoRes getUserInfo(User user) {
        return TokenUserInfoRes.of(user);
    }

    @Transactional
    public void register(UserRegisterSvcReq request, User loginUser) {

        if (loginUser.getIsCompleted()) {
            throw new CustomException(ErrorCode.ALREADY_USER_REGISTER);
        }

        List<Style> styles = styleRepository.findAllById(request.getStyles());

        if (styles.size() != request.getStyles().size()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SIZE_ID);
        }

        List<UserStyle> userStyles = UserStyle.createUserStylesBy(styles, loginUser);

        loginUser.registerBy(request.getName(),
                request.getGender(),
                request.getAge(),
                request.getHeight(),
                request.getWeight(),
                request.getIsBodyPrivate(),
                userStyles);

        userRepository.save(loginUser);
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

    public Set<User> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFollowers();
    }

    public Set<User> getFollowings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFollowings();
    }

    public boolean checkName(String name) {
        if (name == null || name.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
        }
        if (userRepository.findByName(name).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NAME_ERROR);
        }
        return true;
    }

    public User getAuthenticatiedUser() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new CustomException(ErrorCode.NOT_AUTHENTICATED_ERROR);
        }

        Optional<User> user = userRepository.findById(Long.valueOf(authentication.getName()));
        return user.orElseThrow();
    }

    public UserInfoForMyPageRes getUserInfoForMyPage(UserInfoForMyPageSvcReq request, User loginUser) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_ID));

        return UserInfoForMyPageRes.of(user, loginUser);
    }

    public ProfileRes getProfile(User loginUser) {
        return ProfileRes.of(loginUser);
    }
}
