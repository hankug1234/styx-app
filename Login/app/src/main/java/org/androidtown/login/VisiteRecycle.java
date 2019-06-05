package org.androidtown.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.androidtown.login.AptItem;
import org.androidtown.login.InviteItem;
import org.androidtown.login.R;

import java.util.ArrayList;

public class VisiteRecycle extends RecyclerView.Adapter<VisiteRecycle.ItemViewHolder> {

    ArrayList<InviteItem> list = new ArrayList<InviteItem>();
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    String userlistid;
    Context context;

    public VisiteRecycle(ArrayList<InviteItem> list,String userlistid,Context context)
    {
        this.list = list;
        this.userlistid = userlistid;
        this.context = context;

    }

    @NonNull
    @Override
    public VisiteRecycle.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.inviteitem,viewGroup,false);
        ItemViewHolder holder = new ItemViewHolder(view,this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        holder.setPosition(i);
        holder.title.setText(list.get(i).getTitle());
        holder.date.setText(list.get(i).getDate());


        holder.view.setOnClickListener(holder);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title,date,num;
        int position;
        VisiteRecycle me;
        View view;
        public ItemViewHolder(View item,VisiteRecycle me)
        {
            super(item);
            title = item.findViewById(R.id.title);
            date = item.findViewById(R.id.date);

            this.me = me;
            view = item;
        }

        public void setPosition(int position)
        {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            letter d = new letter(context,userlistid,position,me,list,list.get(position).getRID());
            d.call();
            /*DB.child("userlist/"+userlistid+"/roomName").push().setValue(list.get(position).getRID());
            DB.child("userlist/"+userlistid+"/inviteroom/"+list.get(position).getInviteid()+"/").setValue(null);
            Toast.makeText(v.getContext(),list.get(position).getInviteid(),Toast.LENGTH_SHORT).show();
            list.remove(position);
            me.notifyDataSetChanged();*/
        }
    }
}
