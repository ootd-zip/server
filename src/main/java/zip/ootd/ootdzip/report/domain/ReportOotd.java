package zip.ootd.ootdzip.report.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "report_ootds")
@Getter
@NoArgsConstructor
public class ReportOotd extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ootd_id")
    private Ootd ootd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private ReportOotd(Report report, Ootd ootd, User user) {
        this.report = report;
        this.ootd = ootd;
        this.user = user;
    }

    public static ReportOotd of(Report report, Ootd ootd, User user) {
        return ReportOotd.builder()
                .report(report)
                .ootd(ootd)
                .user(user)
                .build();
    }

}
