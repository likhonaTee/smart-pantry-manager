package com.smartpantry.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "smart_pantry.db";
    private final Context context;
    private static final String[] PANTRY_COLUMNS = {
            "id", "name", "ingredient_key", "quantity", "unit"
    };

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, 1);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items ("
                + "id INTEGER PRIMARY KEY, name TEXT NOT NULL CHECK(length(trim(name)) BETWEEN 1 AND 60), "
                + "ingredient_key TEXT NOT NULL CHECK(length(trim(ingredient_key)) > 0), "
                + "quantity TEXT NOT NULL CHECK(CAST(quantity AS REAL) > 0), "
                + "unit TEXT NOT NULL CHECK(unit IN ('g','kg','ml','l','count')))");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY, "
                + "name TEXT NOT NULL UNIQUE, method TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY, "
                + "recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE, "
                + "ingredient_key TEXT NOT NULL, quantity TEXT NOT NULL CHECK(CAST(quantity AS REAL) > 0), "
                + "unit TEXT NOT NULL CHECK(unit IN ('g','kg','ml','l','count')))");
        // SQLiteOpenHelper runs table creation and seeding in one transaction.
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLiteException("A database migration is required.");
    }

    private void seedRecipes(SQLiteDatabase db) {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.recipes), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            JSONArray recipes = new JSONArray(json.toString());
            for (int i = 0; i < recipes.length(); i++) {
                JSONObject recipe = recipes.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id", i + 1);
                values.put("name", recipe.getString("name"));
                values.put("method", recipe.getString("method"));
                long recipeId = db.insertOrThrow("recipes", null, values);
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                for (int j = 0; j < ingredients.length(); j++) {
                    JSONObject ingredient = ingredients.getJSONObject(j);
                    ContentValues required = new ContentValues();
                    required.put("recipe_id", recipeId);
                    required.put("ingredient_key", ingredient.getString("name"));
                    required.put("quantity", ingredient.getString("quantity"));
                    required.put("unit", ingredient.getString("unit"));
                    db.insertOrThrow("recipe_ingredients", null, required);
                }
            }
        } catch (IOException | JSONException error) {
            throw new SQLiteException("Could not load starter recipes.", error);
        }
    }

    public List<PantryItem> getPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("pantry_items", PANTRY_COLUMNS,
                null, null, null, null, "name COLLATE NOCASE, id")) {
            while (cursor.moveToNext()) {
                items.add(readPantryItem(cursor));
            }
        }
        return items;
    }

    public PantryItem getPantryItem(long id) {
        try (Cursor cursor = getReadableDatabase().query("pantry_items", PANTRY_COLUMNS,
                "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readPantryItem(cursor) : null;
        }
    }

    private PantryItem readPantryItem(Cursor cursor) {
        return new PantryItem(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4));
    }

    public long addPantryItem(String name, String key, String quantity, String unit) {
        return getWritableDatabase().insertOrThrow("pantry_items", null,
                pantryValues(name, key, quantity, unit));
    }

    public boolean updatePantryItem(long id, String name, String key, String quantity, String unit) {
        return getWritableDatabase().update("pantry_items", pantryValues(name, key, quantity, unit),
                "id = ?", new String[]{String.valueOf(id)}) == 1;
    }

    public boolean deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_items", "id = ?",
                new String[]{String.valueOf(id)}) == 1;
    }

    private ContentValues pantryValues(String name, String key, String quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("ingredient_key", key);
        values.put("quantity", quantity);
        values.put("unit", unit);
        return values;
    }

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("recipes", new String[]{"id", "name", "method"},
                null, null, null, null, "name COLLATE NOCASE")) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                recipes.add(new Recipe(id, cursor.getString(1), cursor.getString(2),
                        getRecipeIngredients(id)));
            }
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        try (Cursor cursor = getReadableDatabase().query("recipes", new String[]{"id", "name", "method"},
                "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (!cursor.moveToFirst()) {
                return null;
            }
            return new Recipe(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                    getRecipeIngredients(id));
        }
    }

    private List<RecipeIngredient> getRecipeIngredients(long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("recipe_ingredients",
                new String[]{"ingredient_key", "quantity", "unit"}, "recipe_id = ?",
                new String[]{String.valueOf(recipeId)}, null, null, "id")) {
            while (cursor.moveToNext()) {
                ingredients.add(new RecipeIngredient(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2)));
            }
        }
        return ingredients;
    }
}
