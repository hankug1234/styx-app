package org.androidtown.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;

public class pastRecycle extends RecyclerView.Adapter<pastRecycle.ItemViewHolder> {

    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String numb;
    ArrayList<AptItem> list = new ArrayList<>();

    public pastRecycle(ArrayList<AptItem> list)
    {
        this.list = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pastlistui,viewGroup,false);
        ItemViewHolder holder = new ItemViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull pastRecycle.ItemViewHolder viewHolder,final int i) {

        viewHolder.date.setText(list.get(i).getTimeT());
        viewHolder.title.setText(list.get(i).getTitleT());
        viewHolder.setPosition(i);
        viewHolder.view.setOnClickListener(viewHolder);
        viewHolder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String RID = list.get(i).getRID();

                final DatabaseReference room = FirebaseDatabase.getInstance().getReference("room/"+RID+"/");
                final DatabaseReference doneroom = FirebaseDatabase.getInstance().getReference("user/"+UID+"/doneroom/"+RID+"/");

                room.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        if(mutableData.hasChild("personnel"))
                        {     numb = mutableData.child("personnel/").getValue().toString();


                            Integer n = Integer.parseInt(numb) - 1;

                            if ((int) n <= 0) {
                                room.setValue(null);
                                doneroom.setValue(null);
                                mutableData.child("personnel/").setValue(n);
                            } else {
                                doneroom.setValue(null);
                                mutableData.child("personnel/").setValue(n);
                            }
                        }
                        else{
                            Log.v("aa","eeee");
                        };


                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });

            }
            }
        );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title,date,num;
        ImageButton delete;
        int position;
        View view;
        public ItemViewHolder(View item)
        {
            super(item);
            title = item.findViewById(R.id.title);
            date = item.findViewById(R.id.date);
            view = item;
            delete = item.findViewById(R.id.delete);
        }

        public void setPosition(int position)
        {
            this.position =  position;
        }

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




