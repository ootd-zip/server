package zip.ootd.ootdzip.common.response;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommonPageResponse<T> {

    private List<T> content;

    private Integer page = 0;

    private Integer size = 30;

    private Boolean isLast;

    private Long total;

    public CommonPageResponse(List<T> content, Pageable pageable, Boolean isLast, Long total) {
        this.content = content;
        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.isLast = isLast;
        this.total = total;
    }
}
