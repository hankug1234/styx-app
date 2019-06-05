package org.androidtown.login;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class AptActivity extends Fragment {
    Context context;
     ArrayList<AptItem> list = new ArrayList<AptItem>();
    aptRecycle myAdapter = new aptRecycle(list);
    RecyclerView aptListView;
    ImageButton aptMakeB;
    DatabaseReference myDB =  FirebaseDatabase.getInstance().getReference();
    Handler mHandler;
    String UID,phone,CurrentTime,myname;
    ImageView imageView; int src;
    final DatabaseReference room = FirebaseDatabase.getInstance().getReference("room/");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.aptactivity,container,false);
          context = getActivity();
        if(UserInfo.info != null)
        {
            UserInfo.info.finish();
            UserInfo.info = null;
        }
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadDB();

        loadPhone();
        aptListView = v.findViewById(R.id.aptListView);
        aptListView.setAdapter(myAdapter);
        aptListView.setLayoutManager(new LinearLayoutManager(getContext()));
        aptMakeB = v.findViewById(R.id.makeApt);
        aptMakeB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity().getApplicationContext(),MapFragmentActivityForSearch.class);
                intent.putExtra("uid",UID);
                startActivity(intent);

            }
        });




        return v;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onStart() {
        super.onStart();
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        CurrentTime = form.format(date);


    }

    public void checkExite()
    {
        final DatabaseReference Exit = FirebaseDatabase.getInstance().getReference("userlist/");
        Query exit = FirebaseDatabase.getInstance().getReference("userlist/").orderByChild("phone").equalTo(phone);
        exit.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    for(DataSnapshot data: dataSnapshot.getChildren())
                    {
                       final DatabaseReference roomExit =  Exit.child(data.getKey()+"/exit");
                       final String userlistid = data.getKey();
                                roomExit.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    if(dataSnapshot.exists())
                                    {
                                        Exit.child(userlistid+"/point").runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                if(mutableData.getValue()!=null)
                                                {
                                                    int point = mutableData.getValue(int.class) - 10;
                                                    if(point<=0)
                                                    {
                                                        Intent intent = new Intent(context, org.androidtown.login.Alarm.alarm.class);
                                                        context.sendBroadcast(intent);
                                                        point = 0;
                                                    }
                                                    mutableData.setValue(point);
                                                }
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                            }
                                        });
                                        String rid = dataSnapshot.getValue(String.class);
                                        roomExit.child(dataSnapshot.getKey()+"/").setValue(null);
                                        FirebaseDatabase.getInstance().getReference("user/"+UID+"/roomlist/"+rid+"/").setValue(null);

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
                }
                else
                {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void loadDB()//list 초기화 함수 구현 필요
    {
        Query roomList = FirebaseDatabase.getInstance().getReference("user/" + UID + "/roomlist/").orderByKey();
        roomList.addChildEventListener(new ChildEventListener() {
                                           @Override
                                           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                                               if(dataSnapshot.exists()) {
                                                   AptItem item = new AptItem();
                                                   item.setRID(dataSnapshot.getKey());
                                                   item.setTitle(dataSnapshot.child("title").getValue(String.class));
                                                   //item.setNumber(dataSnapshot.child("personnel").getValue(String.class));
                                                   item.setTime(dataSnapshot.child("date").getValue(String.class));

                                                   list.add(item);

                                                   myAdapter.notifyDataSetChanged();
                                               }
                                               else
                                               {
                                                   Toast.makeText(getActivity().getApplicationContext(),"no data",Toast.LENGTH_SHORT).show();
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
                                                       myAdapter.notifyDataSetChanged();
                                                   }
                                                   i++;
                                               }

                                           }

                                           @Override
                                           public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {
                                               Toast.makeText(getActivity().getApplicationContext(), "load cancle error", Toast.LENGTH_SHORT).show();
                                           }

                                       }
        );
    }

    public void loadPhone()
    {
        DatabaseReference s = myDB.child("user/" + UID +"/");
        s.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phone = dataSnapshot.child("phone").getValue(String.class);
                myname = dataSnapshot.child("name").getValue(String.class);
                invited();
                checkExite();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), "load phone error", Toast.LENGTH_SHORT).show();

            }
        });
    }



    public void invited() {
        Query myInvite = myDB.child("userlist/").orderByChild("phone").equalTo(phone);

        myInvite.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() == 1)
                {
                    for(DataSnapshot data: dataSnapshot.getChildren())
                    {
                        final String userlistid = data.getKey();
                        DatabaseReference roomName = myDB.child("userlist/"+userlistid+"/roomName/");
                        roomName.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                if(dataSnapshot.exists()) {
                                    final String RID = dataSnapshot.getValue(String.class);
                                    final String roomNameid = dataSnapshot.getKey();
                                    myDB.child("room/" + RID + "/").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                          if(dataSnapshot.child("exitlist/").hasChild(phone+"/"))
                                          {}
                                          else {
                                              Roommeta meta = new Roommeta();
                                              meta.setDate(dataSnapshot.child("date").getValue(String.class));
                                              //meta.setPersonnel(dataSnapshot.child("personnel").getValue(Integer.class).toString());
                                              meta.setTitle(dataSnapshot.child("title").getValue(String.class));
                                              myDB.child("user/" + UID + "/roomlist/").child(RID).setValue(meta);
                                              myDB.child("userlist/" + userlistid + "/roomName/" + roomNameid + "/").setValue(null);

                                              if (dataSnapshot.child("friendlist/").hasChild(phone + "/")) {
                                              } else {
                                                  room.child(RID + "/personnel").runTransaction(new Transaction.Handler() {
                                                      @NonNull
                                                      @Override
                                                      public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                                                          if (mutableData.getValue() != null) {
                                                              int num = mutableData.getValue(int.class);
                                                              num = num + 1;
                                                              mutableData.setValue(num);
                                                            Log.v("num",num+"");
                                                          } else ;
                                                          {

                                                          }


                                                          return Transaction.success(mutableData);
                                                      }

                                                      @Override
                                                      public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                                      }
                                                  });
                                              }
                                              FirebaseDatabase.getInstance().getReference("room/" + RID + "/friendlist/" + phone + "/").setValue(myname);
                                          }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(getActivity().getApplicationContext(), "no room error", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                }
                                else
                                {
                                    Toast.makeText(getActivity().getApplicationContext(),"no data exist in roomName",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity().getApplicationContext(),"roomName error",Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(),"no data eixst",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(),"userlist error",Toast.LENGTH_SHORT).show();

            }
        });
    }
}

