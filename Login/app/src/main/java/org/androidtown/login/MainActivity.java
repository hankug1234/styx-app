package org.androidtown.login;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    MediaPlayer ring;
    FirebaseAuth mAuth;
    GoogleSignInOptions myGoogle;
    static GoogleSignInClient me;
    String CurrentTime;
     static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ring = MediaPlayer.create(this, R.raw.start);
        ring.start();
    if(count == 0) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        count = 1;
    }
        mAuth = FirebaseAuth.getInstance();
        myGoogle = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        me = GoogleSignIn.getClient(this,myGoogle);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!= null)
                {
                    Toast.makeText(getApplicationContext(),"login state",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), mainui.class);
                    intent.putExtra("uid",mAuth.getCurrentUser().getUid());
                    startActivity(intent);
                }
                else {
                    Intent send = me.getSignInIntent();
                    startActivityForResult(send, 1);
                }
            }
        });
        findViewById(R.id.appointlist).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() == null)
                {
                    Toast.makeText(getApplicationContext(),"logout state",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.signOut();
                    Toast.makeText(getApplicationContext(),"logout",Toast.LENGTH_SHORT).show();
                }
                me.signOut();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        ring.stop();
        ring.release();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final Task<AuthResult> mytask = task;
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"login success",Toast.LENGTH_SHORT).show();
                            Query check = FirebaseDatabase.getInstance().getReference("user/").orderByKey().equalTo(task.getResult().getUser().getUid());
                            check.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {


                                        Intent intent = new Intent(getApplicationContext(),mainui.class);
                                        intent.putExtra("uid",mytask.getResult().getUser().getUid());
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(getApplicationContext(),UserInfo.class);
                                        intent.putExtra("uid",mytask.getResult().getUser().getUid());

                                        startActivity(intent);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getApplicationContext(), room.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        else
                        {Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_LONG).show();}
                    }
                });
            }
            catch(ApiException e)
            {
                Toast.makeText(getApplicationContext(),"catch fail",Toast.LENGTH_LONG).show();

            }
        }

    }
}
