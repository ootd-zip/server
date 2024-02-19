package zip.ootd.ootdzip.common.response;

import lombok.Data;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
public class CommonSliceResponse<T> {

    private List<T> content;

    private Integer page = 0;

    private Integer size = 30;

    private boolean isLast;

    public CommonSliceResponse(List<T> content, Pageable pageable, boolean hasNext) {
        this.content = content;
        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.isLast = hasNext;
    }
}
