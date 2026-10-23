package com.smartpantry.manager;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class IngredientActivity extends Activity {
    private DatabaseHelper database;
    private EditText nameField;
    private EditText quantityField;
    private Spinner unitSpinner;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);
        setTitle(R.string.add_ingredient);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        database = new DatabaseHelper(this);
        nameField = findViewById(R.id.ingredient_name);
        quantityField = findViewById(R.id.ingredient_quantity);
        unitSpinner = findViewById(R.id.ingredient_unit);
        errorMessage = findViewById(R.id.ingredient_error);
        findViewById(R.id.save_ingredient).setOnClickListener(view -> saveIngredient());
        if (savedInstanceState != null) {
            nameField.setText(savedInstanceState.getString("name"));
            quantityField.setText(savedInstanceState.getString("quantity"));
            unitSpinner.setSelection(savedInstanceState.getInt("unit"));
        }
    }

    private void saveIngredient() {
        nameField.setError(null);
        quantityField.setError(null);
        errorMessage.setText("");
        String name = IngredientUtils.cleanName(nameField.getText().toString());
        if (name.isEmpty() || name.length() > 60) {
            nameField.setError(getString(R.string.invalid_name));
            nameField.requestFocus();
            return;
        }
        String quantity = IngredientUtils.validQuantity(quantityField.getText().toString());
        if (quantity == null) {
            quantityField.setError(getString(R.string.invalid_quantity));
            quantityField.requestFocus();
            return;
        }
        String unit = unitSpinner.getSelectedItem().toString();
        try {
            database.addPantryItem(name, IngredientUtils.canonicalName(name), quantity, unit);
            finish();
        } catch (SQLiteException exception) {
            errorMessage.setText(R.string.ingredient_save_error);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putString("name", nameField.getText().toString());
        state.putString("quantity", quantityField.getText().toString());
        state.putInt("unit", unitSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(state);
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
