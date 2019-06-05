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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class InviteAtivity extends Fragment {
    RecyclerView invite;
    ArrayList<InviteItem> list = new ArrayList<InviteItem>();
    VisiteRecycle addapter;
    String UID,phone,userlistid;
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    RadioGroup state;
    RadioButton free,semi,none;
    ImageView imageView; int src;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_invite_ativity,container,false);
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        state = v.findViewById(R.id.state);
        free = v.findViewById(R.id.free); semi = v.findViewById(R.id.semi); none = v.findViewById(R.id.none);

        loadDatabase(DB);



        invite = v.findViewById(R.id.invite);




        state.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                DatabaseReference checkState = DB.child("userlist/"+userlistid+"/state/");
                if(free.getId() == checkedId)
                {
                    checkState.setValue(null);
                }
                else if(semi.getId() == checkedId)
                {
                    checkState.setValue("semi");
                }
                else if(none.getId() == checkedId)
                {
                    checkState.setValue("none");
                }
                else
                {}

            }
        });


        return v;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void loadInviteRoom(DatabaseReference myDB)
    {
       final DatabaseReference db = myDB;
        Query userListId = myDB.child("userlist/").orderByChild("phone").equalTo(phone);
        userListId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot data : dataSnapshot.getChildren())
                    {
                        userlistid = data.getKey();

                        DatabaseReference userlist = db.child("userlist/"+userlistid+"/inviteroom");
                        userlist.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                InviteItem node = new InviteItem();
                                node.setDate(dataSnapshot.child("date").getValue().toString());
                                node.setTitle(dataSnapshot.child("title").getValue().toString());
                                //node.setNum(dataSnapshot.child("num").getValue().toString());
                                node.setRID(dataSnapshot.child("RID").getValue().toString());
                                node.setInviteid(dataSnapshot.getKey());
                                node.setInviteid(dataSnapshot.getKey());

                                list.add(node);

                                addapter.notifyDataSetChanged();
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
                                Toast.makeText(getActivity().getApplicationContext(),"invite error",Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(),"no invite list exist",Toast.LENGTH_SHORT).show();
                }
                addapter = new VisiteRecycle(list,userlistid,getActivity());
                invite.setAdapter(addapter);
                invite.setLayoutManager(new LinearLayoutManager(getContext()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void loadDatabase(final DatabaseReference myDB)
    {
        DatabaseReference s = myDB.child("user/" + UID + "/phone/");
        s.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phone = dataSnapshot.getValue(String.class);
                loadInviteRoom(myDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), "load phone error", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
