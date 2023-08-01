package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.BaseEntity;

@Entity
@Table(name = "clothes_image")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClothesImage extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "clothes_id")
    private Clothes clothes;
    @Column(length = 2048)
    private String imageUrl;
}
