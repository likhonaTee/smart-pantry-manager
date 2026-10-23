package com.smartpantry.manager;

import org.junit.Test;
import java.math.BigDecimal;

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

    @Test
    public void convertsCompatibleUnitsExactly() {
        assertEquals(0, new BigDecimal("200").compareTo(
                IngredientUtils.toBaseQuantity("0.2", "kg")));
        assertEquals(0, new BigDecimal("1000").compareTo(
                IngredientUtils.toBaseQuantity("1", "l")));
        assertEquals(0, new BigDecimal("0.125").compareTo(
                IngredientUtils.toBaseQuantity("0.125", "g")));
        assertEquals(0, new BigDecimal("2").compareTo(
                IngredientUtils.toBaseQuantity("2", "count")));
    }

    @Test
    public void keepsMassVolumeAndCountSeparate() {
        assertEquals("g", IngredientUtils.baseUnit("kg"));
        assertEquals("ml", IngredientUtils.baseUnit("l"));
        assertEquals("count", IngredientUtils.baseUnit("count"));
        assertNotEquals(IngredientUtils.baseUnit("g"), IngredientUtils.baseUnit("ml"));
        assertNotEquals(IngredientUtils.baseUnit("count"), IngredientUtils.baseUnit("g"));
        assertNull(IngredientUtils.baseUnit("cup"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotGuessConversionsForUnsupportedUnits() {
        IngredientUtils.toBaseQuantity("1", "cup");
    }
}
