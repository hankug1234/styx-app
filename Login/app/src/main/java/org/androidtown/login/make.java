package org.androidtown.login;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class make extends AppCompatActivity {

    final int DIALOG_DATE = 1;
    final int DIALOG_TIME_PROMISE = 2;
    final int DIALOG_TIME_SPEND = 3;

    String placeName = "장소선택";
    double promiseLongitude;
    double promiseLatitude;

    TextView promiseTitle, titleText, placeText, dateText, timeText, spendingTimeText;
    ImageView title,dateImage,timeImage,startTime;
    ImageButton fin;
    Button[] button = new Button[3];
    Group group = new Group();
    String UID, myphone, myname;

    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make);

        cal = Calendar.getInstance();

        UID = getIntent().getStringExtra("uid");

        promiseTitle = findViewById(R.id.promiseTitle);
        String editPromiseTitle = "제목"; // 최초 약속 생성시 가져오는 약속제목 - 이후 수정되면 수정된 것으로
        promiseTitle.setText(editPromiseTitle);

        titleText = findViewById(R.id.titleText);
        titleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder editTitleDialog = new AlertDialog.Builder(org.androidtown.login.make.this);

                editTitleDialog.setTitle("제목수정");       // 제목 설정
                //editTitleDialog.setMessage("Message");   // 내용 설정

                final EditText editTitle = new EditText(org.androidtown.login.make.this);
                editTitleDialog.setView(editTitle);

                // 확인 버튼 설정
                editTitleDialog.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Text 값 받아서 TextView 수정하기
                        titleText.setText(editTitle.getText().toString());
                        promiseTitle.setText(editTitle.getText().toString());
                        group.setTitle(editTitle.getText().toString());
                        title = (ImageView)findViewById(R.id.title);
                        title.setImageResource(R.drawable.new_color);
                        Toast.makeText(getApplicationContext(),"제목이 설정되었습니다.", Toast.LENGTH_LONG).show();
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                // 취소 버튼 설정
                editTitleDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });

                // 창 띄우기
                editTitleDialog.show();
            }
        });
        //group.setTitle(titleText.getText().toString());

        //String from = personnelText.getText().toString();
        //int to = Integer.parseInt(from);
        //group.setPersonnel(to);



        placeText = findViewById(R.id.placeText);
        placeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapFragmentActivityForSearch.class);
                startActivity(intent);
            }
        });

        dateText = findViewById(R.id.dateText);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });
        //group.setDate(dateText.getText().toString());


        timeText = findViewById(R.id.timeText);
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME_PROMISE);
            }
        });
        //group.setTime(timeText.getText().toString());




        spendingTimeText = findViewById(R.id.spendingTimeText);
        spendingTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME_SPEND);
                startTime= (ImageView)findViewById(R.id.startTime);
                startTime.setImageResource(R.drawable.run_color);
            }
        });


        Intent passedIntent = getIntent();
        if(passedIntent.getStringExtra("placeName") != null) {
            placeName = passedIntent.getStringExtra("placeName");
            promiseLongitude = passedIntent.getDoubleExtra("promiseLongitude",0);
            promiseLatitude = passedIntent.getDoubleExtra("promiseLatitude",0);

            placeText.setText(placeName);
            group.setPromisePlace(placeName);
            group.setPromiseLongitude(promiseLongitude);
            group.setPromiseLatitude(promiseLatitude);
            Toast.makeText(getApplicationContext(), "장소가 설정되었습니다.", Toast.LENGTH_LONG).show();
        }


        // 수정 완료 후 약속리스트로 돌아가는 동작
        fin = findViewById(R.id.finish);
        fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(group.isChanged()) {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String myDate = sdf.format(date);
                SimpleDateFormat sdff = new SimpleDateFormat("hh:mm:a");
                String myTime = sdff.format(date);


                if(myTime.split(":")[2].equals("PM")){
                    myTime = (Integer.parseInt(myTime.split(":")[0])+12)+":"+ myTime.split(":")[1];

                }

                String proDate = dateText.getText().toString();
                String proTime = timeText.getText().toString();


                            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            DatabaseReference r = FirebaseDatabase.getInstance().getReference();
                            String roomName = r.child("room/").push().getKey();
                            Map<String, Object> data = new HashMap<String, Object>();
                            Roommeta meta = new Roommeta();
                            meta.setDate(group.getDate());
                            //meta.setPersonnel(Integer.toString(group.getPersonnel()));
                            meta.setTitle(group.getTitle());

                            data.put("room/" + roomName + "/", group);
                            data.put("user/" + UID + "/roomlist/" + roomName + "/", meta);
                            r.updateChildren(data);

                            loadPhone(roomName);
                            finish();
                            // room으로 전환
                            Intent intent = new Intent(getApplicationContext(), mainui.class);
                            startActivity(intent);


                }
                else{Toast.makeText(getApplicationContext(), "설정되지 않은 값이 존재합니다", Toast.LENGTH_LONG).show();}
            }
        });

    }





    // TimePicker & DatePicker with dialog
    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DATE:
                DatePickerDialog dpd = new DatePickerDialog
                        (org.androidtown.login.make.this, // 현재화면의 제어권자
                                new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        String editDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                        dateText.setText(editDate);
                                        group.setDate(editDate);
                                        Toast.makeText(getApplicationContext(), "날짜가 설정되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                                //    호출할 리스너 등록
                                2019, 4, 30); // 기본값 연월일

                dateImage = (ImageView) findViewById(R.id.dateImage);
                dateImage.setImageResource(R.drawable.calendar_color);
                return dpd;

            case DIALOG_TIME_PROMISE:
                TimePickerDialog tpd1 =
                        new TimePickerDialog(org.androidtown.login.make.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String editTime;
                                        if (minute / 10 < 1)
                                            editTime = hourOfDay + ":" + "0" + minute;
                                        else if (hourOfDay / 10 < 1)
                                            editTime = "0" + hourOfDay + ":" + minute;
                                        else if (hourOfDay / 10 < 1 && minute / 10 < 1)
                                            editTime = "0" + hourOfDay + ":" + "0" + minute;
                                        else editTime = hourOfDay + ":" + minute;
                                        timeText.setText(editTime);
                                        group.setTime(editTime);

                                        Toast.makeText(getApplicationContext(), "시간이 설정되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }, // 값설정시 호출될 리스너 등록
                                12, 00, true); // 기본값 시분 등록
                // true : 24 시간(0~23) 표시
                // false : 오전/오후 항목이 생김
                timeImage = (ImageView) findViewById(R.id.timeImage);
                timeImage.setImageResource(R.drawable.clock_color);
                return tpd1;
            case DIALOG_TIME_SPEND:
                TimePickerDialog tpd2 =
                        new TimePickerDialog(org.androidtown.login.make.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourSpend, int minuteSpend) {
                                        //소요시간 설정
                                        String promiseTime = timeText.getText().toString();

                                        //출발시간 설정
                                        int hourStart = Integer.parseInt(promiseTime.split(":")[0]);
                                        int minuteStart = Integer.parseInt(promiseTime.split(":")[1]);
                                        int hour;
                                        int minute;
                                        if (hourStart - hourSpend < 0)
                                            hour = hourStart - hourSpend + 24;
                                        else hour = hourStart - hourSpend;
                                        if (minuteStart - minuteSpend < 0) {
                                            minute = minuteStart - minuteSpend + 60;
                                            hour -= 1;
                                        } else minute = minuteStart - minuteSpend;
                                        if (hour < 0) hour += 24;
                                        String startTime;

                                        if (minute / 10 < 1) startTime = hour + ":" + "0" + minute;
                                        else if (hour / 10 < 1)
                                            startTime = "0" + hour + ":" + minute;
                                        else if (hour / 10 < 1 && minute / 10 < 1)
                                            startTime = "0" + hour + ":" + "0" + minute;
                                        else startTime = hour + ":" + minute;
                                        spendingTimeText.setText(startTime);
                                        group.setStartTime(startTime);

                                        Toast.makeText(getApplicationContext(), "시간이 설정되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }, // 값설정시 호출될 리스너 등록
                                12, 00, true); // 기본값 시분 등록
                // true : 24 시간(0~23) 표시
                // false : 오전/오후 항목이 생김
                return tpd2;
        }
        return super.onCreateDialog(id);
    }


        public void loadPhone(final String roomName)
        {
            FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    myphone = dataSnapshot.child("phone").getValue(String.class);
                    myname = dataSnapshot.child("name").getValue(String.class);
                    FirebaseDatabase.getInstance().getReference("room/").child(roomName+"/").child("oner/"+myphone+"/").setValue(myname);
                    FirebaseDatabase.getInstance().getReference("room/").child(roomName+"/friendlist").child(myphone).setValue(myname);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
                                return true;
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

    public boolean compareTime(String n, String o){

        String[] nl = n.split(":");
        String[] ol = o.split(":");

        if(Integer.parseInt(nl[0])>Integer.parseInt(ol[0])){
            return true;
        }
        else{
            if(Integer.parseInt(nl[0])==Integer.parseInt(ol[0])){
                if (Integer.parseInt(nl[1])>=Integer.parseInt(ol[1])){
                    return true;
                }
                else return false;
            }
            return  false;
        }
    }


}


