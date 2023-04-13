package com.absabanking.converter;

import com.absabanking.enums.EPreferredContactType;
import com.absabanking.enums.ESex;

import javax.persistence.AttributeConverter;
import java.util.Arrays;

public class EPreferredContactTypeConverter  implements AttributeConverter<EPreferredContactType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EPreferredContactType attribute) {
        if (attribute == null)
            return null;

        return Arrays.stream(EPreferredContactType.values())
                .filter(item -> item.equals(attribute)).findFirst().get().ordinal();
    }

    @Override
    public EPreferredContactType convertToEntityAttribute(Integer ordinal) {
        if (ordinal == null)
            return null;

        switch (ordinal) {
            case 1:
                return EPreferredContactType.SMS;

            case 2:
                return EPreferredContactType.EMAIL;

            default:
                throw new IllegalArgumentException(String.format("Illegal ordinal passed to method %s ", ordinal));
        }
    }


    public static class  GenderConverter {
        public static String genderConverter(String sexType) {
            if (sexType == ESex.FEMALE.toString()) {
                return "Mr";
            } else return "Mrs";
        }
    }
}
