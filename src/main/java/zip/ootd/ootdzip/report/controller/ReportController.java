package zip.ootd.ootdzip.report.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.report.controller.request.ReportOotdReq;
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

    @GetMapping("")
    public ApiResponse<List<ReportRes>> getAllReports() {
        return new ApiResponse<>(reportService.getAllReports());
    }

    @PostMapping("/ootd")
    public ApiResponse<ReportResultRes> reportOotd(@RequestBody @Valid ReportOotdReq request) {
        return new ApiResponse<>(reportService.reportOotd(request.toServiceReq(), userService.getAuthenticatiedUser()));
    }
}
