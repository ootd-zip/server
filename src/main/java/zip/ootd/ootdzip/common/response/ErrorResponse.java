package zip.ootd.ootdzip.common.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    // 에러 상태 코드
    private int statusCode;

    // 에러 구분 코드
    private String divisionCode;

    // 에러 메시지
    private String resultMsg;

    // 상세 에러 메시지
    private List<FieldError> errors;

    // 에러 이유
    private String reason;

    /**
     * ErrorResponse 생성자-1
     *
     * @param code ErrorCode
     */
    @Builder
    protected ErrorResponse(final ErrorCode code) {
        this.resultMsg = code.getMessage();
        this.statusCode = code.getStatus();
        this.divisionCode = code.getDivisionCode();
        this.errors = new ArrayList<>();
    }

    /**
     * ErrorResponse 생성자-2
     *
     * @param code   ErrorCode
     * @param reason String
     */
    @Builder
    protected ErrorResponse(final ErrorCode code, final String reason) {
        this.resultMsg = code.getMessage();
        this.statusCode = code.getStatus();
        this.divisionCode = code.getDivisionCode();
        this.reason = reason;
    }

    /**
     * ErrorResponse 생성자-3
     *
     * @param code   ErrorCode
     * @param errors List<FieldError>
     */
    @Builder
    protected ErrorResponse(final ErrorCode code, final List<FieldError> errors) {
        this.resultMsg = code.getMessage();
        this.statusCode = code.getStatus();
        this.errors = errors;
        this.divisionCode = code.getDivisionCode();
    }


    /**
     * Global Exception 전송 타입-1
     *
     * @param code          ErrorCode
     * @param bindingResult BindingResult
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    /**
     * Global Exception 전송 타입-2
     *
     * @param code ErrorCode
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    /**
     * Global Exception 전송 타입-3
     *
     * @param code   ErrorCode
     * @param reason String
     * @return ErrorResponse
     */
    public static ErrorResponse of(final ErrorCode code, final String reason) {
        return new ErrorResponse(code, reason);
    }


    /**
     * 에러를 e.getBindingResult() 형태로 전달 받는 경우 해당 내용을 상세 내용으로 변경하는 기능을 수행한다.
     */
    @Getter
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;

        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(FieldError.builder()
                            .field(field)
                            .value(value)
                            .reason(reason)
                            .build());
            return fieldErrors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> FieldError.builder()
                            .field(error.getField())
                            .value(error.getRejectedValue() == null ? "" : error.getRejectedValue().toString())
                            .reason(error.getDefaultMessage())
                            .build())
                    .collect(Collectors.toList());
        }

        @Builder
        FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }
    }
}
