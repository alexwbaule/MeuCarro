package com.alexwbaule.flexprofile.adapter;

/**
 * Created by alex on 26/06/15.
 */

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;


public class AutoCompleteOilNameAdapter extends CursorAdapter {
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    Context context;

    public AutoCompleteOilNameAdapter(Context context) {
        super(context, null, 0);
        this.context = context;
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        Cursor currentCursor = null;

        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }

        String args = "";

        if (constraint != null) {
            args = constraint.toString();
        }
        currentCursor = rootapp.getOilName(args);
        return currentCursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final TextView view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        String item = createItem(cursor);
        view.setText(item);
        return view;
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        String itemNumber = cursor.getString(1);
        return itemNumber;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String item = createItem(cursor);
        ((TextView) view).setText(item);
    }

    private String createItem(Cursor cursor) {
        String item = cursor.getString(1);
        return item;
    }

}
