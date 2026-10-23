package com.smartpantry.manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Spinner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

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

    @Test
    public void editsTheExistingRecordAfterRecreation() {
        long id;
        try (DatabaseHelper database = new DatabaseHelper(controller.get())) {
            id = database.addPantryItem("Eggs", "egg", "2", "count");
        }
        openForEdit(id);
        assertEquals("2", ((EditText) controller.get().findViewById(R.id.ingredient_quantity))
                .getText().toString());
        enter("Eggs", "3", 4);
        controller.recreate();
        controller.get().findViewById(R.id.save_ingredient).performClick();
        try (DatabaseHelper database = new DatabaseHelper(controller.get())) {
            assertEquals(1, database.getPantryItems().size());
            assertEquals("3", database.getPantryItem(id).quantity);
        }
    }

    @Test
    public void deletionRequiresConfirmationAndCanBeCancelled() {
        long id;
        try (DatabaseHelper database = new DatabaseHelper(controller.get())) {
            id = database.addPantryItem("Rice", "rice", "500", "g");
        }
        openForEdit(id);
        controller.get().findViewById(R.id.delete_ingredient).performClick();
        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        try (DatabaseHelper database = new DatabaseHelper(controller.get())) {
            assertNotNull(database.getPantryItem(id));
        }
        controller.get().findViewById(R.id.delete_ingredient).performClick();
        ShadowAlertDialog.getLatestAlertDialog().getButton(DialogInterface.BUTTON_POSITIVE)
                .performClick();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        try (DatabaseHelper database = new DatabaseHelper(controller.get())) {
            assertNull(database.getPantryItem(id));
        }
        assertTrue(controller.get().isFinishing());
    }

    @Test
    public void missingRecordClosesSafely() {
        openForEdit(99999);
        assertTrue(controller.get().isFinishing());
    }

    private void openForEdit(long id) {
        controller.pause().stop().destroy();
        Intent intent = new Intent(RuntimeEnvironment.getApplication(), IngredientActivity.class);
        intent.putExtra(IngredientActivity.EXTRA_INGREDIENT_ID, id);
        controller = Robolectric.buildActivity(IngredientActivity.class, intent).setup();
    }

    private void enter(String name, String quantity, int unit) {
        ((EditText) controller.get().findViewById(R.id.ingredient_name)).setText(name);
        ((EditText) controller.get().findViewById(R.id.ingredient_quantity)).setText(quantity);
        ((Spinner) controller.get().findViewById(R.id.ingredient_unit)).setSelection(unit);
    }
}
