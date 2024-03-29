package zip.ootd.ootdzip.report.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.report.controller.request.ReportReq;
import zip.ootd.ootdzip.report.controller.response.ReportRes;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.service.ReportService;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Report 컨트롤러", description = "신고 관련 API")
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @Operation(summary = "신고항목 조회", description = "신고항목 조회하는 API")
    @GetMapping("")
    public ApiResponse<List<ReportRes>> getAllReports() {
        return new ApiResponse<>(reportService.getAllReports());
    }

    @Operation(summary = "신고", description = "신고 API")
    @PostMapping("")
    public ApiResponse<ReportResultRes> report(@RequestBody @Valid ReportReq request) {
        return new ApiResponse<>(reportService.report(request.toServiceReq(), userService.getAuthenticatiedUser()));
    }
}
