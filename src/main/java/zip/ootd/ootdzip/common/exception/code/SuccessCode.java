package zip.ootd.ootdzip.common.exception.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

    // 조회 성공 코드
    SELECT_SUCCESS(200, "SUCCESS_001", "SELECT SUCCESS"),

    // 삭제 성공 코드
    DELETE_SUCCESS(200, "SUCCESS_002", "DELETE SUCCESS"),

    // 삽입 성공 코드
    INSERT_SUCCESS(201, "SUCCESS_003", "INSERT SUCCESS"),

    // 수정 성공 코드
    UPDATE_SUCCESS(201, "SUCCESS_004", "UPDATE SUCCESS"),

    ;

    private final Integer status;

    private final String code;

    private final String message;
}
