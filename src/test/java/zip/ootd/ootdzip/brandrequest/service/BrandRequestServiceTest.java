package zip.ootd.ootdzip.brandrequest.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;
import zip.ootd.ootdzip.brandrequest.repository.BrandRequestRepository;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestSvcReq;
import zip.ootd.ootdzip.common.exception.CustomException;
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

    @DisplayName("브랜드를 건의한다.")
    @Test
    void insertBrandRequest() {
        // given
        User user = createUserBy("요청자1");

        BrandRequestSvcReq brandRequestSvcReq = BrandRequestSvcReq.builder()
                .requestName("브랜드 건의1")
                .build();

        // when
        brandRequestService.insertBrandReqeust(brandRequestSvcReq, user);

        //then
        BrandRequest brandRequest = brandRequestRepository.findOneByRequestName(brandRequestSvcReq.getRequestName());
        assertThat(brandRequest)
                .extracting("requestName", "requestUser.Id")
                .contains(brandRequestSvcReq.getRequestName(), user.getId());

    }

    @DisplayName("요청 이름이 없이 브랜드를 건의하면 실패한다.")
    @Test
    void insertBrandRequestWithEmptyReqeustName() {
        // given
        User user = createUserBy("요청자1");

        BrandRequestSvcReq brandRequestSvcReq = BrandRequestSvcReq.builder()
                .requestName("")
                .build();

        // when & then
        assertThatThrownBy(() -> brandRequestService.insertBrandReqeust(brandRequestSvcReq, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "BR001", "요청할 브랜드의 이름은 필수입니다.");

    }

    private User createUserBy(String name) {
        User user = User.getDefault();
        user.setName(name);
        return userRepository.save(user);
    }
}