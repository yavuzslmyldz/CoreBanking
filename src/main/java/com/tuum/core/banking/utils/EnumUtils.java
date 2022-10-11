package com.tuum.core.banking.utils;

import java.util.Arrays;
import java.util.EnumSet;

public final class EnumUtils {

    public static boolean isMember(String name, Class<? extends Enum> enumType) {
        for(Enum en : enumType.getEnumConstants()){
            if(en.name().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
