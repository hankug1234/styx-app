package org.androidtown.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class PastAptAtivity extends Fragment {
    ArrayList<AptItem> list = new ArrayList<>();
    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String phone;
    pastRecycle past = new pastRecycle(list);
    RecyclerView listView;
    String num;
    int src;
    ImageView imageView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_past_apt_ativity,container,false);
        listView = v.findViewById(R.id.past);
        listView.setAdapter(past);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadDoneRoom();

        Toast.makeText(getActivity(),"past",Toast.LENGTH_SHORT).show();


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public void loadDoneRoom()
    {
        DatabaseReference doneRoom = FirebaseDatabase.getInstance().getReference("user/"+UID+"/doneroom");
        doneRoom.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                  AptItem item = new AptItem();
                if(dataSnapshot.exists()) {
                    item.setRID(dataSnapshot.getKey());
                    item.setTitle(dataSnapshot.child("title/").getValue().toString());
                    if(dataSnapshot.hasChild("personnel/")) {
                        //item.setNumber(dataSnapshot.child("personnel/").getValue().toString());
                        item.setTime(dataSnapshot.child("date/").getValue().toString());

                        list.add(item);

                    }
                    past.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Iterator<AptItem> iter = list.iterator();
                int i = 0;
                while(iter.hasNext())
                {
                    if(iter.next().getRID().equals(dataSnapshot.getKey()))
                    {
                        list.remove(i);
                        past.notifyDataSetChanged();
                    }
                    i++;
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void loadPhone()
    {
        FirebaseDatabase.getInstance().getReference("user/"+UID+"/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {

                    phone = dataSnapshot.child("phone/").getValue().toString();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
