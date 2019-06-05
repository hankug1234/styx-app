package org.androidtown.login;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class letter {
    Context context;
    TextView accept,deny,oners;
    String userlistid,onerName;
    int position;
    VisiteRecycle me;
    ArrayList<InviteItem> list = new ArrayList<InviteItem>();
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    String myphone,myname,RID;
    public letter (Context context,String userlist,int position,VisiteRecycle me,ArrayList<InviteItem> list,String RID)
    {
        this.context = context;
        this.userlistid = userlist;
        this.position = position;
        this.me = me;
        this.list = list;
        this.RID = RID;
        loadPhone();

    }

    public void call()
    {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.letter);

        accept = dialog.findViewById(R.id.accept);
        deny = dialog.findViewById(R.id.deny);
        oners = dialog.findViewById(R.id.oner);

        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        WindowManager.LayoutParams  wl = new WindowManager.LayoutParams();
        wl.copyFrom(dialog.getWindow().getAttributes());

        wl.width = (int)(display.getWidth()*0.7);
        wl.height = (int)(display.getHeight()*0.3);

        Window window = dialog.getWindow();
        window.setAttributes(wl);

        loadOnerName();


        accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
                String CurrentTime = form.format(date);

                if(compareDate(CurrentTime,list.get(position).getDate()))
                {
                    DB.child("userlist/"+userlistid+"/inviteroom/"+list.get(position).getInviteid()+"/").setValue(null);
                    list.remove(position);
                    me.notifyDataSetChanged();
                    dialog.dismiss();
                    return;
                }

                DB.child("userlist/"+userlistid+"/roomName").push().setValue(list.get(position).getRID());
                DB.child("userlist/"+userlistid+"/inviteroom/"+list.get(position).getInviteid()+"/").setValue(null);
                list.remove(position);
                me.notifyDataSetChanged();

                dialog.dismiss();
            }
        });

        deny.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DB.child("userlist/"+userlistid+"/inviteroom/"+list.get(position).getInviteid()+"/").setValue(null);
                list.remove(position);
                me.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

     dialog.show();


    }

    public boolean compareDate(String a, String b)
    {
        String[] al = a.split("-");
        String[] bl = b.split("-");
        if(Integer.parseInt(al[0])>Integer.parseInt(bl[0]))
        {
            return true;
        }
        else
        {
            if(Integer.parseInt(al[0])==Integer.parseInt(bl[0]))
            {
                if(Integer.parseInt(al[1])>Integer.parseInt(bl[1]))
                {
                    return true;
                }
                else
                {
                    if(Integer.parseInt(al[1])==Integer.parseInt(bl[1]))
                    {
                        if(Integer.parseInt(al[2])>Integer.parseInt(bl[2]))
                        {
                            return true;
                        }
                        else
                        {
                            if(Integer.parseInt(al[2])==Integer.parseInt(bl[2]))
                            {
                                return false;
                            }
                        }
                    }
                    else
                    {

                    }
                    return false;
                }



            }
            return false;

        }
    }


    public void loadPhone()
    {   String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference me = FirebaseDatabase.getInstance().getReference("user/"+UID+"/");
        me.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myphone =   dataSnapshot.child("phone").getValue().toString();
                myname = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
  public void loadOnerName()
  {
      DatabaseReference oner = FirebaseDatabase.getInstance().getReference("room/"+RID+"/oner");
      oner.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.hasChildren())
              {
                  for(DataSnapshot data: dataSnapshot.getChildren())
                  {
                      onerName = data.getValue(String.class);
                  }
                  oners.setText(onerName);
              }
              else
              {}
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
  }

}
