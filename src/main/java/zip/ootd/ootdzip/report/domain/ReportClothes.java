package zip.ootd.ootdzip.report.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "report_clothes")
@Getter
@NoArgsConstructor
public class ReportClothes extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clothes_id")
    private Clothes clothes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User reporter;

    @Builder
    private ReportClothes(Report report, Clothes clothes, User reporter) {
        this.report = report;
        this.clothes = clothes;
        this.reporter = reporter;
    }

    public static ReportClothes of(Report report, Clothes clothes, User reporter) {
        return ReportClothes.builder()
                .report(report)
                .clothes(clothes)
                .reporter(reporter)
                .build();
    }

}
