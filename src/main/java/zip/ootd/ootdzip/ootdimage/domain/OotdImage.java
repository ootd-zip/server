package zip.ootd.ootdzip.ootdimage.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.images.domain.Images;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;

@Entity
@Table(name = "ootd_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OotdImage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ootd_id", nullable = false)
    private Ootd ootd;

    private Images images;

    @Builder.Default
    @OneToMany(mappedBy = "ootdImage", cascade = CascadeType.ALL)
    private List<OotdImageClothes> ootdImageClothesList = new ArrayList<>();

    public static OotdImage createOotdImageBy(String imageUrl,
            List<OotdImageClothes> ootdImageClothesList) {

        OotdImage ootdImage = OotdImage.builder()
                .images(new Images(imageUrl))
                .build();

        ootdImage.addOotdImageClothesList(ootdImageClothesList);

        return ootdImage;
    }

    //== 연관관계 메서드==//
    public void addOotdImageClothes(OotdImageClothes ootdImageClothes) {
        ootdImageClothesList.add(ootdImageClothes);
        ootdImageClothes.setOotdImage(this);
    }

    public void addOotdImageClothesList(List<OotdImageClothes> ootdImageClothesList) {
        ootdImageClothesList.forEach(this::addOotdImageClothes);
    }
}
