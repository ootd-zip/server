package zip.ootd.ootdzip.brandrequest.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;
import zip.ootd.ootdzip.brandrequest.repository.BrandRequestRepository;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestApproveSvcReq;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestRejectSvcReq;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestSvcReq;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.notification.event.NotificationEvent;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandRequestService {

    private final BrandRequestRepository brandRequestRepository;
    private final BrandRepository brandRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void insertBrandRequest(BrandRequestSvcReq request, User loginUser) {

        if (request.getRequestContents().isBlank()) {
            throw new CustomException(ErrorCode.REQUIRED_BRAND_REQUEST_NAME);
        }

        BrandRequest brandRequest = BrandRequest.createBy(request.getRequestContents(), loginUser);

        brandRequestRepository.save(brandRequest);
    }

    @Transactional
    public void approveBrandRequest(BrandRequestApproveSvcReq request, User loginUser) {

        if (!loginUser.isAdmin()) {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }

        List<BrandRequest> approveTargets = brandRequestRepository.findByIdIn(request.getBrandRequestId());

        if (approveTargets.size() != request.getBrandRequestId().size()) {
            throw new CustomException(ErrorCode.INVALID_BRAND_REQUEST_ID);
        }

        if (brandRepository.existsByName(request.getBrandName())) {
            throw new CustomException(ErrorCode.EXISTED_BRAND_NAME);
        }

        if (brandRepository.existsByEngName(request.getBrandEngName())) {
            throw new CustomException(ErrorCode.EXISTED_BRAND_ENG_NAME);
        }

        Brand brand = Brand.builder()
                .name(request.getBrandName())
                .engName(request.getBrandEngName())
                .build();

        approveTargets.forEach((brandRequest) -> {
            eventPublisher.publishEvent(NotificationEvent.builder()
                    .receiver(brandRequest.getRequestUser())
                    .sender(brandRequest.getRequestUser())
                    .notificationType(NotificationType.BRAND_REQUEST_APPROVED)
                    .goUrl("")
                    .imageUrl(brandRequest.getRequestUser().getImages().getImageUrlSmall())
                    .content(String.format("%s(%s)가 추가되었습니다.", brand.getName(), brand.getEngName()))
                    .build());
            brandRequest.approveBrandRequest();
        });

        brandRepository.save(brand);
    }

    @Transactional
    public void rejectBrandRequest(BrandRequestRejectSvcReq request, User loginUser) {
        if (!loginUser.isAdmin()) {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }

        if (request.getReason() == null
                || request.getReason().isBlank()) {
            throw new CustomException(ErrorCode.REQUIRED_REJECT_REASON);
        }

        List<BrandRequest> approveTargets = brandRequestRepository.findByIdIn(request.getBrandRequestId());

        if (approveTargets.size() != request.getBrandRequestId().size()) {
            throw new CustomException(ErrorCode.INVALID_BRAND_REQUEST_ID);
        }

        approveTargets.forEach((brandRequest) -> {
            eventPublisher.publishEvent(NotificationEvent.builder()
                    .receiver(brandRequest.getRequestUser())
                    .sender(brandRequest.getRequestUser())
                    .notificationType(NotificationType.BRAND_REQUEST_REJECTED)
                    .goUrl("")
                    .imageUrl(brandRequest.getRequestUser().getImages().getImageUrlSmall())
                    .content(String.format("사유: %s", request.getReason()))
                    .build());
            brandRequest.rejectBrandRequest(request.getReason());
        });
    }
}
