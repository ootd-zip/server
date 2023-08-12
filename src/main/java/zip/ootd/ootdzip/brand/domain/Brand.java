package zip.ootd.ootdzip.brand.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.BaseEntity;
import zip.ootd.ootdzip.brand.data.BrandStatus;

@Entity
@Table(name = "brands")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {

    private String name;

    private BrandStatus status;
}
