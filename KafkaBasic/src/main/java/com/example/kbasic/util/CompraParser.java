package com.example.kbasic.util;

import com.example.kbasic.model.Compra;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CompraParser {

    private static final Pattern TO_STRING_PATTERN = Pattern.compile(
            "Compra\\{id=(?<id>[-]?[0-9]+), userId=(?<userId>[-]?[0-9]+), value=(?<value>[-]?[0-9]+), orderId=(?<orderId>[-]?[0-9]+)}`?");

    private CompraParser() {}

    public static Compra parseFromToString(String text) {
        if (text == null) return null;
        Matcher m = TO_STRING_PATTERN.matcher(text.trim());
        if (!m.find()) {
            return null;
        }
        try {
            Integer id = Integer.valueOf(m.group("id"));
            Integer userId = Integer.valueOf(m.group("userId"));
            Integer value = Integer.valueOf(m.group("value"));
            Integer orderId = Integer.valueOf(m.group("orderId"));
            return new Compra(id, userId, value, orderId);
        } catch (Exception e) {
            return null;
        }
    }
}
