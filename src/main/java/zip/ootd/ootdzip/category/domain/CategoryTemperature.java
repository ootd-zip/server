package zip.ootd.ootdzip.category.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.entity.BaseEntity;

@Entity
@Table(name = "category_temperatures")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTemperature extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private Double highestTemperature;

    private Double lowestTemperature;
}
