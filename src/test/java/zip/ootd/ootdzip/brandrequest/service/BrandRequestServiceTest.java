package zip.ootd.ootdzip.brandrequest.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;
import zip.ootd.ootdzip.brandrequest.repository.BrandRequestRepository;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestApproveSvcReq;
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
    @Test
    void insertBrandRequest() {
        // given
        User user = createUserBy("요청자1");

        BrandRequestSvcReq brandRequestSvcReq = BrandRequestSvcReq.builder()
                .requestContents("브랜드 건의1")
                .build();

        // when
        brandRequestService.insertBrandReqeust(brandRequestSvcReq, user);

        //then
        BrandRequest brandRequest = brandRequestRepository.findOneByRequestName(
                brandRequestSvcReq.getRequestContents());
        assertThat(brandRequest)
                .extracting("requestName", "requestUser.Id")
                .contains(brandRequestSvcReq.getRequestContents(), user.getId());

    }

    @DisplayName("요청 이름이 없이 브랜드를 건의하면 실패한다.")
    @Test
    void insertBrandRequestWithEmptyReqeustName() {
        // given
        User user = createUserBy("요청자1");

        BrandRequestSvcReq brandRequestSvcReq = BrandRequestSvcReq.builder()
                .requestContents("")
                .build();

        // when & then
        assertThatThrownBy(() -> brandRequestService.insertBrandReqeust(brandRequestSvcReq, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "BR001", "브랜드 요청 내용은 필수입니다.");

    }

    @DisplayName("브랜드 요청을 승인하여 브랜드를 추가한다")
    @Test
    void approveBrandRequest() {
        // given
        User requestUser = createUserBy("요청자1");
        BrandRequest brandRequest = createBrandRequestBy("브랜드1", requestUser);

        User user = createUserBy("어드민1");
        BrandRequestApproveSvcReq request = new BrandRequestApproveSvcReq();

        // when
        brandRequestService.approveBrandRequest(request, user);

        //then
        brandRepository.findOneByName("브랜드1");

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
}