package com.smartpantry.manager;

import android.widget.EditText;
import android.widget.Spinner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class IngredientActivityTest {
    private ActivityController<IngredientActivity> controller;

    @Before
    public void setUp() {
        RuntimeEnvironment.getApplication().deleteDatabase(DatabaseHelper.DATABASE_NAME);
        controller = Robolectric.buildActivity(IngredientActivity.class).setup();
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
        RuntimeEnvironment.getApplication().deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    @Test
    public void addsAValidatedIngredientAndClosesForm() {
        enter(" Tomatoes ", "0,250", 1);
        controller.get().findViewById(R.id.save_ingredient).performClick();
        try (DatabaseHelper database = new DatabaseHelper(controller.get())) {
            PantryItem item = database.getPantryItems().get(0);
            assertEquals("Tomatoes", item.name);
            assertEquals("tomato", item.ingredientKey);
            assertEquals("0.25", item.quantity);
            assertEquals("kg", item.unit);
        }
        assertTrue(controller.get().isFinishing());
    }

    @Test
    public void refusesBlankNameAndZeroQuantity() {
        controller.get().findViewById(R.id.save_ingredient).performClick();
        assertNotNull(((EditText) controller.get().findViewById(R.id.ingredient_name)).getError());
        enter("Rice", "0", 0);
        controller.get().findViewById(R.id.save_ingredient).performClick();
        assertNotNull(((EditText) controller.get().findViewById(R.id.ingredient_quantity)).getError());
        assertFalse(controller.get().isFinishing());
        try (DatabaseHelper database = new DatabaseHelper(controller.get())) {
            assertTrue(database.getPantryItems().isEmpty());
        }
    }

    @Test
    public void preservesUnsavedInputWhenRecreated() {
        enter("Rice", "0.5", 1);
        controller.recreate();
        assertEquals("Rice", ((EditText) controller.get().findViewById(R.id.ingredient_name))
                .getText().toString());
        assertEquals("0.5", ((EditText) controller.get().findViewById(R.id.ingredient_quantity))
                .getText().toString());
        assertEquals(1, ((Spinner) controller.get().findViewById(R.id.ingredient_unit))
                .getSelectedItemPosition());
    }

    private void enter(String name, String quantity, int unit) {
        ((EditText) controller.get().findViewById(R.id.ingredient_name)).setText(name);
        ((EditText) controller.get().findViewById(R.id.ingredient_quantity)).setText(quantity);
        ((Spinner) controller.get().findViewById(R.id.ingredient_unit)).setSelection(unit);
    }
}
