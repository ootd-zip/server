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
import zip.ootd.ootdzip.utils.ImageFileUtil;

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

    private static ClothesImage createClothesImageBy(String imageUrl) {

        if (!ImageFileUtil.isValidImageUrl(imageUrl)) {
            throw new IllegalArgumentException("지원하는 이미지 확장자가 아닙니다.");
        }

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
