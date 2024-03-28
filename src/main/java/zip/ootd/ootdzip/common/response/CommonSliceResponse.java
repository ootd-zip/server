package zip.ootd.ootdzip.common.response;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommonSliceResponse<T> {

    private List<T> content;

    private Integer page = 0;

    private Integer size = 30;

    private Boolean isLast;

    public CommonSliceResponse(List<T> content, Pageable pageable, Boolean isLast) {
        this.content = content;
        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.isLast = isLast;
    }
}
