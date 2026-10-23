package com.smartpantry.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.math.BigDecimal;

public class UnitPreferences {
    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences("display_units", Context.MODE_PRIVATE);
    }

    public static boolean usesLargeUnits(Context context) {
        return preferences(context).getBoolean("large_units", false);
    }

    public static void setLargeUnits(Context context, boolean enabled) {
        preferences(context).edit().putBoolean("large_units", enabled).apply();
    }

    public static String formatQuantity(Context context, String quantity, String unit) {
        String displayUnit = IngredientUtils.baseUnit(unit);
        BigDecimal amount = IngredientUtils.toBaseQuantity(quantity, unit);
        if (usesLargeUnits(context) && (displayUnit.equals("g") || displayUnit.equals("ml"))) {
            amount = amount.divide(new BigDecimal("1000"));
            displayUnit = displayUnit.equals("g") ? "kg" : "l";
        }
        return context.getString(R.string.quantity_and_unit,
                amount.stripTrailingZeros().toPlainString(), displayUnit);
    }
}
