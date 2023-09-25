package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.category.domain.Color;

import java.util.List;
import java.util.stream.Collectors;

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
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "clothes_id")
    private Clothes clothes;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    public static ClothesColor createClothesColorBy(Color color){
        return ClothesColor.builder()
                .color(color)
                .build();
    }

    public static List<ClothesColor> createClothesColorsBy(List<Color> colors){
        return colors.stream()
                .map(ClothesColor::createClothesColorBy)
                .collect(Collectors.toList());
    }

}
