package com.wagglex2.waggle.domain.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;


class KomoranUtilTest {

    KomoranUtil komoranUtil = new KomoranUtil();

    @Test
    @DisplayName("입력 문자열로부터 명사를 추출한다.")
    void extractNouns() {
        // given
        String input1 = "소공 웹 백엔드 자바 스프링";
        String input2 = "운영체제과제3학년";

        // when
        Set<String> res1 = komoranUtil.getNouns(input1);
        Set<String> res2 = komoranUtil.getNouns(input2);

        // then
        assertThat(res1).containsExactlyInAnyOrder("소공", "웹", "백엔드", "자바", "스프링");
        assertThat(res2).containsExactlyInAnyOrder("운영체제", "과제", "3학년");
    }
}