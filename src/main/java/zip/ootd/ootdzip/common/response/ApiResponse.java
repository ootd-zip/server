package zip.ootd.ootdzip.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> implements Response {

    private T result;

    private Integer resultCode;

    private String resultMsg;
}
