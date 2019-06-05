package org.androidtown.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
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

import org.androidtown.login.AptActivity;
import org.androidtown.login.AptItem;
import org.androidtown.login.InviteAtivity;
import org.androidtown.login.PastAptAtivity;
import org.androidtown.login.R;
import org.androidtown.login.mFragmentAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class mainui extends AppCompatActivity {
    mFragmentAdapter my;
    TabLayout tab;
    ViewPager page;
    String CurrentTime;
    String[] titles={"친구","초대장","약속목록","지난약속"};
    ArrayList<Fragment> flist = new ArrayList<>();
    int[] imagelist = new int[]{R.mipmap.whale,R.mipmap.whalee,R.mipmap.whaleee,R.mipmap.whaleeee};
    static ImageView imageView;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            phone = telManager.getLine1Number();
            if (phone.startsWith("+82")) {
                phone = phone.replace("+82", "0");
            }
        }

        checkDoneApt();
        setContentView(R.layout.mainui);


        imageView = findViewById(R.id.images);

        loadFrag();

        my = new mFragmentAdapter(getSupportFragmentManager(),flist,titles,imagelist,this);
        page = findViewById(R.id.page);
        tab = findViewById(R.id.tab);

        page.setAdapter(my);

        tab.setupWithViewPager(page);

        page.setOffscreenPageLimit(7);

    }

    public void loadFrag()
    {

        profile f0 = new profile();
        InviteAtivity f1 = new InviteAtivity();
        AptActivity f2 = new AptActivity();
        PastAptAtivity f3 = new PastAptAtivity();

        flist.add(f0);
        flist.add(f1); flist.add(f2); flist.add(f3);

    }


    public void checkDoneApt() {
        final Query userpoint = FirebaseDatabase.getInstance().getReference("userlist").orderByChild("phone").equalTo(phone);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
        }
        else
        {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
            CurrentTime = form.format(date);

            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final DatabaseReference user = FirebaseDatabase.getInstance().getReference("user/"+UID+"/").child("roomlist/");
            final DatabaseReference done = FirebaseDatabase.getInstance().getReference("user/"+UID+"/").child("doneroom/");
            user.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.exists())
                    {

                            String roomDate = dataSnapshot.child("date/").getValue().toString();
                            if(compareDate(CurrentTime,roomDate))
                            {
                                AptItem item = new AptItem();
                                item.setTime(dataSnapshot.child("date/").getValue().toString());
                                //item.setNumber(dataSnapshot.child("personnel/").getValue().toString());
                                item.setTitle(dataSnapshot.child("title").getValue().toString());
                                item.setRID(null);

                                user.child(dataSnapshot.getKey()+"/").setValue(null);
                                done.child(dataSnapshot.getKey()+"/title").setValue(dataSnapshot.child("title/").getValue().toString());
                                done.child(dataSnapshot.getKey()+"/date").setValue(dataSnapshot.child("date/").getValue().toString());
                                done.child(dataSnapshot.getKey()+"/personnel").setValue(dataSnapshot.child("personnel/").getValue().toString());

                            Query location  =  FirebaseDatabase.getInstance().getReference("room/"+dataSnapshot.getKey()+"/locationinfo/").orderByChild("phone").equalTo(phone);
                                  location.addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                          if(dataSnapshot.getChildrenCount() == 1)
                                          {
                                              for(DataSnapshot data: dataSnapshot.getChildren())
                                              {
                                                  if(data.child("state/").getValue(String.class).equals("on"))
                                                  {
                                                      userpoint.addListenerForSingleValueEvent(new ValueEventListener() {
                                                          @Override
                                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                              if(dataSnapshot.hasChildren())
                                                              {
                                                                  for(DataSnapshot data: dataSnapshot.getChildren())
                                                                  {
                                                                      FirebaseDatabase.getInstance().getReference("userlist/").child(data.getKey()+"/point").runTransaction(
                                                                              new Transaction.Handler() {
                                                                                  @NonNull
                                                                                  @Override
                                                                                  public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                                                      if(mutableData.getValue()!=null)
                                                                                      {
                                                                                          int value = mutableData.getValue(int.class)+10;
                                                                                          if(value>=100)
                                                                                          {value = 100;}
                                                                                          mutableData.setValue(value);
                                                                                      }
                                                                                      return Transaction.success(mutableData);
                                                                                  }

                                                                                  @Override
                                                                                  public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                                                                  }
                                                                              }
                                                                      );
                                                                  }
                                                              }
                                                          }

                                                          @Override
                                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                                          }
                                                      });
                                                  }
                                                  else
                                                  {
                                                      userpoint.addListenerForSingleValueEvent(new ValueEventListener() {
                                                          @Override
                                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                              if(dataSnapshot.hasChildren())
                                                              {
                                                                  for(DataSnapshot data: dataSnapshot.getChildren())
                                                                  {
                                                                      FirebaseDatabase.getInstance().getReference("userlist/").child(data.getKey()+"/point").runTransaction(
                                                                              new Transaction.Handler() {
                                                                                  @NonNull
                                                                                  @Override
                                                                                  public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                                                      if(mutableData.getValue()!=null)
                                                                                      {
                                                                                          int value = mutableData.getValue(int.class)-35;
                                                                                          if(value<=0)
                                                                                          {value = 0;
                                                                                              Intent intent = new Intent(getApplicationContext(), org.androidtown.login.Alarm.alarm.class);
                                                                                              sendBroadcast(intent);}
                                                                                          mutableData.setValue(value);
                                                                                      }
                                                                                      return Transaction.success(mutableData);
                                                                                  }

                                                                                  @Override
                                                                                  public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                                                                  }
                                                                              }
                                                                      );
                                                                  }
                                                              }
                                                          }

                                                          @Override
                                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                                          }
                                                      });
                                                  }
                                              }
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
                    else
                    {

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




}
