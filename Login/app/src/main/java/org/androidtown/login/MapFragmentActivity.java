package org.androidtown.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;

import org.androidtown.login.Location.LocationAdapter;
import org.androidtown.login.Location.LocationInfo;
import org.androidtown.login.Location.LocationTool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapFragmentActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    static double promiseLongitude;
    static double promiseLatitude;
    static double myLongitude;
    static double myLatitude;
    private LocationTool tool;
    private ListView listView;
    private LocationAdapter adapter;
    private GpsInfo gps;
    private String RID,phone;
    private LocationInfo info;
    public ArrayList<LocationInfo> list = new ArrayList<>();
    static LatLng promiseLatLng;
    static LatLng myLatLng;
    static String myTime = "12:30", myDate = "2019-05-31", promiseTime, promiseDate,state = "off", name, nm;
    LocationInfo item;

    TextView nameTV, stateTV, syncTV;
    ImageButton syn, promise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_map);


        RID = getIntent().getStringExtra("rid");
        phone = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        listView = findViewById(R.id.listView);
        adapter = new LocationAdapter(list);
        listView.setAdapter(adapter);




        String lat = "";
        String lng = "";
        promiseLongitude = getIntent().getExtras().getDouble("longitude");
        promiseLatitude = getIntent().getExtras().getDouble("latitude");
        promiseTime = getIntent().getStringExtra("promiseTime");
        promiseDate = getIntent().getStringExtra("promiseDate");





        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);


        // TODO: 동기화시 내 현위치, 동기화 시간 전송 - double 2개, 시간 - 시간 구하기
        // TODO: 새로고침시 시간, 이름, 위도, 경도 받아오기 // 모든 사람 말풍선/ 정보 - 마지막 동기화 시간 및 이름
        // TODO: ListView - ArrayList - 시간 이름 위치 - 좌표의 지번
        // TODO: 인원 리스트에서 클릭시 해당인원에게로 카메라 이동
        // TODO: 약속장소와 거리 계산







    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final @NonNull NaverMap naverMap) {
        // 빌딩, 대중교통 LayerGroup 활성화
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true);
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true);
        // 현위치 체크 버튼 활성화
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);



        // 최초 새로고침
        FirebaseDatabase mdb = FirebaseDatabase.getInstance();
        DatabaseReference mr = mdb.getReference("room/"+RID+"/locationinfo");
        mr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot data: dataSnapshot.getChildren()) {
                        try {
                            double la, lo;
                            nm = data.child("name/").getValue(String.class);
                            la = data.child("lat/").getValue(double.class);
                            lo = data.child("lng/").getValue(double.class);
                            InfoWindow infoWindow = new InfoWindow();
                            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
                                @NonNull
                                @Override
                                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                                    return nm;
                                }
                            });

                            infoWindow.setPosition(new LatLng(la, lo));
                            infoWindow.open(naverMap);

                        } catch (NullPointerException e) {
                        }
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"동기화 없음",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"새로고침 에러",Toast.LENGTH_SHORT).show();

            }
        });



        info = new LocationInfo();
        info.setPhone(phone);
        info.setName(name);

        syn = findViewById(R.id.syn);

        syn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 권한 요청을 해야 함
                if (!isPermission) {
                    callPermission();
                    return;
                }

                gps = new GpsInfo(MapFragmentActivity.this);
                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {

                    myLatitude = gps.getLatitude();
                    myLongitude = gps.getLongitude();
                    myLatLng = new LatLng(myLatitude,myLongitude);
                    //latitude = gps.getLatitude();
                    //longitude = gps.getLongitude();
                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }

                info.setLat(myLatitude);
                info.setLog(myLongitude);


                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                myDate = sdf.format(date);
                SimpleDateFormat sdff = new SimpleDateFormat("hh:mm:a");
                myTime = sdff.format(date);
                if(myTime.split(":")[2].equals("PM")||myTime.split(":")[2].equals("오후")&&(!myTime.split(":")[2].equals("12"))){
                    myTime = (Integer.parseInt(myTime.split(":")[0])+12)+":"+ myTime.split(":")[1];
                }


                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(myLatLng)
                        .animate(CameraAnimation.Easing);
                naverMap.moveCamera(cameraUpdate);

                info.setSynctime(myTime);


                LatLng proLatLng = new LatLng(promiseLatitude, promiseLongitude);
                LatLng myLatLng = new LatLng(myLatitude,myLongitude);

                if (proLatLng.distanceTo(myLatLng)<30)
                {
                    if(compareDate(promiseDate,myDate))
                    {
                        if (compareTime(promiseTime,myTime))
                        {
                            state = "on";
                        }
                        else state = "off";
                    }
                    else state = "off";
                }
                else state = "off";

                info.setState(state);
                upLoadLocationInfo(info);

                FirebaseDatabase mdb = FirebaseDatabase.getInstance();
                DatabaseReference mr = mdb.getReference("room/"+RID+"/locationinfo");
                mr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            for(DataSnapshot data: dataSnapshot.getChildren())
                            {

                                double la,lo;
                                nm = data.child("name/").getValue(String.class);
                                la = data.child("lat/").getValue(double.class);
                                lo = data.child("lng/").getValue(double.class);
                                InfoWindow infoWindow = new InfoWindow();
                                infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
                                    @NonNull
                                    @Override
                                    public CharSequence getText(@NonNull InfoWindow infoWindow) {
                                        return nm;
                                    }
                                });

                                infoWindow.setPosition(new LatLng(la, lo));
                                infoWindow.open(naverMap);

                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"동기화 없음",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"새로고침 에러",Toast.LENGTH_SHORT).show();

                    }
                });



                Toast.makeText(getApplicationContext(),myTime + "에 동기화 완료하였습니다.",Toast.LENGTH_LONG).show();;


            }
        });
        try {
            downLoadLocationInfo();
        }
        catch (NullPointerException e){}
        promiseLatLng = new LatLng(promiseLatitude,promiseLongitude);
        Log.v("lat", promiseLatitude +"");


        promise = findViewById(R.id.promise);
        promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(promiseLatLng)
                        .animate(CameraAnimation.Easing);
                naverMap.moveCamera(cameraUpdate);

                Marker marker = new Marker();
                marker.setPosition(promiseLatLng);
                marker.setMap(naverMap);
            }
        });














        nameTV = findViewById(R.id.name);
        stateTV = findViewById(R.id.state);
        syncTV = findViewById(R.id.sync);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    item = list.get(position);


                    if(item.getLat()!=0) {
                        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(item.getLat(), item.getLng()))
                                .animate(CameraAnimation.Easing);
                        naverMap.moveCamera(cameraUpdate);
                    }


                else Toast.makeText(getApplicationContext(), "동기화 기록이 없습니다.", Toast.LENGTH_LONG).show();

            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    public void downLoadLocationInfo() {
        DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference myRoom = DB.child("room/" + RID + "/locationinfo/");
        myRoom.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {

                    list.add(dataSnapshot.getValue(LocationInfo.class));
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               for(int i=0;i<list.size();i++)
               {
                   if(list.get(i).getPhone().equals(dataSnapshot.child("phone/").getValue(String.class)))
                   {
                       list.set(i,dataSnapshot.getValue(LocationInfo.class));
                       adapter.notifyDataSetChanged();
                   }
               }

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
    public void upLoadLocationInfo(final LocationInfo item)
    {



    DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRoom = DB.child("room/"+RID+"/locationinfo/");
        myRoom.child(phone+"/").setValue(item);



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
        else if(Integer.parseInt(nl[0])==Integer.parseInt(ol[0])){
            if (Integer.parseInt(nl[1])>=Integer.parseInt(ol[1])){
                return true;
            }
            else return false;
        }
        else return false;
    }
}
