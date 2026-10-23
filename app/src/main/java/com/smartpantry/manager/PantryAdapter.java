package com.smartpantry.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends BaseAdapter {
    private final Context context;
    private List<PantryItem> items = new ArrayList<>();

    public PantryAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<PantryItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PantryItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        }
        PantryItem item = getItem(position);
        TextView name = row.findViewById(R.id.item_name);
        TextView quantity = row.findViewById(R.id.item_quantity);
        name.setText(item.name);
        quantity.setText(UnitPreferences.formatQuantity(context, item.quantity, item.unit));
        return row;
    }
}
