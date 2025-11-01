package com.wagglex2.waggle.common.validator;

import com.wagglex2.waggle.common.config.PaginationProperties;
import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * PageableValidator
 *
 * <p>Pageable 요청 객체에 포함된 페이지 번호(page), 페이지 크기(size), 정렬(sort) 값을 검증하는 유틸리티 컴포넌트.</p>
 *
 * <h3>검증 목적</h3>
 * <ul>
 *   <li>비정상적인 페이지 크기(size < 1 등) 요청 차단</li>
 *   <li>너무 큰 pageNumber 요청 제한 (DoS 방지)</li>
 *   <li>허용되지 않은 정렬 필드로 인한 보안/성능 문제 방지</li>
 * </ul>
 *
 * <p>Spring Data JPA의 Pageable은 잘못된 값이 들어와도 내부적으로 보정(기본값 20 등)을 수행하지만,
 * API 정책상 명시적인 예외를 던져 클라이언트에 오류를 알려주는 것이 더 안전하다.</p>
 */
@Component
@RequiredArgsConstructor
public class PageableValidator {

    private final PaginationProperties paginationProperties;

    public Pageable validate(Pageable pageable) {
        validatePageSize(pageable.getPageSize());
        validatePageNumber(pageable.getPageNumber());
        return pageable;
    }

    public void validateSort(Pageable pageable, Set<String> allowedProperties) {
        if (pageable.getSort().isUnsorted()) {
            return;
        }

        Set<String> requestProperties = pageable.getSort().stream()
                .map(Sort.Order::getProperty)
                .collect(Collectors.toSet());

        for (String property : requestProperties) {
            if (!allowedProperties.contains(property)) {
                throw new BusinessException(
                        ErrorCode.INVALID_SORT_PROPERTY,
                        String.format(
                                "%s는 정렬할 수 없는 필드입니다. 허용된 필드: [%s]",
                                property, String.join(", ", allowedProperties)
                        )
                );
            }
        }
    }

    private void validatePageSize(int pageSize) {
        if (pageSize < 1) {
            throw new BusinessException(
                    ErrorCode.PAGE_SIZE_OUT_OF_RANGE,
                    String.format("페이지 크기는 1 이상이어야 합니다. (요청: %d)", pageSize)
            );
        }
    }

    private void validatePageNumber(int pageNumber) {
        if (pageNumber < 0) {
            throw new BusinessException(
                    ErrorCode.PAGE_INDEX_OUT_OF_RANGE,
                    String.format("페이지 번호는 0 이상이어야 합니다. (요청: %d)", pageNumber)
            );
        }

        if (pageNumber > paginationProperties.getMaxPageNumber()) {
            throw new BusinessException(
                    ErrorCode.PAGE_INDEX_OUT_OF_RANGE,
                    String.format("페이지 번호는 최대 %d까지 가능합니다. (요청: %d)",
                            paginationProperties.getMaxPageNumber(), pageNumber)
            );
        }
    }
}
