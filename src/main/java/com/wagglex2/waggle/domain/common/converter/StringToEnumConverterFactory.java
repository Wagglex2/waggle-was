package com.wagglex2.waggle.domain.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 모든 Enum 타입에 대해 {@link String} -> {@link Enum} 변환 지원
 *
 * <p>특징:</p>
 * <ul>
 *     <li>Controller에서 쿼리 파라미터로 전달된 문자열을 Enum으로 자동 변환</li>
 *     <li>소문자, 공백, 하이픈(-) 등을 Enum 형식에 맞게 처리</li>
 *     <li>예: "my-value" -> "MY_VALUE"로 변환 후 Enum.valueOf 적용</li>
 * </ul>
 *
 * <p>사용 목적:</p>
 * <ul>
 *     <li>Controller에서 소문자 형태의 Enum 값을 바인딩할 때 편리하게 사용</li>
 * </ul>
 */
public final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter(targetType);
    }

    private final class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

        private Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            String formatted = source.trim()
                                     .toUpperCase()
                                     .replace('-', '_');

            return (T) Enum.valueOf(this.enumType, formatted);
        }
    }
}
