package com.abhishek.crud_app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ItemsList_Adapter extends ArrayAdapter<Items> {

    private Activity context;
    private List<Items> itemlist;

    // Constructor
    public ItemsList_Adapter(Activity context, List<Items> itemlist) {
        super(context, R.layout.list_layout , itemlist);
        this.context = context;
        this.itemlist = itemlist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItems = inflater.inflate(R.layout.list_layout , null , true);

        TextView tvTitle = listViewItems.findViewById(R.id.tvTitle);
        TextView tvDescription = listViewItems.findViewById(R.id.tvDescription);

        Items items = itemlist.get(position);
        tvTitle.setText(items.getTitle());
        tvDescription.setText(items.getDescription());

        return listViewItems;
    }
}

