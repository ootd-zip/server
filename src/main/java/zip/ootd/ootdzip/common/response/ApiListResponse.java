package zip.ootd.ootdzip.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ApiListResponse<T> implements Response{

    private List<T> resultList;

    private Integer resultCode;

    private String resultMsg;
}
