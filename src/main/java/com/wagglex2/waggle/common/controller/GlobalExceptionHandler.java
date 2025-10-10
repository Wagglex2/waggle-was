package com.wagglex2.waggle.common.controller;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.response.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     * <p>
     * 서비스 또는 도메인 로직에서 발생하는 비즈니스 예외를 처리하여,
     * 클라이언트에게 HTTP 상태 코드와 ErrorCode, 메시지를 전달한다.
     *
     * @param ex {@link BusinessException}
     * @return {@link ResponseEntity} - {@link ApiResponse}를 포함한 에러 응답
     * @author 오재민
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        HttpStatus httpStatus = ex.getErrorCode().getHttpStatus();
        String code = ex.getErrorCode().getCode();
        String message = ex.getMessage();

        return ResponseEntity.status(httpStatus)
                .body(ApiResponse.error(code, message));
    }

    /**
     * Validation 예외 처리
     * <p>
     * 요청 DTO에서 {@code @Valid} 또는 {@code @Validated}를 사용해 검증할 때
     * 발생하는 모든 필드 예외를 처리하여,
     * 클라이언트에게 HTTP 상태 코드와 메시지를 전달한다.
     * </p>
     *
     * @param ex {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity} - {@link ApiResponse}를 포함한 Validation 실패 응답
     * @author 오재민
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidationError>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ValidationError> errorInfo = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.VALIDATION_FAILED, errorInfo));
    }

    /**
     * 컨트롤러에서 전달된 파라미터 타입이 예상과 다른 경우 발생하는 예외를 처리
     *
     * <p>특히 Enum 타입 파라미터에 잘못된 문자열이 전달될 때 발생하는 {@link MethodArgumentTypeMismatchException}를 처리</p>
     *
     * <p>예시:</p>
     * <ul>
     *     <li>Enum 타입 파라미터에 허용되지 않은 문자열이 들어온 경우 (예: "banana" → Position enum)</li>
     *     <li>숫자 타입 파라미터에 문자열이 들어온 경우 (예: "abc" → int)</li>
     * </ul>
     *
     * @param ex {@link MethodArgumentTypeMismatchException}
     * @return {@link ResponseEntity} - {@link ApiResponse}를 포함한 타입 불일치 실패 응답
     * @author 오재민
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "쿼리 파라미터 '%s'의 값 '%s'이(가) 유효하지 않습니다.",
                ex.getName(),
                ex.getValue()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_ENUM_VALUE, message));
    }
}
