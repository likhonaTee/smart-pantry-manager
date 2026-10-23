package com.smartpantry.manager;

import android.view.View;
import android.widget.ListView;
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
public class PantryActivityTest {
    private ActivityController<PantryActivity> controller;

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
    public void emptyPantryShowsHelpfulMessage() {
        controller = Robolectric.buildActivity(PantryActivity.class).setup();
        PantryActivity activity = controller.get();
        ListView list = activity.findViewById(R.id.pantry_list);
        assertEquals(0, list.getAdapter().getCount());
        assertEquals(View.VISIBLE, activity.findViewById(R.id.pantry_empty).getVisibility());
    }

    @Test
    public void readsSavedRowsAndRefreshesOnResume() {
        try (DatabaseHelper database = new DatabaseHelper(RuntimeEnvironment.getApplication())) {
            database.addPantryItem("Eggs", "egg", "2", "count");
        }
        controller = Robolectric.buildActivity(PantryActivity.class).setup();
        PantryActivity activity = controller.get();
        ListView list = activity.findViewById(R.id.pantry_list);
        assertEquals(1, list.getAdapter().getCount());
        View row = list.getAdapter().getView(0, null, list);
        assertEquals("Eggs", ((TextView) row.findViewById(R.id.item_name)).getText().toString());
        assertEquals("2 count", ((TextView) row.findViewById(R.id.item_quantity)).getText().toString());
        try (DatabaseHelper database = new DatabaseHelper(activity)) {
            database.addPantryItem("Rice", "rice", "500", "g");
        }
        controller.pause().resume();
        assertEquals(2, list.getAdapter().getCount());
        assertEquals(View.GONE, activity.findViewById(R.id.pantry_empty).getVisibility());
    }
}
