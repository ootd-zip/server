package zip.ootd.ootdzip.brandrequest.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.brandrequest.data.BrandRequestStatus;
import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;
import zip.ootd.ootdzip.brandrequest.repository.BrandRequestRepository;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestApproveSvcReq;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestRejectSvcReq;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestSvcReq;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.user.data.UserRole;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

@SpringBootTest
class BrandRequestServiceTest {

    @Autowired
    private BrandRequestService brandRequestService;

    @Autowired
    private BrandRequestRepository brandRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandRepository brandRepository;

    @DisplayName("브랜드를 건의한다.")
    @Transactional
    @Test
    void insertBrandRequest() {
        // given
        User user = createUserBy("요청자1");

        BrandRequestSvcReq brandRequestSvcReq = BrandRequestSvcReq.builder()
                .requestContents("브랜드 건의1")
                .build();

        // when
        brandRequestService.insertBrandRequest(brandRequestSvcReq, user);

        //then
        BrandRequest brandRequest = brandRequestRepository.findOneByRequestContents(
                brandRequestSvcReq.getRequestContents()).get();
        assertThat(brandRequest)
                .extracting("requestContents", "requestUser.Id")
                .contains(brandRequestSvcReq.getRequestContents(), user.getId());

    }

    @DisplayName("요청 이름이 없이 브랜드를 건의하면 실패한다.")
    @Transactional
    @Test
    void insertBrandRequestWithEmptyReqeustName() {
        // given
        User user = createUserBy("요청자1");

        BrandRequestSvcReq brandRequestSvcReq = BrandRequestSvcReq.builder()
                .requestContents("")
                .build();

        // when & then
        assertThatThrownBy(() -> brandRequestService.insertBrandRequest(brandRequestSvcReq, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "BR001", "브랜드 요청 내용은 필수입니다.");

    }

    @DisplayName("브랜드 요청을 승인하여 브랜드를 추가한다.")
    @Transactional
    @Test
    void approveBrandRequest() {
        // given
        User requestUser = createUserBy("요청자1");
        BrandRequest brandRequest = createBrandRequestBy("브랜드1", requestUser);
        BrandRequest brandRequest2 = createBrandRequestBy("브랜드1 요청", requestUser);
        BrandRequest brandRequest3 = createBrandRequestBy("브랜드1 추가해주세요.", requestUser);

        User user = createUserBy("어드민1", UserRole.ADMIN);
        BrandRequestApproveSvcReq request = BrandRequestApproveSvcReq.builder()
                .brandName("브랜드1")
                .brandEngName("Brand1")
                .brandRequestId(List.of(brandRequest.getId(), brandRequest2.getId(), brandRequest3.getId()))
                .build();

        // when
        brandRequestService.approveBrandRequest(request, user);

        //then
        Brand addedBrand = brandRepository.findOneByName("브랜드1").get();
        List<BrandRequest> result = brandRequestRepository.findAllById(
                List.of(brandRequest.getId(), brandRequest2.getId(), brandRequest3.getId()));

        assertThat(addedBrand)
                .extracting("name", "engName")
                .containsExactlyInAnyOrder(request.getBrandName(), request.getBrandEngName());

        assertThat(result)
                .extracting("id", "requestStatus")
                .containsExactlyInAnyOrder(
                        tuple(brandRequest.getId(), BrandRequestStatus.APPROVED),
                        tuple(brandRequest2.getId(), BrandRequestStatus.APPROVED),
                        tuple(brandRequest3.getId(), BrandRequestStatus.APPROVED)
                );

    }

    @DisplayName("브랜드 요청을 승인할 때 일반 유저가 승인하면 에러가 발생한다.")
    @Transactional
    @Test
    void approveBrandRequestWithNoAdminUser() {
        // given
        User requestUser = createUserBy("요청자1");
        BrandRequest brandRequest = createBrandRequestBy("브랜드1", requestUser);

        User user = createUserBy("어드민1", UserRole.USER);
        BrandRequestApproveSvcReq request = BrandRequestApproveSvcReq.builder()
                .brandName("브랜드1")
                .brandEngName("Brand1")
                .brandRequestId(List.of(brandRequest.getId()))
                .build();
        // when & then
        assertThatThrownBy(() -> brandRequestService.approveBrandRequest(request, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(403, "G008", "Forbidden Exception");
    }

    @DisplayName("브랜드 요청을 승인할 때 이미 존재하는 브랜드명을 입력하면 에러가 발생한다.")
    @Transactional
    @Test
    void approveBrandRequestWithExistBrandName() {
        // given
        User requestUser = createUserBy("요청자1");
        BrandRequest brandRequest = createBrandRequestBy("브랜드1", requestUser);
        Brand brand = createBrandBy("브랜드1", "Brand1");

        User user = createUserBy("어드민1", UserRole.ADMIN);
        BrandRequestApproveSvcReq request = BrandRequestApproveSvcReq.builder()
                .brandName("브랜드1")
                .brandEngName("Brand")
                .brandRequestId(List.of(brandRequest.getId()))
                .build();

        // when & then
        assertThatThrownBy(() -> brandRequestService.approveBrandRequest(request, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "BR004", "이미 존재하는 브랜드명입니다.");
    }

    @DisplayName("브랜드 요청을 승인할 때 이미 존재하는 영문 브랜드명을 입력하면 에러가 발생한다.")
    @Transactional
    @Test
    void approveBrandRequestWithExistBrandEngName() {
        // given
        User requestUser = createUserBy("요청자1");
        BrandRequest brandRequest = createBrandRequestBy("브랜드1", requestUser);
        Brand brand = createBrandBy("브랜드1", "Brand1");

        User user = createUserBy("어드민1", UserRole.ADMIN);
        BrandRequestApproveSvcReq request = BrandRequestApproveSvcReq.builder()
                .brandName("브랜드")
                .brandEngName("Brand1")
                .brandRequestId(List.of(brandRequest.getId()))
                .build();

        // when & then
        assertThatThrownBy(() -> brandRequestService.approveBrandRequest(request, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "BR005", "이미 존재하는 영문 브랜드명입니다.");
    }

    @DisplayName("브랜드 요청을 거절한다.")
    @Transactional
    @Test
    void rejectBrandRequest() {
        // given
        User requestUser = createUserBy("요청자1");
        BrandRequest brandRequest = createBrandRequestBy("브랜드1", requestUser);
        BrandRequest brandRequest2 = createBrandRequestBy("브랜드1 요청", requestUser);
        BrandRequest brandRequest3 = createBrandRequestBy("브랜드1 추가해주세요.", requestUser);

        User user = createUserBy("어드민1", UserRole.ADMIN);
        String reason = "거절 사유를 입력합니다.";
        BrandRequestRejectSvcReq request = BrandRequestRejectSvcReq.builder()
                .brandRequestId(List.of(brandRequest.getId(), brandRequest2.getId(), brandRequest3.getId()))
                .reason(reason)
                .build();
        // when
        brandRequestService.rejectBrandRequest(request, user);

        //then
        List<BrandRequest> result = brandRequestRepository.findAllById(
                List.of(brandRequest.getId(), brandRequest2.getId(), brandRequest3.getId()));

        assertThat(result)
                .extracting("id", "requestStatus", "reason")
                .containsExactlyInAnyOrder(
                        tuple(brandRequest.getId(), BrandRequestStatus.REJECTION, reason),
                        tuple(brandRequest2.getId(), BrandRequestStatus.REJECTION, reason),
                        tuple(brandRequest3.getId(), BrandRequestStatus.REJECTION, reason)
                );

    }

    @DisplayName("브랜드 요청을 거절할 때 사유를 입력하지 않으면 에러가 발생한다.")
    @Transactional
    @Test
    void rejectBrandRequestWithoutReason() {
        // given
        User requestUser = createUserBy("요청자1");
        BrandRequest brandRequest = createBrandRequestBy("브랜드1", requestUser);
        BrandRequest brandRequest2 = createBrandRequestBy("브랜드1 요청", requestUser);
        BrandRequest brandRequest3 = createBrandRequestBy("브랜드1 추가해주세요.", requestUser);

        User user = createUserBy("어드민1", UserRole.ADMIN);
        String reason = "";
        BrandRequestRejectSvcReq request = BrandRequestRejectSvcReq.builder()
                .brandRequestId(List.of(brandRequest.getId(), brandRequest2.getId(), brandRequest3.getId()))
                .reason(reason)
                .build();
        // when & then
        assertThatThrownBy(() -> brandRequestService.rejectBrandRequest(request, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "BR006", "거절 사유를 입력해주세요.");

    }

    @DisplayName("브랜드 요청을 거절할 때 유효하지 않은 브랜드 요청 ID를 입력하면 에러가 발생한다.")
    @Transactional
    @Test
    void rejectBrandRequestWithInvalidBrandRequestId() {
        // given
        User requestUser = createUserBy("요청자1");
        BrandRequest brandRequest = createBrandRequestBy("브랜드1", requestUser);
        BrandRequest brandRequest2 = createBrandRequestBy("브랜드1 요청", requestUser);
        BrandRequest brandRequest3 = createBrandRequestBy("브랜드1 추가해주세요.", requestUser);

        User user = createUserBy("어드민1", UserRole.ADMIN);
        String reason = "거절 사유를 입력합니다.";
        BrandRequestRejectSvcReq request = BrandRequestRejectSvcReq.builder()
                .brandRequestId(List.of(brandRequest.getId() + 12331, brandRequest2.getId(), brandRequest3.getId()))
                .reason(reason)
                .build();
        // when & then
        assertThatThrownBy(() -> brandRequestService.rejectBrandRequest(request, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "BR003", "유효하지 않은 브랜드 요청 ID가 있습니다.");

    }

    private User createUserBy(String name) {
        User user = User.getDefault();
        user.setName(name);
        return userRepository.save(user);
    }

    private User createUserBy(String name, UserRole userRole) {
        User user = User.getDefault();
        user.setName(name);
        user.setUserRole(userRole);
        return userRepository.save(user);
    }

    private BrandRequest createBrandRequestBy(String requestContents, User requestUser) {
        BrandRequest brandRequest = BrandRequest.createBy(requestContents, requestUser);
        return brandRequestRepository.save(brandRequest);
    }

    private Brand createBrandBy(String brandName, String brandEngName) {
        Brand brand = Brand.builder()
                .name(brandName)
                .engName(brandEngName)
                .build();

        return brandRepository.save(brand);
    }
}
