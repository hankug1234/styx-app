package org.androidtown.login.Location;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class LocationTool {

    DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    public ArrayList<LocationInfo> list = new ArrayList<>();
    public HashMap<String,LocationInfo> info = new HashMap<>();

    private String RID;
    private String phone;

    public LocationTool(String RID,String phone)
    {
        this.RID = RID;
        this.phone = phone;
    }

    public void upLoadLocationInfo(final LocationInfo item)
    {
        DatabaseReference myRoom = DB.child("room/"+RID+"/locationinfo/");
        myRoom.child(phone+"/").setValue(item);
    }

    public void downLoadLocationInfo()
    {
        DatabaseReference myRoom = DB.child("room/"+RID+"/locationinfo/");
        myRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    for(DataSnapshot data: dataSnapshot.getChildren())
                    {
                        info.put(data.getKey(),data.getValue(LocationInfo.class));
                        list.add(data.getValue(LocationInfo.class));
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



}
