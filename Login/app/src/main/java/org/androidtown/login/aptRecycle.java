package org.androidtown.login;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class aptRecycle extends RecyclerView.Adapter<aptRecycle.ItemViewHolder> {

    public ArrayList<AptItem> list = new ArrayList<AptItem>();


    public aptRecycle(ArrayList<AptItem> list)
    {
        this.list = list;

    }


    @NonNull
    @Override // 새로운 홀더 생성
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.aptitem,viewGroup,false);

        ItemViewHolder holder = new ItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setPosition(position);
        holder.date.setText(list.get(position).getTimeT());
        holder.title.setText(list.get(position).getTitleT());
        holder.view.setOnClickListener(holder);

    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView date, title,num;
        Button modify, attandance;
        int position; View view;

        public ItemViewHolder(View item)
        {
            super(item);
            date = item.findViewById(R.id.apttext1);
            title = item.findViewById(R.id.apttext2);

            view = item;

        }

        public void setPosition(int i)
        {position = i;}

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), room.class);
            intent.putExtra("rid",list.get(position).getRID());
            intent.putExtra("title",list.get(position).getTitleT());
            intent.putExtra("date",list.get(position).getTimeT());
            intent.putExtra("num",list.get(position).getNum());
            v.getContext().startActivity(intent);

        }
    }

}
