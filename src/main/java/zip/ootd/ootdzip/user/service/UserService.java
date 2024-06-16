package zip.ootd.ootdzip.user.service;

import static zip.ootd.ootdzip.common.exception.code.ErrorCode.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.notification.event.NotificationEvent;
import zip.ootd.ootdzip.oauth.service.UserSocialLoginService;
import zip.ootd.ootdzip.user.controller.response.CommonPageResponseForUserSearch;
import zip.ootd.ootdzip.user.controller.response.ProfileRes;
import zip.ootd.ootdzip.user.controller.response.UserInfoForMyPageRes;
import zip.ootd.ootdzip.user.controller.response.UserSearchRes;
import zip.ootd.ootdzip.user.controller.response.UserStyleRes;
import zip.ootd.ootdzip.user.data.CheckNameReq;
import zip.ootd.ootdzip.user.data.FollowReq;
import zip.ootd.ootdzip.user.data.UserSearchType;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserStyle;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.user.repository.UserStyleRepository;
import zip.ootd.ootdzip.user.service.request.ProfileSvcReq;
import zip.ootd.ootdzip.user.service.request.UserInfoForMyPageSvcReq;
import zip.ootd.ootdzip.user.service.request.UserRegisterSvcReq;
import zip.ootd.ootdzip.user.service.request.UserSearchSvcReq;
import zip.ootd.ootdzip.user.service.request.UserStyleUpdateSvcReq;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserSocialLoginService userSocialLoginService;
    private final UserRepository userRepository;
    private final StyleRepository styleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserStyleRepository userStyleRepository;
    private final EntityManager em;
    private final UserBlockRepository userBlockRepository;

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

    public long getUserId() {
        User currentUser = getAuthenticatiedUser();
        return currentUser.getId();
    }

    public String getUserSocialLoginProvider() {
        User currentUser = getAuthenticatiedUser();
        return userSocialLoginService.getUserProvider(currentUser);
    }

    @Transactional
    public boolean follow(Long userId, Long followerId) {
        User user = userRepository.findById(userId).orElseThrow();
        User follower = userRepository.findById(followerId).orElseThrow();
        notifyFollow(user, follower, follower.getId());
        return user.addFollower(follower);
    }

    private void notifyFollow(User receiver, User sender, Long id) {

        eventPublisher.publishEvent(NotificationEvent.builder()
                .receiver(receiver)
                .sender(sender)
                .notificationType(NotificationType.FOLLOW)
                .goUrl("mypage/" + id)
                .build());
    }

    @Transactional
    public boolean unfollow(Long userId, Long followerId) {
        User user = userRepository.findById(userId).orElseThrow();
        User follower = userRepository.findById(followerId).orElseThrow();
        return user.removeFollower(follower);
    }

    @Transactional
    public boolean removeFollower(User loginUser, FollowReq request) {
        User follower = userRepository.findById(request.getUserId()).orElseThrow();
        return loginUser.removeFollower(follower);
    }

    public boolean checkName(CheckNameReq req) {
        try {
            // 현재 사용자 닉네임과 동일한 닉네임일 경우 허용
            User user = getAuthenticatiedUser();
            if (req.getName().equals(user.getName())) {
                return true;
            }
        } catch (CustomException ignored) {
            // 비로그인 상태
        }
        return userRepository.findByName(req.getName()).isEmpty();
    }

    public User getAuthenticatiedUser() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new CustomException(ErrorCode.NOT_AUTHENTICATED_ERROR);
        }

        Optional<User> user = userRepository.findById(Long.valueOf(authentication.getName()));

        if (user.isPresent() && user.get().getIsDeleted()) {
            throw new CustomException(DELETED_USER_ERROR);
        }

        return user.orElseThrow();
    }

    public void checkValidUser(User loginUser, User targetUser) {
        if (!loginUser.equals(targetUser)) {
            throw new CustomException(UNAUTHORIZED_USER_ERROR);
        }
    }

    public void checkValidUser(User targetUser) {
        if (!getAuthenticatiedUser().equals(targetUser)) {
            throw new CustomException(UNAUTHORIZED_USER_ERROR);
        }
    }

    public UserInfoForMyPageRes getUserInfoForMyPage(UserInfoForMyPageSvcReq request, User loginUser) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_ID));

        if (user.getIsDeleted()) {
            throw new CustomException(DELETED_USER_ERROR);
        }

        return UserInfoForMyPageRes.of(user, loginUser);
    }

    public ProfileRes getProfile(User loginUser) {
        return ProfileRes.of(loginUser);
    }

    @Transactional
    public void updateProfile(ProfileSvcReq request, User loginUser) {

        loginUser.updateProfile(request.getName(),
                request.getProfileImage(),
                request.getDescription(),
                request.getHeight(),
                request.getWeight(),
                request.getIsBodyPrivate());

        userRepository.save(loginUser);
    }

    public CommonPageResponse<UserSearchRes> searchUser(UserSearchSvcReq request, User loginUser) {

        UserSearchType userSearchType = request.getUserSearchType();
        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(loginUser.getId());

        Page<User> findUsers = userRepository.searchUsers(
                userSearchType,
                request.getName(),
                request.getUserId(),
                nonAccessibleUserIds,
                request.getPageable()
        );

        List<UserSearchRes> result = findUsers.stream()
                .map((item) -> UserSearchRes.of(item, loginUser))
                .toList();

        return new CommonPageResponseForUserSearch<>(result, request.getPageable(), findUsers.isLast(),
                findUsers.getTotalElements(), loginUser.getFollowerCount(), loginUser.getFollowingCount());
    }

    public List<UserStyleRes> getUserStyle(User loginUser) {

        List<UserStyle> userStyles = userStyleRepository.findAllByUser(loginUser);

        return userStyles.stream()
                .map((userStyle) -> {
                    return UserStyleRes.of(userStyle.getStyle());
                })
                .toList();
    }

    @Transactional
    public void updateUserStyles(UserStyleUpdateSvcReq request, User loginUser) {

        em.merge(loginUser);
        List<UserStyle> userStyles = userStyleRepository.findAllByUser(loginUser);

        List<Style> styles = styleRepository.findAllById(request.getStyleIds());

        if (styles.size() != request.getStyleIds().size()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SIZE_ID);
        }

        List<UserStyle> deleteUserStyle = userStyles.stream()
                .filter(userStyle -> !styles.contains(userStyle.getStyle()))
                .toList();

        userStyleRepository.deleteAllInBatch(deleteUserStyle);

        List<Style> existingStyles = userStyles
                .stream()
                .map(UserStyle::getStyle)
                .toList();

        List<Style> addStyles = styles.stream()
                .filter(style -> !existingStyles.contains(style))
                .toList();

        userStyles.addAll(UserStyle.createUserStylesBy(addStyles, loginUser));

        userStyleRepository.saveAll(userStyles);
    }

    @Transactional
    public void deleteUser(User loginUser) {
        em.merge(loginUser);
        loginUser.disjoin();
    }
}
