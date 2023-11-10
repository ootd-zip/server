package zip.ootd.ootdzip.ootdimageclothe.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.ootd.domain.OotdImage;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OotdImageClothes extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ootd_image_id")
    private OotdImage ootdImage;

    @ManyToOne
    @JoinColumn(name = "clothes_id")
    private Clothes clothes;

    @Embedded
    private Coordinate coordinate;

    public static OotdImageClothes createOotdImageClothesBy(Clothes clothes,
            Coordinate coordinate) {

        return OotdImageClothes.builder()
                .clothes(clothes)
                .coordinate(coordinate)
                .build();
    }
}
