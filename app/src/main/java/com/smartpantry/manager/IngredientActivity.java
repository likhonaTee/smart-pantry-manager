package com.smartpantry.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class IngredientActivity extends Activity {
    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";
    private long ingredientId = -1;
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
        findViewById(R.id.delete_ingredient).setOnClickListener(view -> confirmDelete());
        if (getIntent().hasExtra(EXTRA_INGREDIENT_ID)) {
            ingredientId = getIntent().getLongExtra(EXTRA_INGREDIENT_ID, -1);
            try {
                PantryItem item = database.getPantryItem(ingredientId);
                if (item == null) {
                    ingredientUnavailable();
                    return;
                }
                setTitle(R.string.edit_ingredient);
                findViewById(R.id.delete_ingredient).setVisibility(android.view.View.VISIBLE);
                if (savedInstanceState == null) {
                    nameField.setText(item.name);
                    quantityField.setText(item.quantity);
                    String[] units = getResources().getStringArray(R.array.ingredient_units);
                    for (int i = 0; i < units.length; i++) {
                        if (units[i].equals(item.unit)) {
                            unitSpinner.setSelection(i);
                        }
                    }
                }
            } catch (SQLiteException exception) {
                ingredientUnavailable();
                return;
            }
        }
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
            String key = IngredientUtils.canonicalName(name);
            if (ingredientId == -1) {
                database.addPantryItem(name, key, quantity, unit);
            } else if (!database.updatePantryItem(ingredientId, name, key, quantity, unit)) {
                ingredientUnavailable();
                return;
            }
            finish();
        } catch (SQLiteException exception) {
            errorMessage.setText(R.string.ingredient_save_error);
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_ingredient_title)
                .setMessage(R.string.delete_ingredient_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete_ingredient, (dialog, which) -> {
                    try {
                        if (database.deletePantryItem(ingredientId)) {
                            finish();
                        } else {
                            ingredientUnavailable();
                        }
                    } catch (SQLiteException exception) {
                        errorMessage.setText(R.string.ingredient_delete_error);
                    }
                })
                .show();
    }

    private void ingredientUnavailable() {
        Toast.makeText(this, R.string.ingredient_unavailable, Toast.LENGTH_LONG).show();
        finish();
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
