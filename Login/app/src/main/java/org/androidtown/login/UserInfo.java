package org.androidtown.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserInfo extends AppCompatActivity {
    EditText name;
    Button submit;
    String UID,phone;
    int deny = 0;
    static UserInfo info = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            phone = telManager.getLine1Number();
            if(phone.startsWith("+82")){
                phone = phone.replace("+82", "0");
            }
        } else{
            //Manifest.permission.READ_CALENDAR이 접근 거절 상태 일때
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE)){
                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
            } else{
                deny = 1;
            }

            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            //만약 사용자가 다시 보지 않기에 체크를 했을 경우엔 권한 설정 다이얼로그가 뜨지 않고,
            //곧바로 OnRequestPermissionResult가 실행된다.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},0);

        }


        redundancyCheck();

        info = this;

        name = findViewById(R.id.name);
        submit = findViewById(R.id.submit);
        UID = getIntent().getStringExtra("uid");


        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(name.getText() == null)
                {}
                else {

                    Intent intent = new Intent(getApplicationContext(), mainui.class);
                    Map<String, Object> update = new HashMap<String, Object>();
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("name", name.getText().toString());
                    data.put("phone", phone);
                    update.put(UID + "/", data);
                    intent.putExtra("uid", UID);
                    FirebaseDatabase.getInstance().getReference().child("user/").updateChildren(update);
                    Map<String, Object> userinfo = new HashMap<String, Object>();
                    userinfo.put("name", name.getText().toString());
                    userinfo.put("phone", phone);
                    userinfo.put("point",new Integer(100));
                    FirebaseDatabase.getInstance().getReference().child("userlist/").push().setValue(userinfo);


                    startActivity(intent);
                }
            }
        });
    }

    public void redundancyCheck()
    {
        Query userlist = FirebaseDatabase.getInstance().getReference("userlist/").orderByChild("phone").equalTo(phone);
        userlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0)
                {
                    Toast.makeText(getApplicationContext(),"you have already id please use that one",Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    MainActivity.me.signOut();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            // requestPermission의 두번째 매개변수는 배열이므로 아이템이 여러개 있을 수 있기 때문에 결과를 배열로 받는다.
            // 해당 예시는 요청 퍼미션이 한개 이므로 i=0 만 호출한다.
            if (grantResults[0] == 0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                    TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    phone = telManager.getLine1Number();
                    if(phone.startsWith("+82")){
                        phone = phone.replace("+82", "0");
                    }
                }
            } else {
                if(deny == 1)
                {

                }
                FirebaseAuth.getInstance().signOut();
                MainActivity.me.signOut();


                finish();
            }


        }
    }
}
