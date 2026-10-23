package com.smartpantry.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeMatcher {
    public static List<Recipe> suggestRecipes(List<PantryItem> pantry, List<Recipe> recipes) {
        Map<String, BigDecimal> stock = new HashMap<>();
        for (PantryItem item : pantry) {
            addQuantity(stock, item.ingredientKey, item.quantity, item.unit);
        }
        List<Recipe> suggestions = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(stock, recipe)) {
                suggestions.add(recipe);
            }
        }
        return suggestions;
    }

    private static boolean canMake(Map<String, BigDecimal> stock, Recipe recipe) {
        if (recipe.ingredients.isEmpty()) {
            return false;
        }
        Map<String, BigDecimal> required = new HashMap<>();
        for (RecipeIngredient ingredient : recipe.ingredients) {
            if (!addQuantity(required, ingredient.ingredientKey, ingredient.quantity, ingredient.unit)) {
                return false;
            }
        }
        // Every required total must be present in sufficient quantity.
        for (String key : required.keySet()) {
            BigDecimal available = stock.get(key);
            if (available == null || available.compareTo(required.get(key)) < 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean addQuantity(Map<String, BigDecimal> totals, String name,
                                       String quantity, String unit) {
        String baseUnit = IngredientUtils.baseUnit(unit);
        String ingredient = IngredientUtils.canonicalName(name);
        if (baseUnit == null || ingredient.isEmpty()) {
            return false;
        }
        try {
            BigDecimal amount = IngredientUtils.toBaseQuantity(quantity, unit);
            if (amount.signum() <= 0) {
                return false;
            }
            String key = ingredient + "|" + baseUnit;
            BigDecimal previous = totals.getOrDefault(key, BigDecimal.ZERO);
            totals.put(key, previous.add(amount));
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
