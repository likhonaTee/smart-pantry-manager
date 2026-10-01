package com.smartpantry.manager;

import java.math.BigDecimal;
import java.util.Locale;

public class IngredientUtils {
    public static String cleanName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    public static String canonicalName(String name) {
        String key = cleanName(name).toLowerCase(Locale.ROOT);
        switch (key) {
            case "tomatoes": return "tomato";
            case "potatoes": return "potato";
            case "eggs": return "egg";
            case "onions": return "onion";
            case "bananas": return "banana";
            case "carrots": return "carrot";
            case "cucumbers": return "cucumber";
            default: return key;
        }
    }

    public static String validQuantity(String input) {
        String text = input.trim().replace(',', '.');
        if (text.length() > 16 || !text.matches("(?:[0-9]+(?:\\.[0-9]{1,3})?|\\.[0-9]{1,3})")) {
            return null;
        }
        BigDecimal quantity = new BigDecimal(text);
        if (quantity.signum() <= 0 || quantity.compareTo(new BigDecimal("1000000")) > 0) {
            return null;
        }
        return quantity.stripTrailingZeros().toPlainString();
    }
}
