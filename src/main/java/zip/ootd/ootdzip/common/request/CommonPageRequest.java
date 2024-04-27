package zip.ootd.ootdzip.common.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CommonPageRequest {

    private Integer page = 0;

    private Integer size = 30;

    private String sortCriteria = "createdAt";

    private Direction sortDirection = Direction.DESC;

    public CommonPageRequest(Integer page, Integer size, String sortCriteria, Direction sortDirection) {
        this.page = page;
        this.size = size;
        this.sortCriteria = sortCriteria;
        this.sortDirection = sortDirection;
    }

    public CommonPageRequest(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public Pageable toPageable() {
        return PageRequest.of(this.page, this.size, sortDirection, sortCriteria);
    }

    public Pageable toPageableWithSort(Sort sort) {
        return PageRequest.of(this.page, this.size, sort);
    }
}
