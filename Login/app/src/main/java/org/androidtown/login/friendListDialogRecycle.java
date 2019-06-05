package org.androidtown.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class friendListDialogRecycle extends RecyclerView.Adapter<friendListDialogRecycle.itemHolder> {

    ArrayList<friendInformation> list = new ArrayList<>();
    String RID,myphone;
    friendListDialogRecycle me = this;

    public friendListDialogRecycle(ArrayList<friendInformation> list,String RID,String phone)
    {
        this.list = list;
        this.RID = RID;
        this.myphone = phone;

    }


    @NonNull
    @Override
    public friendListDialogRecycle.itemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater =(LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.friendlistdialogrecycle,viewGroup,false);
        itemHolder item = new itemHolder(view);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull friendListDialogRecycle.itemHolder holder, int position) {
        holder.setPosition(position);
        final int Position = position;
         holder.name.setText(list.get(position).getName());
         holder.phone.setText(list.get(position).getPhone());
         holder.phone.setText(list.get(position).getState());
         holder.view.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 loadRoomMetaAndFriendList(RID,list.get(Position).getPhone(),myphone);
                 list.remove(Position);
                 me.notifyDataSetChanged();
             }
         });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class itemHolder extends  RecyclerView.ViewHolder
    {
        View view; int position;
        TextView name,phone,state;
        public itemHolder(View item)
        {
            super(item);
            view = item;
            name = item.findViewById(R.id.name); phone = item.findViewById(R.id.phone);
            state = item.findViewById(R.id.state);
        }

        public void setPosition(int position)
        {
            this.position = position;
        }


    }

    public void loadRoomMetaAndFriendList(final String RID,final String phone,final String mphone)
    {

        DatabaseReference loadRoomInfo = FirebaseDatabase.getInstance().getReference("room/"+RID);
        loadRoomInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    final  String n="0";
                    final String t = dataSnapshot.child("title/").getValue(String.class);
                    final String d = dataSnapshot.child("date/").getValue(String.class);

                    final DatabaseReference userlist = FirebaseDatabase.getInstance().getReference("userlist/");

                    if(phone.equals(mphone))
                    {return;}

                    Query ulist = userlist.orderByChild("phone").equalTo(phone);
                    final String p = phone;
                    ulist.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                Query change = userlist.orderByChild("phone").equalTo(p);
                                change.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getChildrenCount() == 1)
                                        {
                                            for(DataSnapshot data : dataSnapshot.getChildren()) {
                                                if (data.child("state/").exists()) {
                                                    if(data.child("state").getValue().toString().equals("semi")) {

                                                        Map<String, Object> input = new HashMap<String, Object>();
                                                        input.put("RID", RID);
                                                        input.put("title", t);
                                                        input.put("date", d);
                                                        input.put("num", n);
                                                        FirebaseDatabase.getInstance().getReference("userlist/").child(data.getKey()).child("/inviteroom/").push().updateChildren(input);
                                                    }
                                                    else
                                                    {

                                                    }
                                                }
                                                else
                                                {
                                                    FirebaseDatabase.getInstance().getReference("userlist/").child(data.getKey()).child("/roomName/").push().setValue(RID);
                                                }
                                            }
                                        }
                                        else
                                        {

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {


                                    }
                                });
                            }
                            else
                            {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
