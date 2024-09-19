package zip.ootd.ootdzip.brandrequest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brandrequest.controller.reqeuest.BrandRequestApproveReq;
import zip.ootd.ootdzip.brandrequest.controller.reqeuest.BrandRequestRejectReq;
import zip.ootd.ootdzip.brandrequest.controller.reqeuest.BrandRequestSearchReq;
import zip.ootd.ootdzip.brandrequest.controller.response.BrandRequestSearchRes;
import zip.ootd.ootdzip.brandrequest.service.BrandRequestService;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Brand Request Admin 컨트롤러", description = "관리자가 사용하는 브랜드 요청 관련 기능입니다.")
@RequestMapping("/api/admin/brand-request")
public class AdminBrandRequestController {

    private final BrandRequestService brandRequestService;
    private final UserService userService;

    @PostMapping("/approve-brand-request")
    public ApiResponse<String> approveBrandRequest(@Valid @RequestBody BrandRequestApproveReq request) {
        brandRequestService.approveBrandRequest(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("OK");
    }

    @PostMapping("/reject-brand-request")
    public ApiResponse<String> rejectBrandRequest(@Valid @RequestBody BrandRequestRejectReq request) {
        brandRequestService.rejectBrandRequest(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("OK");
    }

    @GetMapping
    public ApiResponse<CommonPageResponse<BrandRequestSearchRes>> searchBrandRequest(
            @Valid BrandRequestSearchReq request) {
        return new ApiResponse<>(brandRequestService.searchBrandRequest(request.toServiceRequest(),
                userService.getAuthenticatiedUser()));
    }
}
