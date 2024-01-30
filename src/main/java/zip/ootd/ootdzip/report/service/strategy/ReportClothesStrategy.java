package zip.ootd.ootdzip.report.service.strategy;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.user.domain.User;

@RequiredArgsConstructor
@Component
public class ReportClothesStrategy implements ReportStrategy {
    @Override
    public ReportResultRes report(User reporter, ReportSvcReq request) {
        return null;
    }
}
