package zip.ootd.ootdzip.brandrequest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brandrequest.controller.reqeuest.BrandRequestApproveReq;
import zip.ootd.ootdzip.brandrequest.controller.reqeuest.BrandRequestRejectReq;
import zip.ootd.ootdzip.brandrequest.controller.reqeuest.BrandRequestReq;
import zip.ootd.ootdzip.brandrequest.service.BrandRequestService;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Brand Request 컨트롤러", description = "브랜드 건의 관련 API입니다.")
@RequestMapping("/api/v1/brand-request")
public class BrandRequestController {

    private final BrandRequestService brandRequestService;
    private final UserService userService;

    @PostMapping
    public ApiResponse<String> insertBrandRequest(@Valid @RequestBody BrandRequestReq request) {
        brandRequestService.insertBrandRequest(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("OK");
    }

    @PostMapping("/api/admin/approve-brand-request")
    public ApiResponse<String> approveBrandRequest(@Valid @RequestBody BrandRequestApproveReq request) {
        brandRequestService.approveBrandRequest(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("OK");
    }

    @PostMapping("/api/admin/reject-brand-request")
    public ApiResponse<String> rejectBrandRequest(@Valid @RequestBody BrandRequestRejectReq request) {
        brandRequestService.rejectBrandRequest(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("OK");
    }

}
