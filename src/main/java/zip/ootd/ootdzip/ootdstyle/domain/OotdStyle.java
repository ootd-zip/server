package zip.ootd.ootdzip.ootdstyle.domain;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OotdStyle extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ootd_id")
    private Ootd ootd;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private Style style;

    public static OotdStyle createOotdStyleBy(Style style) {

        return OotdStyle.builder()
                .style(style)
                .build();
    }

    public static List<OotdStyle> createOotdStylesBy(List<Style> styles) {

        return styles.stream()
                .map(OotdStyle::createOotdStyleBy)
                .collect(Collectors.toList());
    }
}
