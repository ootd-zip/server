package zip.ootd.ootdzip.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import zip.ootd.ootdzip.oauth.service.KakaoOAuthUtils;
import zip.ootd.ootdzip.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KakaoOAuthUtils kakaoOAuthUtils;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

//    public TokenInfo register(UserDto.RegisterReq request) {
//        if (!request.getOauthProvider().equalsIgnoreCase("kakao")) {
//            throw new IllegalArgumentException("Not implemented OAuth provider: " + request.getOauthProvider());
//        }
//        kakaoOAuthUtils.requestTokenByAuthorizationCode(request.getAuthorizationCode());
//    }

}
