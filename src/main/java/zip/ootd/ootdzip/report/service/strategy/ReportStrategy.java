package zip.ootd.ootdzip.report.service.strategy;

import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.user.domain.User;

@FunctionalInterface
public interface ReportStrategy {

    ReportResultRes report(final User reporter, final ReportSvcReq request);

}
