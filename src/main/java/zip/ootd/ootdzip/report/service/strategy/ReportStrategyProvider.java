package zip.ootd.ootdzip.report.service.strategy;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import zip.ootd.ootdzip.report.service.request.ReportType;

@Component
public class ReportStrategyProvider {

    private final Map<ReportType, ReportStrategy> reportActions;

    public ReportStrategyProvider(
            final ReportOotdStrategy reportOotdStrategy,
            final ReportCommentStrategy reportCommentStrategy,
            final ReportClothesStrategy reportClothesStrategy
    ) {
        this.reportActions = new EnumMap<>(ReportType.class);
        this.reportActions.put(ReportType.OOTD, reportOotdStrategy);
        this.reportActions.put(ReportType.COMMENT, reportCommentStrategy);
        this.reportActions.put(ReportType.CLOTHES, reportClothesStrategy);
    }

    public ReportStrategy getStrategy(ReportType type) {
        return reportActions.get(type);
    }
}
