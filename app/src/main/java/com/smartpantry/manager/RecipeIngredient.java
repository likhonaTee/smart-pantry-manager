package com.smartpantry.manager;

public class RecipeIngredient {
    public final String ingredientKey;
    public final String quantity;
    public final String unit;

    public RecipeIngredient(String ingredientKey, String quantity, String unit) {
        this.ingredientKey = ingredientKey;
        this.quantity = quantity;
        this.unit = unit;
    }
}
