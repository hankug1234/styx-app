package org.androidtown.login;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;


public class MapFragmentActivityForSearch extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MaterialSearchBar.OnSearchActionListener, OnMapReadyCallback {

    ImageButton finishButton;
    TextView ll0tv, ll1tv, ll2tv, ll3tv, ll4tv;
    String address, coordinate, name = null;
    LinearLayout ll0, ll1, ll2, ll3, ll4;

    String xy[] = new String[5];
    String names[] = new String[5];

    double longitude, latitude, promiseLongitude = 0, promiseLatitude = 0;

    MaterialSearchBar searchBar;
    private DrawerLayout drawer;

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    private GpsInfo gps;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_map_for_search);




        drawer = findViewById(R.id.drawer_layout);
        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);
        searchBar.inflateMenu(R.menu.main);
        searchBar.setText("");
        Log.d("LOG_TAG", getClass().getSimpleName() + ": text " + searchBar.getText());
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        final FloatingActionButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.enableSearch();
            }
        });






        ll0 = findViewById(R.id.ll0);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);
        ll4 = findViewById(R.id.ll4);

        ll0tv = findViewById(R.id.ll0tv);
        ll1tv = findViewById(R.id.ll1tv);
        ll2tv = findViewById(R.id.ll2tv);
        ll3tv = findViewById(R.id.ll3tv);
        ll4tv = findViewById(R.id.ll4tv);

        finishButton = findViewById(R.id.finish_button);


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


/*
        searchText.setImeOptions(EditorInfo.IME_ACTION_DONE); // 키보드 확인 버튼 클릭
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow( searchText.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // 권한 요청을 해야 함
                if (!isPermission) {
                    callPermission();
                    return;
                }

                gps = new GpsInfo(MapFragmentActivityForSearch.this);
                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    //latitude = gps.getLatitude();
                    //longitude = gps.getLongitude();
                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }


                System.out.println(); //결과 : 3.14
                String laS = String.format("%.7f", latitude);
                String loS = String.format("%.7f", longitude);
                address = searchText.getText().toString();
                coordinate = loS + "," + laS;

                NetworkTask networkTask = new NetworkTask();
                networkTask.execute(); // 데이터 가져오기

            }
        });
        // AsyncTask를 통해 HttpURLConnection 수행.

*/



        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 111
                if (name != null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.make, null);

                    TextView placeText = (TextView) view.findViewById(R.id.placeText);
                    placeText.setText(name);
                    ImageView placeImage;
                    placeImage = (ImageView) view.findViewById(R.id.placeImage);
                    placeImage.setImageResource(R.drawable.location_color);

                    finish();
                    Intent intent = new Intent(getApplicationContext(), make.class);
                    intent.putExtra("placeName", name);
                    intent.putExtra("promiseLongitude", promiseLongitude);
                    intent.putExtra("promiseLatitude", promiseLatitude);
                    startActivity(intent);
                }
                else Toast.makeText(getApplicationContext(),"장소를 선택해 주세요", Toast.LENGTH_LONG).show();
            }

        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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


        final Marker marker0 = new Marker();
        final Marker marker1 = new Marker();
        final Marker marker2 = new Marker();
        final Marker marker3 = new Marker();
        final Marker marker4 = new Marker();



        ll0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name != null) {
                    promiseLatitude = Double.parseDouble(xy[0].split(",")[0]);
                    promiseLongitude = Double.parseDouble(xy[0].split(",")[1]);
                    name = names[0];

                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(promiseLatitude, promiseLongitude))
                            .animate(CameraAnimation.Easing);
                    naverMap.moveCamera(cameraUpdate);
                    setBlue(0);

                    marker1.setMap(null);
                    marker2.setMap(null);
                    marker3.setMap(null);
                    marker4.setMap(null);
                    marker0.setPosition(new LatLng(promiseLatitude, promiseLongitude));
                    marker0.setMap(naverMap);
                }
            }
        });
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name != null) {
                    promiseLatitude = Double.parseDouble(xy[1].split(",")[0]);
                    promiseLongitude = Double.parseDouble(xy[1].split(",")[1]);
                    name = names[1];

                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(promiseLatitude, promiseLongitude))
                            .animate(CameraAnimation.Easing);
                    naverMap.moveCamera(cameraUpdate);
                    setBlue(1);

                    marker0.setMap(null);
                    marker2.setMap(null);
                    marker3.setMap(null);
                    marker4.setMap(null);
                    marker1.setPosition(new LatLng(promiseLatitude, promiseLongitude));
                    marker1.setMap(naverMap);
                }
            }
        });
        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name != null) {
                    promiseLatitude = Double.parseDouble(xy[2].split(",")[0]);
                    promiseLongitude = Double.parseDouble(xy[2].split(",")[1]);
                    name = names[2];

                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(promiseLatitude, promiseLongitude))
                            .animate(CameraAnimation.Easing);
                    naverMap.moveCamera(cameraUpdate);
                    setBlue(2);

                    marker0.setMap(null);
                    marker1.setMap(null);
                    marker3.setMap(null);
                    marker4.setMap(null);
                    marker2.setPosition(new LatLng(promiseLatitude, promiseLongitude));
                    marker2.setMap(naverMap);
                }
            }
        });
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name != null) {
                    promiseLatitude = Double.parseDouble(xy[3].split(",")[0]);
                    promiseLongitude = Double.parseDouble(xy[3].split(",")[1]);
                    name = names[3];

                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(promiseLatitude, promiseLongitude))
                            .animate(CameraAnimation.Easing);
                    naverMap.moveCamera(cameraUpdate);
                    setBlue(3);

                    marker0.setMap(null);
                    marker1.setMap(null);
                    marker2.setMap(null);
                    marker4.setMap(null);
                    marker3.setPosition(new LatLng(promiseLatitude, promiseLongitude));
                    marker3.setMap(naverMap);
                }
            }
        });
        ll4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name != null) {
                    promiseLatitude = Double.parseDouble(xy[4].split(",")[0]);
                    promiseLongitude = Double.parseDouble(xy[4].split(",")[1]);
                    name = names[4];

                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(promiseLatitude, promiseLongitude))
                            .animate(CameraAnimation.Easing);
                    naverMap.moveCamera(cameraUpdate);
                    setBlue(4);

                    marker0.setMap(null);
                    marker1.setMap(null);
                    marker2.setMap(null);
                    marker3.setMap(null);
                    marker4.setPosition(new LatLng(promiseLatitude, promiseLongitude));
                    marker4.setMap(naverMap);
                }
            }
        });




/*
        Marker marker = new Marker();
        marker.setPosition(new LatLng(Double.parseDouble(xy[0].split(",")[0]), Double.parseDouble(xy[0].split(",")[1])));
        marker.setMap(naverMap);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(Double.parseDouble(xy[0].split(",")[0]), Double.parseDouble(xy[0].split(",")[1])));
        naverMap.moveCamera(cameraUpdate);
        */

        //마커 찍기
                //지도 이동
                //값저장


    }



    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask() {

        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(address, coordinate); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String ss[] = s.split("\n");
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            try {
                ll0tv.setText(ss[0]);
                xy[0] = ss[1];
                names[0] = ss[0].split(" ")[2];

                ll1tv.setText(ss[2]);
                xy[1] = ss[3];
                names[1] = ss[2].split(" ")[2];

                ll2tv.setText(ss[4]);
                xy[2] = ss[5];
                names[2] = ss[4].split(" ")[2];

                ll3tv.setText(ss[6]);
                xy[3] = ss[7];
                names[3] = ss[6].split(" ")[2];

                ll4tv.setText(ss[8]);
                xy[4] = ss[9];
                names[4] = ss[8].split(" ")[2];
            }
            catch (ArrayIndexOutOfBoundsException e){
                Toast.makeText(getApplicationContext(), "검색결과가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            }




            name = "not null!";

        }

    }


    public void setBlue(int i){
        switch (i){
            case 0:
                ll0tv.setTextColor(Color.parseColor("#B5532F63"));
                ll1tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll2tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll3tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll4tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll0.setBackgroundColor(Color.parseColor("#FFD5D5"));
                ll1.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll2.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll3.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll4.setBackgroundColor(Color.parseColor("#00ff0000"));
                break;
            case 1:
                ll0tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll1tv.setTextColor(Color.parseColor("#B5532F63"));
                ll2tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll3tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll4tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll0.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll1.setBackgroundColor(Color.parseColor("#FFD5D5"));
                ll2.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll3.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll4.setBackgroundColor(Color.parseColor("#00ff0000"));
                break;
            case 2:
                ll0tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll1tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll2tv.setTextColor(Color.parseColor("#B5532F63"));
                ll3tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll4tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll0.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll1.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll2.setBackgroundColor(Color.parseColor("#FFD5D5"));
                ll3.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll4.setBackgroundColor(Color.parseColor("#00ff0000"));
                break;
            case 3:
                ll0tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll1tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll2tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll3tv.setTextColor(Color.parseColor("#B5532F63"));
                ll4tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll0.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll1.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll2.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll3.setBackgroundColor(Color.parseColor("#FFD5D5"));
                ll4.setBackgroundColor(Color.parseColor("#00ff0000"));
                break;
            case 4:
                ll0tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll1tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll2tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll3tv.setTextColor(Color.parseColor("#FFD5D5"));
                ll4tv.setTextColor(Color.parseColor("#B5532F63"));
                ll0.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll1.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll2.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll3.setBackgroundColor(Color.parseColor("#00ff0000"));
                ll4.setBackgroundColor(Color.parseColor("#FFD5D5"));
                break;
        }
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




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

        // 검색 누를시의 변화


        // 권한 요청을 해야 함
        if (!isPermission) {
            callPermission();
            return;
        }

        gps = new GpsInfo(MapFragmentActivityForSearch.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            //latitude = gps.getLatitude();
            //longitude = gps.getLongitude();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }


        System.out.println(); //결과 : 3.14
        String laS = String.format("%.7f", latitude);
        String loS = String.format("%.7f", longitude);

        address = searchBar.getText();
        coordinate = loS + "," + laS;

        NetworkTask networkTask = new NetworkTask();
        networkTask.execute(); // 데이터 가져오기

        // AsyncTask를 통해 HttpURLConnection 수행.


        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBar.getSearchEditText().getWindowToken(), 0);
        // 검색 목록 나오게

    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.disableSearch();
                break;
        }
    }


}
