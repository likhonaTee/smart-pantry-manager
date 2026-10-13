package com.smartpantry.manager;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class RecipeMatcherTest {
    @Test
    public void acceptsACompleteMatchWithAliasesAndCompatibleUnits() {
        Recipe recipe = recipe(required("egg", "2", "count"), required("tomato", "200", "g"));
        assertTrue(matches(recipe, stock("Eggs", "2", "count"), stock(" TOMATOES ", "0.2", "kg")));
    }

    @Test
    public void excludesOneMissingIngredient() {
        Recipe recipe = recipe(required("egg", "2", "count"), required("tomato", "200", "g"));
        assertFalse(matches(recipe, stock("egg", "2", "count")));
    }

    @Test
    public void excludesInsufficientQuantity() {
        assertFalse(matches(recipe(required("egg", "2", "count")), stock("egg", "1", "count")));
    }

    @Test
    public void neverRoundsAShortageIntoAMatch() {
        Recipe recipe = recipe(required("tomato", "200", "g"));
        assertFalse(matches(recipe, stock("tomato", "0.199", "kg")));
        assertTrue(matches(recipe, stock("tomato", "0.2", "kg")));
    }

    @Test
    public void cannotUseMassVolumeOrCountInterchangeably() {
        Recipe recipe = recipe(required("tomato", "200", "g"));
        assertFalse(matches(recipe, stock("tomato", "200", "ml")));
        assertFalse(matches(recipe, stock("tomato", "200", "count")));
    }

    @Test
    public void sumsDuplicatePantryRowsAfterConvertingUnits() {
        Recipe recipe = recipe(required("rice", "1", "kg"));
        assertTrue(matches(recipe, stock("rice", "500", "g"), stock("rice", "0.5", "kg")));
    }

    @Test
    public void sumsRepeatedRecipeRequirementsInsteadOfReusingStock() {
        Recipe recipe = recipe(required("egg", "2", "count"), required("eggs", "2", "count"));
        assertFalse(matches(recipe, stock("egg", "2", "count")));
        assertTrue(matches(recipe, stock("egg", "4", "count")));
    }

    @Test
    public void combinesRepeatedRequirementsWithDifferentCompatibleUnits() {
        Recipe recipe = recipe(required("rice", "500", "g"), required("rice", "0.5", "kg"));
        assertFalse(matches(recipe, stock("rice", "0.75", "kg")));
        assertTrue(matches(recipe, stock("rice", "1", "kg")));
    }

    @Test
    public void rejectsEmptyPantryAndEmptyRecipe() {
        assertFalse(matches(recipe(required("egg", "1", "count"))));
        assertFalse(matches(recipe(), stock("egg", "2", "count")));
    }

    @Test
    public void rejectsUnsupportedOrInvalidRequirements() {
        assertFalse(matches(recipe(required("rice", "1", "cup")), stock("rice", "1", "cup")));
        assertFalse(matches(recipe(required("rice", "0", "g")), stock("rice", "100", "g")));
        assertFalse(matches(recipe(required("rice", "invalid", "g")), stock("rice", "100", "g")));
    }

    @Test
    public void suggestionsAreAlternativesAndDoNotConsumeStock() {
        PantryItem eggs = stock("egg", "2", "count");
        Recipe first = recipe(required("egg", "2", "count"));
        Recipe second = recipe(required("egg", "1", "count"));
        List<Recipe> results = RecipeMatcher.suggestRecipes(Collections.singletonList(eggs),
                Arrays.asList(first, second));
        assertEquals(2, results.size());
        assertEquals("2", eggs.quantity);
    }

    private boolean matches(Recipe recipe, PantryItem... stock) {
        return !RecipeMatcher.suggestRecipes(Arrays.asList(stock), Collections.singletonList(recipe)).isEmpty();
    }

    private Recipe recipe(RecipeIngredient... ingredients) {
        return new Recipe(1, "Test recipe", "1. Prepare.", Arrays.asList(ingredients));
    }

    private RecipeIngredient required(String name, String quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    private PantryItem stock(String name, String quantity, String unit) {
        return new PantryItem(1, name, name, quantity, unit);
    }
}
