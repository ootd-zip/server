package zip.ootd.ootdzip.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */
    // 잘못된 서버 요청
    BAD_REQUEST_ERROR(400, "G001", "Bad Request Exception"),

    // @RequestBody 데이터 미 존재
    REQUEST_BODY_MISSING_ERROR(400, "G002", "Required request body is missing"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(400, "G003", " Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(400, "G004", "Missing Servlet RequestParameter Exception"),

    // 입력/출력 값이 유효하지 않음
    IO_ERROR(400, "G005", "I/O Exception"),

    // com.google.gson JSON 파싱 실패
    JSON_PARSE_ERROR(400, "G006", "JsonParseException"),

    // com.fasterxml.jackson.core Processing Error
    JACKSON_PROCESS_ERROR(400, "G007", "com.fasterxml.jackson.core Exception"),

    // 권한이 없음
    FORBIDDEN_ERROR(403, "G008", "Forbidden Exception"),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(404, "G009", "Not Found Exception"),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(404, "G010", "Null Point Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(404, "G011", "handle Validation Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(404, "G012", "Header에 데이터가 존재하지 않는 경우 "),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR(500, "G999", "Internal Server Error Exception"),

    /**
     * ******************************* Custom Error CodeList ***************************************
     */
    KAKAO_LOGIN_ERROR(400, "A001", "KAKAO Login Error Exception"),

    GOOGLE_LOGIN_ERROR(400, "A002", "Google Login Error Exception"),

    SOCIAL_LOGIN_ERROR(401, "A003", "Social Login Error Exception"),

    NONE_SOCIAL_ERROR(400, "U001", "존재 하지 않는 소셜로그인 요청"),

    NOT_AUTHENTICATED_ERROR(401, "U002", "인증되지 않은 사용자"),

    DELETED_USER_ERROR(403, "U003", "탈퇴된 사용자"),

    INVALID_GRANT_TYPE_ERROR(401, "T001", "잘못된 grant type"),

    INVALID_REFRESH_TOKEN_ERROR(401, "T002", "잘못된 리프레시 토큰"),

    DUPLICATE_NAME_ERROR(409, "U003", "중복된 닉네임"),

    IMAGE_CONVERT_ERROR(400, "UT001", "이미지 변환 실패"),

    FOLLOW_ERROR(409, "F001", "이미 팔로우 상태임"),

    UNFOLLOW_ERROR(409, "F002", "이미 언팔로우 상태임"),

    UNAUTHORIZED_USER_ERROR(401, "C001", "해당 데이터에 접근할 수 없는 사용자"),

    PRIVATE(401, "O001", "ootd 글이 비공개 상태"),

    DELETED(401, "O002", "ootd 글이 삭제된 상태"),

    BLOCKED(401, "O003", "ootd 글이 차단된 상태"),

    OVER_REPORT(401, "O004", "ootd 글이 신고를 많이 먹은 상태"),

    NOT_LARGE_CATEGORY(400, "S001", "상위 카테고리가 아닙니다"),

    NOT_REGISTERED_SIZE(400, "S002", "카테고리에 사이즈 등록 필요"),

    INVALID_CATEGORY_AND_SIZE(400, "C002", "카테고리에 속한 사이즈가 아님"),

    DUPLICATE_BRAND_NAME(409, "B001", "브랜드 이름이 중복됩니다."),

    REQUIRED_BRAND_NAME(400, "B002", "브랜드 이름을 입력해주세요."),

    REQUIRED_DETAIL_CATEGORY(404, "C003", "하위 카테고리를 선택해주세요."),

    NOT_FOUND_BRAND_ID(404, "B003", "유효하지 않은 브랜드 ID"),

    NOT_FOUND_CATEGORY_ID(404, "C002", "유효하지 않은 카테고리 ID"),

    NOT_FOUND_SIZE_ID(404, "S002", "유효하지 않은 사이즈 ID"),

    NOT_FOUND_COLOR_ID(404, "C003", "유효하지 않은 색 ID"),

    NOT_FOUND_CLOTHES_ID(404, "C004", "유효하지 않은 옷 ID"),

    NOT_FOUND_USER_ID(404, "U002", "유효하지 않은 유저 ID"),

    NOT_FOUND_OOTD_ID(404, "O005", "유효하지 않은 ootd ID"),

    NOT_FOUND_REPORT_ID(404, "R001", "유효하지 않은 신고 ID"),

    NOT_DUPLICATE_REPORT(200, "R002", "신고는 한번만 가능합니다."),

    CANT_MY_REPORT(400, "R003", "작성자는 신고가 불가능합니다."),

    NOT_FOUNT_COMMENT_ID(404, "CM001", "유효하지 않은 댓글 ID"),

    DUPLICATE_DELETE_COMMENT(404, "CM002", "이미 삭제된 댓글 입니다."),

    NO_TAGGING_USER(404, "CM003", "대댓글에는 반드시 태깅이 있어야 합니다."),

    ALREADY_USER_REGISTER(409, "U003", "회원가입이 완료된 유저입니다."),

    INVALID_IMAGE_URL(400, "I001", "이미지 URL이 유효하지 않습니다."),

    IMAGE_DELETE_FAIL(400, "I002", "이미지 삭제를 실패 했습니다."),

    IMAGE_UPLOAD_FAIL(400, "I003", "이미지 업로드를 실패 했습니다."),

    DELETE_USER_CLOTHES(404, "C006", "삭제된 유저의 옷입니다."),

    NOT_FOUND_USER_BLCOK_ID(404, "UB001", "유효하지 않은 사용자 차단 ID"),

    NOT_AUTH_UNBLOCK_USER(403, "UB002", "본인만 차단을 해제할 수 았습니다."),

    EXISTED_BLOCK_USER(200, "UB003", "이미 차단한 유저입니다."),

    BLOCK_USER_CONTENTS(404, "UB004", "차단한 사용자의 컨텐츠입니다."),

    REQUIRED_BRAND_REQUEST_NAME(400, "BR001", "브랜드 요청 내용은 필수입니다.");

    private final Integer status;

    private final String divisionCode;

    private final String message;
}
