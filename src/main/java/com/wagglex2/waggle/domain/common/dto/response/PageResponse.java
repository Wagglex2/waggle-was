package com.wagglex2.waggle.domain.common.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이지네이션 결과를 표현하는 공통 응답 DTO.
 *
 * <p>Spring Data JPA의 {@link Page} 객체를 API 응답에 직렬화할 때,
 * 내부 구현 클래스({@code PageImpl})를 그대로 반환하면 JSON 구조가 불안정해질 수 있다.
 * 이를 방지하고 명확하고 일관된 JSON 포맷을 제공하기 위해 {@code PageResponse}를 사용한다.</p>
 *
 * <p><b>주요 역할:</b></p>
 * <ul>
 *   <li>Spring Data {@link Page} 객체를 프론트엔드에서 사용하기 쉬운 단순 구조로 변환</li>
 *   <li>페이징 정보({@code pageNumber}, {@code pageSize}, {@code totalElements}, {@code totalPages}, {@code last})를 명시적으로 제공</li>
 *   <li>Swagger/OpenAPI 문서에서 응답 구조를 명확히 표시</li>
 * </ul>
 *
 * <p><b>예시 응답(JSON):</b></p>
 * <pre>
 * {
 *   "content": [ { "id": 1, "content": "좋아요" } ],
 *   "pageNumber": 0,
 *   "pageSize": 5,
 *   "totalElements": 10,
 *   "totalPages": 2,
 *   "last": false
 * }
 * </pre>
 *
 * @param <T> 페이지 내에 포함될 데이터 타입 (예: {@code ReviewResponseDto})
 */
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
