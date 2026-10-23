package com.smartpantry.manager;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        RadioGroup units = findViewById(R.id.units_group);
        units.check(UnitPreferences.usesLargeUnits(this) ? R.id.units_large : R.id.units_small);
        units.setOnCheckedChangeListener((group, checkedId) ->
                UnitPreferences.setLargeUnits(this, checkedId == R.id.units_large));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
