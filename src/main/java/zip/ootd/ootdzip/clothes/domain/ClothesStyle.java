package zip.ootd.ootdzip.clothes.domain;

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
    public ClothesStyle(Clothes clothes, Style style) {
        this.clothes = clothes;
        this.style = style;
    }
}
