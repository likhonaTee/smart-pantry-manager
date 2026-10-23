package com.smartpantry.manager;

import java.util.List;

public class Recipe {
    public final long id;
    public final String name;
    public final String method;
    public final List<RecipeIngredient> ingredients;

    public Recipe(long id, String name, String method, List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.method = method;
        this.ingredients = ingredients;
    }
}
