package com.smartpantry.manager;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsActivity extends Activity {
    private DatabaseHelper database;
    private ArrayAdapter<String> adapter;
    private List<Recipe> suggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        setTitle(R.string.suggested_recipes);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        database = new DatabaseHelper(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        ListView list = findViewById(R.id.suggestions_list);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.suggestions_empty));
        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, suggestions.get(position).id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView empty = findViewById(R.id.suggestions_empty);
        adapter.clear();
        suggestions.clear();
        try {
            suggestions = RecipeMatcher.suggestRecipes(database.getPantryItems(), database.getRecipes());
            List<String> names = new ArrayList<>();
            for (Recipe recipe : suggestions) {
                names.add(recipe.name);
            }
            adapter.addAll(names);
            empty.setText(R.string.no_recipes_match);
        } catch (SQLiteException exception) {
            empty.setText(R.string.recipes_load_error);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pantry_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (item.getItemId() == android.R.id.home || item.getItemId() == R.id.nav_pantry) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.nav_recipes) {
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
