package zip.ootd.ootdzip.clothes.domain;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.category.domain.Style;

@Entity
@Table(name = "clothes_styles_map")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class ClothesStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clothes_id", nullable = false)
    private Clothes clothes;

    @ManyToOne
    @JoinColumn(name = "style_id", nullable = false)
    private Style style;

    public static ClothesStyle createClothesStyleBy(Style style) {
        return ClothesStyle.builder()
                .style(style)
                .build();
    }

    public static List<ClothesStyle> createClothesStylesBy(List<Style> styles) {
        return styles.stream()
                .map(ClothesStyle::createClothesStyleBy)
                .collect(Collectors.toList());
    }

}
