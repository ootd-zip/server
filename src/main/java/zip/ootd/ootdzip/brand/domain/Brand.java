package zip.ootd.ootdzip.brand.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
