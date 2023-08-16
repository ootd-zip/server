package zip.ootd.ootdzip.oauth.service;


import zip.ootd.ootdzip.oauth.domain.OauthProvider;

public interface SocialOAuth {

    /**
     * @param args : first argument must be Authorization Code
     *             : second argument is option (redirectUri)
     * @return String : id
     */
    String getSocialIdBy(String... args);

    default OauthProvider type() {
        if (this instanceof GoogleOAuthUtils) {
            return OauthProvider.GOOGLE;
        } else if (this instanceof KakaoOAuthUtils) {
            return OauthProvider.KAKAO;
        } else {
            return null;
        }
    }
}
