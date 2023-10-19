package zip.ootd.ootdzip.common.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {

    private T result;

    private int statusCode;

    public ApiResponse(T result) {
        this.result = result;
        statusCode = 200;
    }
}
