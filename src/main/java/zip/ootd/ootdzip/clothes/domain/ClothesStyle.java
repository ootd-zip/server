package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.category.domain.Style;

@Entity
@Table(name = "clothes_styles_map")
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder
    public ClothesStyle(Clothes clothes, Style style){
        this.clothes = clothes;
        this.style = style;
    }
}
