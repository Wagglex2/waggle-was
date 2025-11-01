package com.wagglex2.waggle.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 페이지네이션(Pagination) 관련 글로벌 설정 클래스.
 *
 * <p>application.properties 외부 설정 파일에서 "pagination" prefix로 값을 주입받는다.</p>
 *
 * <h3>설명</h3>
 * <ul>
 *   <li><b>defaultSize</b> - 클라이언트가 size 파라미터를 전달하지 않았을 때의 기본 페이지 크기</li>
 *   <li><b>maxSize</b> - 클라이언트가 요청할 수 있는 최대 페이지 크기 (DoS 방지 목적)</li>
 *   <li><b>maxPageNumber</b> - 허용되는 최대 페이지 번호 (너무 깊은 페이지 요청 차단)</li>
 * </ul>
 *
 * <h3>주의사항</h3>
 * <ul>
 *   <li>Spring Data JPA의 기본 Pageable은 잘못된 size/page 값을 교정(default로 바꿈)하므로,
 *       예외로 막으려면 별도 Validator에서 검사해야 한다.</li>
 * </ul>
 */
@Component
@ConfigurationProperties(prefix = "pagination")
@Getter
public class PaginationProperties {
    private int maxSize = 100;
    private int defaultSize = 20;
    private int maxPageNumber = 10000;
}
