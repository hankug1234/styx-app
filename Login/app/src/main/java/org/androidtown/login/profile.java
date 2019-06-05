package org.androidtown.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class profile extends Fragment {
    DatabaseReference userlist = FirebaseDatabase.getInstance().getReference("userlist/");
    DatabaseReference user = FirebaseDatabase.getInstance().getReference("user/");
    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayList<friendInformation> friendList = new ArrayList<>();
    friendRecycle recycle;

    String myphone;

    RecyclerView flist;
    ImageButton submit;
    EditText edit;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPhone();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile,container,false);
        flist = view.findViewById(R.id.friendlist);
        edit = view.findViewById(R.id.friend);
        submit = view.findViewById(R.id.fsubmit);


        loadFriendList();
        recycle = new friendRecycle(friendList,getContext());

        flist.setAdapter(recycle);
        flist.setLayoutManager(new LinearLayoutManager(getContext()));


        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String p = edit.getText().toString();
                edit.setText(null);

                addFriend(p);

            }
        });

        return view;
    }


    private void loadFriendList()
    {
        Query frelist = user.child(UID+"/friendlist/").orderByKey();
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
                               friendList.add(f);
                               for (int i = 0; i < friendList.size(); i++) {
                                   if (friendList.get(i).getPhone().equals(dataSnapshot.child("phone/").getValue().toString())) {
                                       if (dataSnapshot.child("state/").exists()) {
                                           friendList.get(i).setState(dataSnapshot.child("state/").getValue().toString());
                                       } else {
                                           friendList.get(i).setState("free");
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
                               for (int i = 0; i < friendList.size(); i++) {
                                   if (friendList.get(i).getPhone().equals(dataSnapshot.child("phone/").getValue().toString())) {
                                       if (dataSnapshot.child("state/").exists()) {
                                           friendList.get(i).setState(dataSnapshot.child("state/").getValue().toString());
                                       } else {
                                           friendList.get(i).setState("free");
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


    }



   private void addFriend(String phone)
   {
       if(phone.equals(myphone))
       {
           return;
       }


       Query friend = userlist.orderByChild("phone").equalTo(phone);
       friend.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
               { DatabaseReference friendList = user.child(UID+"/").child("friendlist/");
                   if(dataSnapshot.getChildrenCount() == 1)
                   {
                       for(DataSnapshot data: dataSnapshot.getChildren() )
                       {
                           friendList.child(data.child("phone").getValue().toString()+"/").setValue(data.child("name/").getValue().toString());
                       }
                   }
                   else
                   {
                       Toast.makeText(getContext(),"too many child error",Toast.LENGTH_SHORT).show();
                   }
               }
               else
               {
                   Toast.makeText(getContext(),"no search user",Toast.LENGTH_SHORT).show();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
      recycle.notifyDataSetChanged();
   }


   public void loadPhone()
   {
       DatabaseReference me = FirebaseDatabase.getInstance().getReference("user/"+UID+"/");
       me.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               try{
             myphone =   dataSnapshot.child("phone").getValue().toString();}
               catch (NullPointerException e){}
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
   }



}
