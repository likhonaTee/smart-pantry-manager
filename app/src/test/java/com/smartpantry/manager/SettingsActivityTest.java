package com.smartpantry.manager;

import android.widget.RadioGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SettingsActivityTest {
    private ActivityController<SettingsActivity> controller;

    @Before
    public void setUp() {
        UnitPreferences.setLargeUnits(RuntimeEnvironment.getApplication(), false);
        RuntimeEnvironment.getApplication().deleteDatabase(DatabaseHelper.DATABASE_NAME);
        controller = Robolectric.buildActivity(SettingsActivity.class).setup();
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
        UnitPreferences.setLargeUnits(RuntimeEnvironment.getApplication(), false);
        RuntimeEnvironment.getApplication().deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    @Test
    public void remembersTheSelectionWhenRecreated() {
        ((RadioGroup) controller.get().findViewById(R.id.units_group)).check(R.id.units_large);
        assertTrue(UnitPreferences.usesLargeUnits(controller.get()));
        controller.recreate();
        assertEquals(R.id.units_large, ((RadioGroup) controller.get().findViewById(R.id.units_group))
                .getCheckedRadioButtonId());
    }

    @Test
    public void formatsMetricUnitsWithoutChangingCount() {
        assertEquals("500 g", UnitPreferences.formatQuantity(controller.get(), "0.5", "kg"));
        UnitPreferences.setLargeUnits(controller.get(), true);
        assertEquals("0.5 kg", UnitPreferences.formatQuantity(controller.get(), "500", "g"));
        assertEquals("0.25 l", UnitPreferences.formatQuantity(controller.get(), "250", "ml"));
        assertEquals("2 count", UnitPreferences.formatQuantity(controller.get(), "2", "count"));
    }

    @Test
    public void preferenceDoesNotChangeSavedStockOrMatching() {
        try (DatabaseHelper database = new DatabaseHelper(controller.get())) {
            database.addPantryItem("Eggs", "egg", "2", "count");
            long butter = database.addPantryItem("Butter", "butter", "10", "g");
            List<Recipe> before = RecipeMatcher.suggestRecipes(database.getPantryItems(), database.getRecipes());
            ((RadioGroup) controller.get().findViewById(R.id.units_group)).check(R.id.units_large);
            List<Recipe> after = RecipeMatcher.suggestRecipes(database.getPantryItems(), database.getRecipes());
            assertEquals("10", database.getPantryItem(butter).quantity);
            assertEquals("g", database.getPantryItem(butter).unit);
            assertEquals(1, before.size());
            assertEquals(before.size(), after.size());
            assertEquals(before.get(0).id, after.get(0).id);
        }
    }
}
