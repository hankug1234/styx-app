package org.androidtown.login;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;

public class participationRecycle extends RecyclerView.Adapter<participationRecycle.itemHolder> {

    ArrayList<friendInformation> list = new ArrayList<>();
    ArrayList<Integer> check = new ArrayList<Integer>();

    public participationRecycle(ArrayList<friendInformation> list)
    {
        this.list = list;
    }

    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.participationitem,viewGroup,false);
        itemHolder item = new itemHolder(view);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull itemHolder itemHolder, int i) {
        final int position = i;
        itemHolder.name.setText(list.get(i).getName());
        itemHolder.phone.setText(list.get(i).getPhone());
        list.get(i).setState("n");
        itemHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout main = v.findViewById(R.id.main);
                if(list.get(position).getState().equals("n"))
                {
                    list.get(position).setState("y");
                    main.setBackgroundColor(Color.RED);
                }
                else
                {
                    list.get(position).setState("n");
                    main.setBackgroundColor(Color.WHITE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class itemHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView name,phone;
        public itemHolder(View item)
        {
            super(item);
            name = item.findViewById(R.id.name);
            phone = item.findViewById(R.id.phone);
            view = item;
        }


    }
}
