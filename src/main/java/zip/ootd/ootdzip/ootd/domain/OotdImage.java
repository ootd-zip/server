package zip.ootd.ootdzip.ootd.domain;

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

    @Column(length = 2048)
    private String imageUrl;

    public static OotdImage createOotdImageBy(String imageUrl) {
        return OotdImage.builder()
                .imageUrl(imageUrl)
                .build();
    }

    public static List<OotdImage> createOotdImagesBy(List<String> imageUrls) {
        return imageUrls.stream()
                .map(OotdImage::createOotdImageBy)
                .collect(Collectors.toList());
    }
}
