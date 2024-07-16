package zip.ootd.ootdzip.brandrequest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;
import zip.ootd.ootdzip.brandrequest.repository.BrandRequestRepository;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestApproveSvcReq;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestSvcReq;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandRequestService {

    private final BrandRequestRepository brandRequestRepository;

    @Transactional
    public void insertBrandReqeust(BrandRequestSvcReq request, User loginUser) {

        if (request.getRequestContents().isBlank()) {
            throw new CustomException(ErrorCode.REQUIRED_BRAND_REQUEST_NAME);
        }

        BrandRequest brandRequest = BrandRequest.createBy(request.getRequestContents(), loginUser);

        brandRequestRepository.save(brandRequest);
    }

    @Transactional
    public void approveBrandRequest(BrandRequestApproveSvcReq request, User loginUser) {

    }
}
