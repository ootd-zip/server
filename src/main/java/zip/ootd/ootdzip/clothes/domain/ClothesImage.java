package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.common.entity.BaseEntity;

import java.util.List;
import java.util.stream.Collectors;

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
