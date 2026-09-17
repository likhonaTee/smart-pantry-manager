package com.smartpantry.manager;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class DatabaseHelperTest {
    private Context context;
    private DatabaseHelper helper;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
        helper = new DatabaseHelper(context);
    }

    @After
    public void tearDown() {
        helper.close();
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    @Test
    public void seedsTwentyCompleteRecipesOnlyOnce() {
        assertEquals(20, helper.getRecipes().size());
        Set<String> names = new HashSet<>();
        for (Recipe recipe : helper.getRecipes()) {
            assertTrue(names.add(recipe.name));
            assertFalse(recipe.method.isEmpty());
            assertFalse(recipe.ingredients.isEmpty());
            for (RecipeIngredient ingredient : recipe.ingredients) {
                assertFalse(ingredient.ingredientKey.isEmpty());
                assertTrue(new BigDecimal(ingredient.quantity).signum() > 0);
                assertTrue("g kg ml l count".contains(ingredient.unit));
            }
        }
        reopen();
        assertEquals(20, helper.getRecipes().size());
        assertTrue(helper.getPantryItems().isEmpty());
    }

    @Test
    public void pantryChangesPersistAcrossReopening() {
        long id = helper.addPantryItem("Eggs", "egg", "2", "count");
        reopen();
        assertEquals("2", helper.getPantryItem(id).quantity);
        assertTrue(helper.updatePantryItem(id, "Eggs", "egg", "3", "count"));
        reopen();
        assertEquals("3", helper.getPantryItem(id).quantity);
        assertEquals(1, helper.getPantryItems().size());
        assertTrue(helper.deletePantryItem(id));
        reopen();
        assertNull(helper.getPantryItem(id));
        assertTrue(helper.getPantryItems().isEmpty());
    }

    @Test
    public void duplicateStockRemainsIndividuallyEditable() {
        long first = helper.addPantryItem("Rice", "rice", "500", "g");
        long second = helper.addPantryItem("Rice", "rice", "0.5", "kg");
        assertNotEquals(first, second);
        helper.deletePantryItem(first);
        assertEquals("0.5", helper.getPantryItem(second).quantity);
    }

    @Test
    public void quotedNamesAreStoredAsData() {
        String name = "Farmer's eggs'; DROP TABLE pantry_items; --";
        long id = helper.addPantryItem(name, "egg", "2", "count");
        assertEquals(name, helper.getPantryItem(id).name);
        assertEquals(1, helper.getPantryItems().size());
    }

    @Test(expected = SQLiteConstraintException.class)
    public void rejectsUnsupportedUnits() {
        helper.addPantryItem("Rice", "rice", "1", "cup");
    }

    @Test
    public void missingRecordsDoNotChangeOtherRows() {
        helper.addPantryItem("Rice", "rice", "500", "g");
        assertNull(helper.getPantryItem(9999));
        assertFalse(helper.updatePantryItem(9999, "Egg", "egg", "1", "count"));
        assertFalse(helper.deletePantryItem(9999));
        assertEquals(1, helper.getPantryItems().size());
    }

    private void reopen() {
        helper.close();
        helper = new DatabaseHelper(context);
    }
}
