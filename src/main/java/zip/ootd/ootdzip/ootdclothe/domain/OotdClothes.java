package zip.ootd.ootdzip.ootdclothe.domain;

import java.util.List;
import java.util.stream.Collectors;

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
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OotdClothes extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ootd_id")
    private Ootd ootd;

    @ManyToOne
    @JoinColumn(name = "clothe_id")
    private Clothes clothes;

    public static OotdClothes createOotdClothesBy(Clothes clothes) {

        return OotdClothes.builder()
                .clothes(clothes)
                .build();
    }

    public static List<OotdClothes> createOotdClothesListBy(List<Clothes> clothesList) {

        return clothesList.stream()
                .map(OotdClothes::createOotdClothesBy)
                .collect(Collectors.toList());
    }
}
