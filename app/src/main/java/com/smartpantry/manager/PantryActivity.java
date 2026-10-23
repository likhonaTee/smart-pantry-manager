package com.smartpantry.manager;

import android.app.Activity;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
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
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
