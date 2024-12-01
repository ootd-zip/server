package zip.ootd.ootdzip.fcm.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.fcm.data.FcmMessageRes;
import zip.ootd.ootdzip.fcm.data.FcmPostConfigReq;
import zip.ootd.ootdzip.fcm.data.FcmPostReq;
import zip.ootd.ootdzip.fcm.domain.FcmInfo;
import zip.ootd.ootdzip.fcm.repository.FcmRepository;
import zip.ootd.ootdzip.notification.domain.Notification;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmService {

    private final FcmRepository fcmRepository;
    private final UserRepository userRepository;

    @Value(("${fcm.service-key}"))
    private String accessKey;

    @Value(("${fcm.project-id}"))
    private String projectId;

    /**
     * 사용자가 토큰이없다면 최초생성이므로
     * 모든 알람을 on 으로 하여 생성합니다.
     *
     * 토큰이 이미 존재한다면
     * 사용자 토큰을 로그인상태 설정하여 알람을 받을 수 있도록 합니다.
     */
    @Transactional
    public void onFcmToken(FcmPostReq request, User loginUser) {

        Optional<FcmInfo> fcmInfo = fcmRepository.findByFcmToken(request.getFcmToken());

        // fcm 토큰이 없다면
        // 최초로 생성하고, 모든 알람허용으로 기본 생성
        if (fcmInfo.isEmpty()) {
            FcmInfo createdFcmInfo = FcmInfo.createDefaultFcmInfo(loginUser, request.getFcmToken());
            fcmRepository.save(createdFcmInfo);
        }

        // fcm 토큰이 있다면
        // 해당 토큰 기기 사용자를 로그인상태로 변경
        fcmInfo.ifPresent(FcmInfo::login);
    }

    @Transactional
    public void offFcmToken(FcmPostReq request) {

        Optional<FcmInfo> fcmInfo = fcmRepository.findByFcmToken(request.getFcmToken());

        // fcm 토큰이 있다면
        // 해당 토큰 기기 사용자를 로그아웃 상태로 변경
        fcmInfo.ifPresent(FcmInfo::logout);
    }

    /**
     * 푸시 메시지 처리를 수행하는 비즈니스 로직
     */
    public boolean sendMessage(Notification notification) {

        List<String> receiverTokens = getReceiverTokens(notification);

        // 토큰이 없다면 없는 유저거나 알람을 받지 않는 유저
        if (receiverTokens == null || receiverTokens.isEmpty()) {
            return false;
        }

        List<ResponseEntity<String>> responses = receiverTokens.stream()
                .map(token -> requestFCM(makeMessage(notification, token))).toList();

        return true;
    }

    public ResponseEntity<String> requestFCM(String message) {

        RestTemplate restTemplate = new RestTemplate();

        // 추가된 사항 : RestTemplate 이용중 클라이언트의 한글 깨짐 증상에 대한 수정
        //@refernece : https://stackoverflow.com/questions/29392422/how-can-i-tell-resttemplate-to-post-with-utf-8-encoding
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        String url = "<https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send>";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response;
    }

    /**
     * 사용자가 허락한 푸쉬알람일시 해당하는 기기 토큰값 반환
     * 동일한 사용자여도 알람을 허용한 기기가 여러개 일 수 있음으로 리스트로 반환합니다.
     */
    @Transactional
    public List<String> getReceiverTokens(Notification notification) {
        Optional<User> receiver = userRepository.findWithFcmInfosByUser(notification.getReceiver());
        // 알람을 수신할 유저가 존재하고
        // 해당 유저가 로그인상태고
        // 해당 유저가 알람 권한을 허용해놨을 경우
        return receiver.map(user -> user.getFcmInfos().stream()
                .filter(FcmInfo::getIsLogin)
                .filter(FcmInfo::getIsPermission)
                .filter(fcmInfo -> fcmInfo.isExistAllowNotificationType(notification))
                .map(FcmInfo::getFcmToken)
                .collect(Collectors.toList())).orElse(null);
    }

    /**
     * Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰을 발급 받습니다.
     */
    private String getAccessToken() {

        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(accessKey).getInputStream())
                    .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));

            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 메시지를 생성합니다. (Object -> String)
     */
    private String makeMessage(Notification notification, String receiverToken) {

        ObjectMapper om = new ObjectMapper();
        try {
            FcmMessageRes fcmMessageRes = FcmMessageRes.builder()
                    .message(FcmMessageRes.Message.builder()
                            .token(receiverToken)
                            .notification(notification)
                            .build()).validateOnly(false).build();
            return om.writeValueAsString(fcmMessageRes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void changeConfig(FcmPostConfigReq fcmPostConfigReq) {

        FcmInfo fcmInfo = fcmRepository.findByFcmToken(fcmPostConfigReq.getFcmToken()).orElseThrow();

        // 전체 알림 허용 수정
        if (fcmPostConfigReq.getIsPermission() != null) {
            fcmInfo.changePermission(fcmPostConfigReq.getIsPermission());
        }

        // 세부 알림 허용 수정
        if (fcmPostConfigReq.getDetailNotifications() != null) {
            fcmPostConfigReq.getDetailNotifications().forEach(dn ->
                    fcmInfo.getFcmNotificationTypes().stream()
                            .filter(fn -> fn.getNotificationType() == dn.getNotificationType())
                            .findFirst()
                            .ifPresent(fn -> fn.changeAllow(dn.getIsAllow())));
        }
    }

    @Transactional
    public void getConfig(String token) {

        FcmInfo fcmInfo = fcmRepository.findByFcmToken(token).orElseThrow();

    }
}
