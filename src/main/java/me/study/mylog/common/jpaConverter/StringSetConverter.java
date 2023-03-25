package me.study.mylog.common.jpaConverter;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class StringSetConverter implements AttributeConverter<Set<String>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return attribute.stream()
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .collect(Collectors.toSet());
    }
}