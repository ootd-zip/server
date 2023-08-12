package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.category.domain.Color;

@Entity
@Table(name = "clothes_colors_map")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
