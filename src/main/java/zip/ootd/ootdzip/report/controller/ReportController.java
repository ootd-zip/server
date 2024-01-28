package zip.ootd.ootdzip.report.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.report.controller.response.ReportRes;
import zip.ootd.ootdzip.report.service.ReportService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Report 컨트롤러", description = "신고 관련 API")
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("")
    public ApiResponse<List<ReportRes>> getAllReports() {
        return new ApiResponse<>(reportService.getAllReports());
    }

}
