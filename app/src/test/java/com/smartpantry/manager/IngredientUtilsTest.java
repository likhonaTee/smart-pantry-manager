package com.smartpantry.manager;

import org.junit.Test;

import static org.junit.Assert.*;

public class IngredientUtilsTest {
    @Test
    public void normalizesCommonIngredientNames() {
        assertEquals("tomato", IngredientUtils.canonicalName("  TOMATOES  "));
        assertEquals("potato", IngredientUtils.canonicalName("Potatoes"));
        assertEquals("egg", IngredientUtils.canonicalName("Eggs"));
        assertEquals("lemon juice", IngredientUtils.canonicalName(" Lemon   Juice "));
        assertEquals("couscous", IngredientUtils.canonicalName("Couscous"));
    }

    @Test
    public void acceptsExactPositiveDecimalQuantities() {
        assertEquals("0.125", IngredientUtils.validQuantity("0,125"));
        assertEquals("0.5", IngredientUtils.validQuantity(".5"));
        assertEquals("1000000", IngredientUtils.validQuantity("1000000.000"));
        assertEquals("250", IngredientUtils.validQuantity("250.000"));
    }

    @Test
    public void rejectsInvalidOrUnsupportedQuantityFormats() {
        String[] invalid = {"", "0", "-1", "NaN", "Infinity", "1e3", "1,000,000", "1.2,3",
                "1 000", "0.0001", "1000000.001", "2kg"};
        for (String input : invalid) {
            assertNull(input, IngredientUtils.validQuantity(input));
        }
    }
}
