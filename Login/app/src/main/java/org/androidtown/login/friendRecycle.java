package org.androidtown.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class friendRecycle extends RecyclerView.Adapter<friendRecycle.itemHolder> {

    ArrayList<friendInformation> list = new ArrayList<>();
    Context context;

    public friendRecycle(ArrayList<friendInformation> list,Context context)
    {
        this.list = list; this.context = context;
    }

    @NonNull
    @Override
    public friendRecycle.itemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.friendinformation,viewGroup,false);

        itemHolder holder = new itemHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull friendRecycle.itemHolder holder, final int i) {

        holder.name.setText(list.get(i).getName());
        holder.phone.setText(list.get(i).getPhone());
        holder.state.setText(list.get(i).getState());
        holder.view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CustomDialog custom = new CustomDialog(context,list.get(i).getPhone());
                custom.callFunction();
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    class itemHolder extends RecyclerView.ViewHolder
    {
        TextView name, phone,state;
        View view;
        public itemHolder(View item)
        {
            super(item);
            name = item.findViewById(R.id.name);
            phone = item.findViewById(R.id.phone); state = item.findViewById(R.id.state);
            view = item;
        }
    }
}
