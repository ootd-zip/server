package zip.ootd.ootdzip.brand.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.data.BrandStatus;
import zip.ootd.ootdzip.common.entity.BaseEntity;

@Entity
@Table(name = "brands")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrandStatus status;
}
