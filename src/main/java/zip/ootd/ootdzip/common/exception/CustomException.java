package zip.ootd.ootdzip.common.exception;

import lombok.Getter;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private Exception exception;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, Exception exception) {
        this.errorCode = errorCode;
        this.exception = exception;
    }
}
