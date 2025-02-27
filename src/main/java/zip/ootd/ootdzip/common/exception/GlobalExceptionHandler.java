package zip.ootd.ootdzip.common.exception;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.common.response.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * [Exception] API 호출 시 Valid 데이터 값이 유효하지 않은 경우
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR, ex.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * [Exception] API 호출 시 Validate 데이터 값이 유효하지 않은 경우
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * [Exception] API 호출 시 'Header' 내에 데이터 값이 유효하지 않은 경우
     * @param ex MissingRequestHeaderException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * [Exception] 클라이언트에서 Body로 '객체' 데이터가 넘어오지 않았을 경우
     * @param ex HttpMessageNotReadableException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * [Exception] 클라이언트에서 request로 '파라미터로' 데이터가 넘어오지 않았을 경우
     * @param ex MissingServletRequestParameterException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderExceptionException(
            MissingServletRequestParameterException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * [Exception] 잘못된 서버 요청일 경우 발생한 경우
     * @param ex HttpClientErrorException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(HttpClientErrorException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * [Exception] 잘못된 주소로 요청 한 경우
     * @param ex NoHandlerFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoHandlerFoundExceptionException(NoHandlerFoundException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * [Exception] NULL 값이 발생한 경우
     * @param ex NullPointerException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NULL_POINT_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Input / Output 내에서 발생한 경우
     * @param ex IOException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.IO_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * com.google.gson 내에 Exception 발생하는 경우
     * @param ex JsonParseException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(JsonParseException.class)
    protected ResponseEntity<ErrorResponse> handleJsonParseExceptionException(JsonParseException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.JSON_PARSE_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * com.fasterxml.jackson.core 내에 Exception 발생하는 경우
     * @param ex JsonProcessingException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * repository에서 row를 찾지 못했을 경우
     */
    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // =================================================================================================================

    /**
     * [Exception] 모든 Exception 경우 발생
     * @param ex Exception
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * [Exception] 모든 Throwable 경우 발생
     * @param ex Exception
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(Throwable.class)
    protected final ResponseEntity<ErrorResponse> handleAllThrowable(Throwable ex) {
        logThrowable(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Bearer 토큰이 잘못되었을 경우
     * @param ex InvalidBearerTokenException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(InvalidBearerTokenException.class)
    protected final ResponseEntity<ErrorResponse> handleInvalidBearerTokenException(InvalidBearerTokenException ex) {
        logException(ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_AUTHENTICATED_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    private void logException(Exception ex) {
        log.warn("""
                        Ex={}
                        Message={}
                        StackTrace={}""",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString) // 스택트레이스 줄바꿈 추가
                        .collect(Collectors.joining("\n")));
    }

    private void logThrowable(Throwable ex) {
        log.error("""
                        Ex={}
                        Message={}
                        StackTrace={}""",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString) // 스택트레이스 줄바꿈 추가
                        .collect(Collectors.joining("\n")));
    }
    // ======================================Custom Exception===========================================================

    @ExceptionHandler(CustomException.class)
    protected final ResponseEntity<ErrorResponse> handleAllExceptions(CustomException ex) {

        log.warn("""
                        Ex=CustomException
                        Status={}
                        DivisionCode={}
                        Message={}
                        StackTrace={}""",
                ex.getErrorCode().getStatus(),
                ex.getErrorCode().getDivisionCode(),
                ex.getErrorCode().getMessage(),
                Arrays.stream(ex.getStackTrace()) // 스택트레이스 줄바꿈 추가
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n")));

        return new ResponseEntity<>(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()),
                HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }
}
