package zip.ootd.ootdzip.common.request;

import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SortColumn {
    private String sortCriteria;
    private Sort.Direction sortDirection;

    public Sort toSort() {
        return Sort.by(sortDirection, sortCriteria);
    }
}
