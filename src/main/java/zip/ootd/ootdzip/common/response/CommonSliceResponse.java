package zip.ootd.ootdzip.common.response;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Data;

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
