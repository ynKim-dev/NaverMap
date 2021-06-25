package com.example.and_map;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
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
import java.net.MalformedURLException;
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
    //test_end
    private Spinner maps_spinner;
    private NaverMap mNaverMap;
    private CheckBox mCheck;
    private Button iButton;
    private Button zButton;

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

        //test
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onMapReady(@NonNull @org.jetbrains.annotations.NotNull NaverMap naverMap) {
        mNaverMap = naverMap;
        //test

        ArrayList<Marker> arrayListMarker = new ArrayList<>();

        ArrayList<Marker> arrayListLongMarker = new ArrayList<>();

        // 정보 창 test
        InfoWindow infoWindow = new InfoWindow();

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
                infoWindow.open(arrayListLongMarker.get(lCount));
                lCount++;

                String[] url = getUrl(latLng);
                downloadAxis da = new downloadAxis();
                da.execute(url[0], Double.toString(latLng.latitude), Double.toString(latLng.longitude));

                // String url = getUrl(latLng);
               // Log.d("tag33", "메세지" + url);

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

                arrayListLatLng.clear();
                arrayListMarker.clear();
                arrayListLongMarker.clear();
                polygon.setMap(null);

                count = 0;
                lCount = 0;
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
    // url 만들기 test openconnection 필요
    /*
    private String getUrl(LatLng latLng)
    {

        String[] url = {"", ""};
        InputStream iStream = null;
        String data;
        String latlng_place = latLng.latitude + "," + latLng.longitude;
        url[0] = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=" + latlng_place + "&sourcecrs=epsg:4326&output=json&orders=addr"; // 수정 필요

        HttpURLConnection conn = null;

        try {
            URL mUrl = new URL(url[0]);
            conn = (HttpURLConnection)mUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "jp5obaai53");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", "iPiOYsYw0QCqzBMy0rSu6YgF9gdDjjYwBOxsoBZp");
            conn.connect();

            iStream = conn.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            data = "Null";
        }


        return data;
    }
    */
    private String[] getUrl(LatLng latLng)
    {
        String[] url = {"",""};
        String addressAxis = latLng.latitude  + ","  + latLng.longitude;
        url[0] = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=" + addressAxis + "&sourcecrs=epsg:4326&output=json&orders=addr";

        return url;
    }


    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection conn = null;

        try{
            URL mUrl = new URL(strUrl);

            conn =  (HttpURLConnection)mUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "jp5obaai53");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", "iPiOYsYw0QCqzBMy0rSu6YgF9gdDjjYwBOxsoBZp");


            conn.connect();

            iStream = conn.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            Log.d("testU", br.toString());
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();

            br.close();
        }
        catch (Exception e)
        {
            Log.d("Urlfail", "url download fail");
        }
        finally {
            Log.d("urlend", "end");
            iStream.close();
            conn.disconnect();
        }

        return data;
    }

    public List<String> R_geocoding(JSONObject jObject)
    {
        List<String> f_a = new ArrayList<>();
        JSONArray jResults;
        JSONArray jAddress;
        try{
            jResults = jObject.getJSONArray("results");
            jAddress = ((JSONObject)jResults.get(0)).getJSONArray("address_components");
            f_a.add(((JSONObject)(jAddress.get(0))).get("long_name").toString()+" " +((JSONObject)(jAddress.get(1))).get("long_name").toString());
            f_a.add(((JSONObject)jResults.get(0)).get("formatted_address").toString());
            Log.d("R_g", f_a.get(0) );
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.d("R_g","R_g error");

        }
        return f_a;
    }

    // async test

    private class downloadAxis extends AsyncTask<String, String, String[]>
    {
        protected String[] doInBackground(String... URLnLL)
        {
            String[] data = {"", "", ""};
            try{
                data[0] = downloadUrl(URLnLL[0]);
                data[1] = URLnLL[1];
                data[2] = URLnLL[2];
            } catch (Exception e){
                Log.d("Background task", e.toString());
            }

            return data;
        }

        protected void onPostExecute(String[] result)
        {
            super.onPostExecute(result);
            JSONObject jObject;
            List<String> Rgeo;
            try{
                jObject = new JSONObject(result[0]);

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

