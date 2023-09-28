package zip.ootd.ootdzip.clothes.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.entity.BaseEntity;

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
