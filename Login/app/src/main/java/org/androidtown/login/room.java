package org.androidtown.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class room extends AppCompatActivity {
    FirebaseDatabase mdb = FirebaseDatabase.getInstance();
    DatabaseReference mr = mdb.getReference("room/");
    DatabaseReference friendlist,userlist;
    DatabaseReference chet;
    TextView title,num,date,content,place;
    ListView messagesView;
    MessageAdapter messageAdapter;
    EditText keyboard,friend;
    ImageButton submit,fsubmit,location,exit;
    String RID,UID,phone,userColor, promiseTime, promiseDate, name;
    String t,d,n;
    Context context;
    Map<String, String> friendName =  new HashMap<>();
    double longitude = 0.0 ;
    double latitude = 0.0;
    String onerName,onerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);
        context = this;

        friend = findViewById(R.id.friend);
        fsubmit = findViewById(R.id.fsubmit);
        exit = findViewById(R.id.exit);
        location = findViewById(R.id.location);
        title = findViewById(R.id.title);
        num = findViewById(R.id.num);
        place = findViewById(R.id.place);
        date = findViewById(R.id.date);
        keyboard = findViewById(R.id.keyboard);
        submit = findViewById(R.id.submit);
        RID = getIntent().getStringExtra("rid");
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chet = FirebaseDatabase.getInstance().getReference("room/"+RID+"/").child("chet");
        friendlist = mdb.getReference("room/"+RID+"/").child("friendlist");
        userlist = mdb.getReference("userlist/");
        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.edit);
        messagesView.setAdapter(messageAdapter);

        userColor = getRandomColor();
        t = getIntent().getStringExtra("title"); d = getIntent().getStringExtra("date"); n = getIntent().getStringExtra("num");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            phone = telManager.getLine1Number();
            if (phone.startsWith("+82")) {
                phone = phone.replace("+82", "0");
            }
        }

        loadFriendName();
        loadOner();
        loadPhone();
        Query room = mr.orderByKey().equalTo(RID);
        room.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot data: dataSnapshot.getChildren())
                    {

                        title.setText(data.child("title/").getValue(String.class));
                        date.setText(data.child("date/").getValue(String.class));
                        try{ num.setText(data.child("personnel/").getValue().toString());}
                        catch (NullPointerException e){}
                        place.setText(data.child("promisePlace/").getValue(String.class));
                        longitude = data.child("promiseLongitude/").getValue(double.class);
                        latitude = data.child("promiseLatitude/").getValue(double.class);
                        promiseTime = data.child("time/").getValue(String.class);
                        promiseDate = data.child("date/").getValue(String.class);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"no room eixst error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"room call error",Toast.LENGTH_SHORT).show();

            }
        });


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapFragmentActivity.class);
                intent.putExtra("rid", RID);
                intent.putExtra("phone", phone);
                intent.putExtra("name", name);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("promiseTime", promiseTime);
                intent.putExtra("promiseDate", promiseDate);
                startActivity(intent);
            }
        });


        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Chet c = new Chet();
                c.setUser(phone);
                String input = keyboard.getText().toString();
                keyboard.setText(null);
                c.setContent(input);
                chet.push().setValue(c);

            }
        });
        fsubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(onerPhone.equals(phone)) {
                    friendListDialog friend = new friendListDialog(context, RID, phone);
                    friend.call();
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onerPhone.equals(phone)) {
                    participation ptc = new participation(context, RID, phone);
                    ptc.call();
                }
            }});


    }

    public void exitRoom()
    {
        DatabaseReference exitState = FirebaseDatabase.getInstance().getReference("room/"+RID+"/exitlist");
        exitState.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                  if(!dataSnapshot.exists())
                  {
                      return;
                  }
                  if(dataSnapshot.getKey().equals(phone))
                  {
                      finish();
                  }
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

    public void loadChet()
    {

        chet.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.child("user/").getValue(String.class).equals(phone))
                { textview(dataSnapshot.child("content/").getValue().toString(),true,friendName.get(dataSnapshot.child("user/").getValue().toString())); }
                else
                { textview(dataSnapshot.child("content/").getValue().toString(),false,friendName.get(dataSnapshot.child("user/").getValue().toString())); }

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
                Toast.makeText(getApplicationContext(),"load error",Toast.LENGTH_SHORT).show();

            }
        });

    }


    public void textview(String a,Boolean b,String name)
    {
        MemberData data = new MemberData(name,userColor);
        Message message = new Message(a,data,b);
        messageAdapter.add(message);
        messagesView.setSelection(messagesView.getCount() - 1);

    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }




    public void loadPhone()
    {
        FirebaseDatabase.getInstance().getReference("user/"+UID+"/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    name = dataSnapshot.child("name/").getValue().toString();
                    phone = dataSnapshot.child("phone/").getValue().toString();
                }

                exitRoom();
                loadChet();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"load error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadFriendName(){

        FirebaseDatabase.getInstance().getReference("room/"+RID+"/friendlist/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    friendName.put(data.getKey(),data.getValue(String.class));

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"load error",Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

