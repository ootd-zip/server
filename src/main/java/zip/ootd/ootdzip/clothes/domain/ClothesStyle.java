package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.category.domain.Style;

import java.util.List;
import java.util.stream.Collectors;

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

    public static ClothesStyle createClothesStyleBy(Style style){
        return ClothesStyle.builder()
                .style(style)
                .build();
    }

    public static List<ClothesStyle> createClothesStylesBy(List<Style> styles){
        return styles.stream()
                .map(ClothesStyle::createClothesStyleBy)
                .collect(Collectors.toList());
    }

}
