package org.androidtown.login;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class participation {

    Context context;
    String RID;
    ArrayList<friendInformation> list = new ArrayList<>();
    RecyclerView recyclerView;
    participationRecycle recycle;
    String onerName,onerPhone,phone;
    int count;

    public participation(Context context,String RID,String phone)
    {
        this.context = context;
        this.RID = RID;
        this.phone = phone;
    }

    public void call()
    {
        final Dialog me = new Dialog(context);
        me.requestWindowFeature(Window.FEATURE_NO_TITLE);
        me.setContentView(R.layout.participation);
        recyclerView = me.findViewById(R.id.recycle);
        recycle = new participationRecycle(list);
        recyclerView.setAdapter(recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        load();
        loadOner();

        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        WindowManager.LayoutParams  wl = new WindowManager.LayoutParams();
        wl.copyFrom(me.getWindow().getAttributes());
        wl.width = (int)(display.getWidth()*0.6);
        wl.height = (int)(display.getHeight()*0.6);
        Window window = me.getWindow();
        window.setAttributes(wl);

        Button exit = me.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!onerPhone.equals(phone))
                { me.dismiss();
                   return;
                }
                final DatabaseReference out = FirebaseDatabase.getInstance().getReference("userlist/");
                final DatabaseReference thisRoom = FirebaseDatabase.getInstance().getReference("room/"+RID+"/personnel");
                final DatabaseReference exiteList = FirebaseDatabase.getInstance().getReference("room/"+RID+"/");
                for(int n = 0;n<list.size();n++)
                {
                    if(list.get(n).getState().equals("y"))
                    {final int nn = n;
                      count+=1;
                      Query Out = out.orderByChild("phone").equalTo(list.get(n).getPhone());
                      Out.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if(dataSnapshot.hasChildren())
                              {
                                  for(DataSnapshot data: dataSnapshot.getChildren())
                                  {
                                      final String userlistid = data.getKey();

                                      out.child(userlistid+"/").child("exit/").push().setValue(RID);
                                      exiteList.child("friendlist/").child(list.get(nn).getPhone()+"/").setValue(null);
                                      exiteList.child("exitlist/").child(list.get(nn).getPhone()+"/").setValue(list.get(nn).getName());

                                  }
                              }
                              else
                              {}
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
                thisRoom.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                        if(mutableData !=null)
                        {
                            int personnel = mutableData.getValue(int.class);
                            personnel = personnel -count;
                            mutableData.setValue(personnel);
                        }
                        else
                        {}

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                           if(dataSnapshot.exists())
                           {
                               if(dataSnapshot.getValue(int.class)<=0)
                               {
                                   FirebaseDatabase.getInstance().getReference("room/"+RID+"/").setValue(null);
                               }
                           }
                    }
                });
                me.dismiss();
            }
        });
        me.show();
    }

    public void load()
    {
        DatabaseReference ptc = FirebaseDatabase.getInstance().getReference("room/"+RID+"/friendlist");
        ptc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    for(DataSnapshot data : dataSnapshot.getChildren())
                    {

                        list.add(new friendInformation(data.getValue(String.class),data.getKey()));
                    }
                }
                else
                {

                }
                recycle.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadOner()
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
                        onerPhone = data.getKey();
                    }
                }
                else
                {}
                Log.v("onerName",onerName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
