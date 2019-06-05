package org.androidtown.login;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class friendListDialog {

    Context context;
    friendListDialogRecycle recycle;
    ArrayList<friendInformation> list = new ArrayList<friendInformation>();
    RecyclerView recycleView;
    String RID,myphone;
    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public friendListDialog(Context context,String RID,String myphone)
    {
        this.context = context;
        this.RID = RID;
        this.myphone = myphone;
    }


    public void call()
    {

        Dialog me = new Dialog(context);
        me.requestWindowFeature(Window.FEATURE_NO_TITLE);
        me.setContentView(R.layout.friendlistdialog);

        recycleView = me.findViewById(R.id.recycle);

        recycle = new friendListDialogRecycle(list,RID,myphone);
        recycleView.setAdapter(recycle);
        recycleView.setLayoutManager(new LinearLayoutManager(context));


        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        WindowManager.LayoutParams  wl = new WindowManager.LayoutParams();
        wl.copyFrom(me.getWindow().getAttributes());
        wl.width = (int)(display.getWidth()*0.5);
        wl.height = (int)(display.getHeight()*0.4);
        Window window = me.getWindow();
        window.setAttributes(wl);






        Query frelist = FirebaseDatabase.getInstance().getReference("user/").child(UID+"/friendlist/").orderByKey();
        final  DatabaseReference state = FirebaseDatabase.getInstance().getReference("userlist/");

        frelist.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final friendInformation f =  new friendInformation(dataSnapshot.getValue().toString(),dataSnapshot.getKey());
                Query ss =  state.orderByChild("phone").equalTo(dataSnapshot.getKey());
                ss.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.exists()) {
                            list.add(f);
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getPhone().equals(dataSnapshot.child("phone/").getValue().toString())) {
                                    if (dataSnapshot.child("state/").exists()) {
                                        list.get(i).setState(dataSnapshot.child("state/").getValue().toString());
                                    } else {
                                        list.get(i).setState("free");
                                    }
                                }

                            }
                            recycle.notifyDataSetChanged();
                        }
                        else
                        {}

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.exists()) {
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getPhone().equals(dataSnapshot.child("phone/").getValue().toString())) {
                                    if (dataSnapshot.child("state/").exists()) {
                                        list.get(i).setState(dataSnapshot.child("state/").getValue().toString());
                                    } else {
                                        list.get(i).setState("free");
                                    }
                                }
                            }
                        }
                        else
                        {}
                        recycle.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        me.show();
    }

}
