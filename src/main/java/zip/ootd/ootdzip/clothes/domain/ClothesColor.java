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
import zip.ootd.ootdzip.category.domain.Color;

@Entity
@Table(name = "clothes_colors_map")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ClothesColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clothes_id")
    private Clothes clothes;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    private static ClothesColor createClothesColorBy(Color color) {
        return ClothesColor.builder()
                .color(color)
                .build();
    }

    public static List<ClothesColor> createClothesColorsBy(List<Color> colors) {
        return colors.stream()
                .map(ClothesColor::createClothesColorBy)
                .collect(Collectors.toList());
    }

}
