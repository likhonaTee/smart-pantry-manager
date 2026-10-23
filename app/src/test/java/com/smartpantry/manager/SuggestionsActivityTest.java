package com.smartpantry.manager;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

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

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SuggestionsActivityTest {
    private ActivityController<SuggestionsActivity> controller;

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
    public void emptyPantryShowsNoMatchMessage() {
        controller = Robolectric.buildActivity(SuggestionsActivity.class).setup();
        ListView list = controller.get().findViewById(R.id.suggestions_list);
        assertEquals(0, list.getAdapter().getCount());
        assertEquals(View.VISIBLE, controller.get().findViewById(R.id.suggestions_empty).getVisibility());
    }

    @Test
    public void recipeAppearsAndDisappearsAfterStockChanges() {
        long butter;
        try (DatabaseHelper database = new DatabaseHelper(RuntimeEnvironment.getApplication())) {
            database.addPantryItem("Eggs", "egg", "2", "count");
            butter = database.addPantryItem("Butter", "butter", "0.01", "kg");
        }
        controller = Robolectric.buildActivity(SuggestionsActivity.class).setup();
        ListView list = controller.get().findViewById(R.id.suggestions_list);
        assertEquals(1, list.getAdapter().getCount());
        assertEquals("Scrambled eggs", list.getAdapter().getItem(0));
        try (DatabaseHelper database = new DatabaseHelper(controller.get())) {
            database.deletePantryItem(butter);
        }
        controller.pause().resume();
        assertEquals(0, list.getAdapter().getCount());
        assertEquals(View.VISIBLE, controller.get().findViewById(R.id.suggestions_empty).getVisibility());
    }

    @Test
    public void rowSelectionPassesTheRecipeIdToDetails() {
        try (DatabaseHelper database = new DatabaseHelper(RuntimeEnvironment.getApplication())) {
            database.addPantryItem("Eggs", "egg", "2", "count");
            database.addPantryItem("Butter", "butter", "10", "g");
        }
        controller = Robolectric.buildActivity(SuggestionsActivity.class).setup();
        ListView list = controller.get().findViewById(R.id.suggestions_list);
        list.performItemClick(null, 0, 0);
        Intent intent = Shadows.shadowOf(controller.get()).getNextStartedActivity();
        assertEquals(RecipeDetailActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(2, intent.getLongExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, -1));
    }
}
