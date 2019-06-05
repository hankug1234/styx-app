package org.androidtown.login.Location;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.login.InviteItem;
import org.androidtown.login.R;

import java.util.ArrayList;

public class LocationAdapter extends BaseAdapter {
    ArrayList<LocationInfo> itemList;
    private LocationInfo temp;

    public LocationAdapter(ArrayList<LocationInfo> list)
    {
        itemList = list;
    }

    public ArrayList<LocationInfo> getArrayList(){return itemList;}

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override //listview에 아이템의 view를 제공한다
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewGroup p = parent;
        final int pos = position;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.locationlistview, parent, false);
        }

        temp = itemList.get(position);

            TextView name, synctime, state;
            name = convertView.findViewById(R.id.name);
            synctime = convertView.findViewById(R.id.sync);
            state = convertView.findViewById(R.id.state);

            name.setText(itemList.get(position).getName());
            synctime.setText(itemList.get(position).getSynctime());
            state.setText(itemList.get(position).getState());





        return convertView;
    }

}
