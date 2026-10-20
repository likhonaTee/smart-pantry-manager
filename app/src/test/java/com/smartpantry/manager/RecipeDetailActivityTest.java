package com.smartpantry.manager;

import android.content.Intent;
import android.widget.TextView;

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
public class RecipeDetailActivityTest {
    private ActivityController<RecipeDetailActivity> controller;

    @Before
    public void setUp() {
        RuntimeEnvironment.getApplication().deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
        RuntimeEnvironment.getApplication().deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    @Test
    public void displaysEveryIngredientAndTheStoredMethod() {
        open(2);
        assertEquals("Scrambled eggs", text(R.id.recipe_name));
        assertEquals("egg: 2 count\nbutter: 10 g", text(R.id.recipe_ingredients));
        assertTrue(text(R.id.recipe_method).contains("Beat the eggs"));
        assertTrue(text(R.id.recipe_method).contains("stir until fully set"));
    }

    @Test
    public void missingRecipeReturnsSafely() {
        open(99999);
        assertTrue(controller.get().isFinishing());
    }

    private void open(long id) {
        Intent intent = new Intent(RuntimeEnvironment.getApplication(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, id);
        controller = Robolectric.buildActivity(RecipeDetailActivity.class, intent).setup();
    }

    private String text(int id) {
        return ((TextView) controller.get().findViewById(id)).getText().toString();
    }
}
