package com.example.and_map;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// gps를 쓰기 위한 manifest, gradle 설정 완료 코드 작성만 하면 됨
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //test_start
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    //test_middle
    int count = 0;
    int lCount = 0;
    int geoCount = 0;
    Double geoLat = 0.0;
    Double geoLng = 0.0;
    String info = "null";
    String text = "";
    //test_end
    private Spinner maps_spinner;
    private NaverMap mNaverMap;
    private CheckBox mCheck;
    private Button iButton;
    private Button zButton;
    private InfoWindow infoWindow = new InfoWindow();
    private EditText eText;
    private Button eButton;
    private ArrayList<Marker> arrayListGeoMarker = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        maps_spinner = (Spinner) findViewById(R.id.maps_spinner);

        final ArrayList<String> list = new ArrayList<>();
        list.add("satellite");
        list.add("road");
        list.add("hybrid");

        ArrayAdapter spinnerAdapter;
        spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list);
        maps_spinner.setAdapter(spinnerAdapter);
        mCheck = (CheckBox) findViewById(R.id.check);
        iButton = (Button) findViewById(R.id.initButton);
        zButton = (Button)findViewById(R.id.zButton);


        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        //test
        eText = (EditText) findViewById(R.id.eText);
        eButton = (Button) findViewById(R.id.eButton);

    }

    @Override
    public void onMapReady(@NonNull @org.jetbrains.annotations.NotNull NaverMap naverMap) {
        mNaverMap = naverMap;
        //test

        ArrayList<Marker> arrayListMarker = new ArrayList<>();

        ArrayList<Marker> arrayListLongMarker = new ArrayList<>();



        // 마커 및 선 연결, 폴리곤 만들기
        Marker marker1 = new Marker();
        Marker marker2 = new Marker();
        Marker marker3 = new Marker();
        PolylineOverlay polyline = new PolylineOverlay();
        marker1.setPosition(new LatLng(35.94616, 126.68220));
        marker2.setPosition(new LatLng(35.96851, 126.73797));
        marker3.setPosition(new LatLng(35.97035, 126.61734));

        ArrayList<LatLng> arrayListLatLng = new ArrayList<>();

        //마커 처음 마커 생성
        arrayListLatLng.add(new LatLng(35.94616, 126.68220));
        arrayListMarker.add(count,new Marker());
        arrayListMarker.get(count).setPosition(arrayListLatLng.get(count));
        arrayListMarker.get(count).setMap(naverMap);
        count++;

        arrayListLatLng.add(new LatLng(35.96851, 126.73797));
        arrayListMarker.add(count,new Marker());
        arrayListMarker.get(count).setPosition(arrayListLatLng.get(count));
        arrayListMarker.get(count).setMap(naverMap);
        count++;

        arrayListLatLng.add(new LatLng(35.97035, 126.61734));
        arrayListMarker.add(count,new Marker());
        arrayListMarker.get(count).setPosition(arrayListLatLng.get(count));
        arrayListMarker.get(count).setMap(naverMap);
        count++;



        PolygonOverlay polygon = new PolygonOverlay();




        polygon.setCoords(Arrays.asList(new LatLng(35.94616, 126.68220), new LatLng(35.96851, 126.73797), new LatLng(35.97035, 126.61734)));
        polygon.setColor(0x7fff0000); // 50% 빨간색

        polyline.setCoords(Arrays.asList(new LatLng(35.94616, 126.68220), new LatLng(35.96851, 126.73797), new LatLng(35.97035, 126.61734)));
        marker1.setMap(naverMap);
        marker2.setMap(naverMap);
        marker3.setMap(naverMap);
        polyline.setMap(naverMap);
        polygon.setMap(naverMap);

        //처음 실행 시 위성지도로 표시
        naverMap.setMapType(NaverMap.MapType.Satellite);
        //처음 실행 시 카메라 위치 이동(군산대)
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(35.94616, 126.68220));
        naverMap.moveCamera(cameraUpdate);
        // 스피너
        maps_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //               Log.d("myLog", "position : " + position);
//                naverMap.setMapType(NaverMap.MapType.Satellite);
                if (position == 0) naverMap.setMapType(NaverMap.MapType.Satellite);
                else if (position == 1) naverMap.setMapType(NaverMap.MapType.Basic);
                else if (position == 2) naverMap.setMapType(NaverMap.MapType.Hybrid);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 체크박스
        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheck.isChecked()) {
                    naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, true);
                } else {
                    naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false);
                }
            }
        });




        // 롱 클릭 시 test
        mNaverMap.setOnMapLongClickListener(new NaverMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull @NotNull PointF pointF, @NonNull @NotNull LatLng latLng) {



                arrayListLongMarker.add(lCount, new Marker());

                arrayListLongMarker.get(lCount).setPosition(latLng);
                arrayListLongMarker.get(lCount).setMap(naverMap);


                // async로 처리 후, async에서 정보 텍스트를 수정함
                downloadAxis da = new downloadAxis();
                da.execute(latLng);

                infoWindow.open(arrayListLongMarker.get(lCount));
                lCount++;


            }
        });

        // 클릭 시 좌표 표시
        mNaverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull PointF pointF, @NonNull @NotNull LatLng latLng) {

                Toast.makeText(getApplicationContext(), "위도: " + latLng.latitude + ", 경도: " + latLng.longitude, Toast.LENGTH_SHORT).show();

                // 클릭 시 마커 생성 후, 폴리곤 그리기
                arrayListLatLng.add(latLng);
                arrayListMarker.add(count,new Marker());


                arrayListMarker.get(count).setPosition(arrayListLatLng.get(count));
                arrayListMarker.get(count).setMap(naverMap);

                polygon.setMap(null);
                if(count >= 2) {
                    polygon.setCoords(arrayListLatLng);
                    polygon.setColor(0x7fff0000);
                    polygon.setMap(naverMap);
                }
                count++;

            }
        });
        // 오버레이 전부 지우는 버튼 클릭 이벤트
        iButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker1.setMap(null);
                marker2.setMap(null);
                marker3.setMap(null);
                polyline.setMap(null);


                for(int i = 0; i < count; i++)
                {
                    arrayListMarker.get(i).setMap(null);
                }

                for(int i = 0; i < lCount; i++)
                {
                    arrayListLongMarker.get(i).setMap(null);
                }

                //test start
                for(int i = 0; i < geoCount; i++)
                {
                    arrayListGeoMarker.get(i).setMap(null);
                }

                arrayListGeoMarker.clear();
                // test end
                arrayListLatLng.clear();
                arrayListMarker.clear();
                arrayListLongMarker.clear();
                polygon.setMap(null);

                count = 0;
                lCount = 0;
                geoCount = 0;
               // Log.d("mytag", "난 여기가 궁금해");
            }
        });

        // 삼각형의 중심으로 좌표 이동
        zButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(35.96167, 126.67916), 12);
                naverMap.moveCamera(cameraUpdate);
            }
        });

        //gps로 내 위치 설정하기
        naverMap.setLocationSource(locationSource);
      //  Log.d("mytag112", "좌표 : " + locationSource.getLastLocation() );
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

        // 엔터 버튼 테스트
        eButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String test = "전라북도 군산시 신관동 290";
                        StringBuilder urlGeoBuilder = new StringBuilder("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" +  test);
                        StringBuilder output = new StringBuilder();
                        URL gURL;
                        HttpURLConnection connection;


                        try{
                            gURL = new URL(urlGeoBuilder.toString());

                            connection =  (HttpURLConnection)gURL.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setRequestProperty("Content-type", "application/json");
                            connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "jp5obaai53");
                            connection.setRequestProperty("X-NCP-APIGW-API-KEY", "iPiOYsYw0QCqzBMy0rSu6YgF9gdDjjYwBOxsoBZp");


                            BufferedReader geoBuffer;
                            if(connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300) {
                                geoBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            } else {
                                geoBuffer = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                            }
                            String line;
                            while ((line = geoBuffer.readLine()) != null) {
                                output.append(line);
                            }
                           // Log.d("test_call","");
                            geoBuffer.close();
                            connection.disconnect();

                        } catch (Exception e) {
                            Log.d("test_d", "connection failed");
                        }
                        Log.d("test", output.toString());

                        JsonParser jsonGeoParser = new JsonParser();


                        JsonObject jObject = (JsonObject) jsonGeoParser.parse(output.toString());
                        JsonArray jsonArray = (JsonArray) jObject.get("addresses");
                        jObject = (JsonObject) jsonArray.get(0);
                        String result = "경도" + jObject.get("x").getAsString() + jObject.get("y").getAsString();
                        Log.d("test_parse", result);
                        geoLat = jObject.get("y").getAsDouble();
                        geoLng = jObject.get("x").getAsDouble();


                    }
                }).start();
                arrayListGeoMarker.add(new Marker());
                arrayListGeoMarker.get(geoCount).setPosition(new LatLng(geoLat, geoLng));
                arrayListGeoMarker.get(geoCount).setMap(naverMap);
                geoCount++;
            }
        });


        // test 에디트 텍스트 입력 받은 값 가져오기
        text = eText.getText().toString();
        Log.d("test_Text", text);
    }

    //권한 받아오기
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);


    }

    // 비동기로 리버스 지오코딩해서 정보창 처리하기

    private class downloadAxis extends AsyncTask<LatLng , String, String>
    {

        private StringBuilder urlBuilder;
        private URL url;
        private HttpURLConnection conn;
        @Override
        protected String doInBackground(LatLng... latLngs)
        {

            String strCoord = String.valueOf(latLngs[0].longitude)+","+String.valueOf(latLngs[0].latitude);
            StringBuilder sb = new StringBuilder();

            urlBuilder = new StringBuilder("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=" + strCoord + "&sourcecrs=epsg:4326&output=json&orders=addr");

            try{
                url = new URL(urlBuilder.toString());

                conn =  (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "jp5obaai53");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY", "iPiOYsYw0QCqzBMy0rSu6YgF9gdDjjYwBOxsoBZp");



                BufferedReader br;
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                conn.disconnect();

              } catch (Exception e) {
                 return null;
              }
                Log.d("test", sb.toString());


                return sb.toString();

        }

        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);


            JsonParser jsonParser = new JsonParser();


            JsonObject jObject = (JsonObject) jsonParser.parse(result);
            JsonArray jsonArray = (JsonArray) jObject.get("results");
            jObject = (JsonObject) jsonArray.get(0);
            jObject = (JsonObject) jObject.get("region");
            jObject = (JsonObject) jObject.get("area1");
            String data = jObject.get("name").getAsString();

            jObject = (JsonObject) jsonParser.parse(result);
            jsonArray = (JsonArray) jObject.get("results");
            jObject = (JsonObject) jsonArray.get(0);
            jObject = (JsonObject) jObject.get("region");
            jObject = (JsonObject) jObject.get("area2");
            data = data +" " +jObject.get("name").getAsString();

            jObject = (JsonObject) jsonParser.parse(result);
            jsonArray = (JsonArray) jObject.get("results");
            jObject = (JsonObject) jsonArray.get(0);
            jObject = (JsonObject) jObject.get("region");
            jObject = (JsonObject) jObject.get("area3");
            data = data +" " +jObject.get("name").getAsString();


            jObject = (JsonObject) jsonParser.parse(result);
            jsonArray = (JsonArray) jObject.get("results");
            jObject = (JsonObject) jsonArray.get(0);
            jObject = (JsonObject) jObject.get("land");
            data = data +" " +jObject.get("number1").getAsString();
            if(jObject.get("number2").getAsString().equals("") == true);
            else {
                data = data + "-" + jObject.get("number2").getAsString();
            }
             Log.d("test_d", data);
             info = data;

            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
                @NonNull
                @NotNull
                @Override
                public CharSequence getText(@NonNull @NotNull InfoWindow infoWindow) {
                    return info;
                }
            });



        }
    }

}


