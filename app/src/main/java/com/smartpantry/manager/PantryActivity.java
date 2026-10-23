package com.smartpantry.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class PantryActivity extends Activity {
    private DatabaseHelper database;
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        database = new DatabaseHelper(this);
        adapter = new PantryAdapter(this);
        ListView list = findViewById(R.id.pantry_list);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.pantry_empty));
        findViewById(R.id.add_ingredient).setOnClickListener(view ->
                startActivity(new Intent(this, IngredientActivity.class)));
        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, IngredientActivity.class);
            intent.putExtra(IngredientActivity.EXTRA_INGREDIENT_ID, adapter.getItem(position).id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView error = findViewById(R.id.pantry_error);
        try {
            adapter.setItems(database.getPantryItems());
            error.setText("");
        } catch (SQLiteException exception) {
            error.setText(R.string.pantry_load_error);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pantry_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_recipes) {
            startActivity(new Intent(this, SuggestionsActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.nav_pantry) {
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
