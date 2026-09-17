package com.smartpantry.manager;

public class PantryItem {
    public final long id;
    public final String name;
    public final String ingredientKey;
    public final String quantity;
    public final String unit;

    public PantryItem(long id, String name, String ingredientKey, String quantity, String unit) {
        this.id = id;
        this.name = name;
        this.ingredientKey = ingredientKey;
        this.quantity = quantity;
        this.unit = unit;
    }
}
