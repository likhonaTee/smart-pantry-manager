package com.smartpantry.manager;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeDetailActivity extends Activity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        setTitle(R.string.recipe_details);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        database = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long id = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        try {
            Recipe recipe = database.getRecipe(id);
            if (recipe == null) {
                recipeUnavailable();
                return;
            }
            ((TextView) findViewById(R.id.recipe_name)).setText(recipe.name);
            ((TextView) findViewById(R.id.recipe_method)).setText(recipe.method);
            StringBuilder ingredients = new StringBuilder();
            for (RecipeIngredient ingredient : recipe.ingredients) {
                ingredients.append(ingredient.ingredientKey).append(": ");
                ingredients.append(UnitPreferences.formatQuantity(this,
                        ingredient.quantity, ingredient.unit)).append("\n");
            }
            ((TextView) findViewById(R.id.recipe_ingredients)).setText(ingredients.toString().trim());
        } catch (SQLiteException exception) {
            recipeUnavailable();
        }
    }

    private void recipeUnavailable() {
        Toast.makeText(this, R.string.recipe_unavailable, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
