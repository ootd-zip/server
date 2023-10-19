package zip.ootd.ootdzip.clothes.domain;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.common.entity.BaseEntity;

@Entity
@Table(name = "clothes_image")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class ClothesImage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "clothes_id")
    private Clothes clothes;

    @Column(length = 2048)
    private String imageUrl;

    public static ClothesImage createClothesImageBy(String imageUrl) {
        return ClothesImage.builder()
                .imageUrl(imageUrl)
                .build();
    }

    public static List<ClothesImage> createClothesImagesBy(List<String> imageUrls) {
        return imageUrls.stream()
                .map(ClothesImage::createClothesImageBy)
                .collect(Collectors.toList());
    }

}
