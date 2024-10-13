package zip.ootd.ootdzip.common.exception;

import lombok.Getter;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
