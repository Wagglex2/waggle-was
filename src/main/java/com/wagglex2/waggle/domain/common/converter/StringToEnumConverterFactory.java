package com.wagglex2.waggle.domain.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 모든 enum에 대해 String -> Enum 변환
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
