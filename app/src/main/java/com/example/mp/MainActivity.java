package com.example.mp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private WeatherAdapter adapter;
    private RecyclerView weatherRecyclerView;
    private String base_date = "20231215";
    private String base_time = "0100";
    private String nx="55";
    private String ny="127";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvDate = findViewById(R.id.tv_date);
        weatherRecyclerView = findViewById(R.id.weatherRecyclerView);
        ImageButton btn_refresh = findViewById(R.id.btn_refresh);
        ImageButton btn_calendar = findViewById(R.id.btn_calendar);

        // 리사이클러 뷰
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        adapter = new WeatherAdapter(new WeatherModel[0]);
        weatherRecyclerView.setAdapter(adapter);
        weatherRecyclerView.setHasFixedSize(false);

        Calendar cal = Calendar.getInstance();
        tvDate.setText(new SimpleDateFormat("MM월 dd일").format(cal.getTime()) + " 날씨");

        // 현재 위치
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( MainActivity.this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }
        else{
            // 가장최근 위치정보 가져오기
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                nx = String.valueOf(Math.round(Math.abs(latitude)));
                ny = String.valueOf(Math.round(Math.abs(longitude)));
            }
        }

        setWeather(nx, ny);

        // 새로고침
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeather(nx, ny);
            }
        });

        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
    }

    // 날씨 설정
    private void setWeather(String nx, String ny) {
        Calendar cal = Calendar.getInstance();
        base_date = new SimpleDateFormat("yyyyMMdd").format(cal.getTime()); // 날짜
        String timeH = new SimpleDateFormat("HH").format(cal.getTime()); // 시
        String timeM = new SimpleDateFormat("mm").format(cal.getTime()); // 분
        base_time = getBaseTime(timeH, timeM);
        if ("00".equals(timeH) && "2330".equals(base_time)) {
            cal.add(Calendar.DATE, -1);
            base_date = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        }

        Call<Weather> call = ApiObject.retrofitService.GetWeather(60, 1, "JSON", base_date, base_time, nx, ny);

        // 비동기적 실행
        call.enqueue(new retrofit2.Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful()) {
                    List<ITEM> itemList = response.body().response.body.items.item;

                    WeatherModel[] weatherArr = {new WeatherModel(), new WeatherModel(), new WeatherModel(),
                            new WeatherModel(), new WeatherModel(), new WeatherModel()};

                    int index = 0;
                    int totalCount = response.body().response.body.totalCount - 1;
                    for (int i = 0; i <= totalCount; i++) {
                        index %= 6;
                        switch (itemList.get(i).category) {
                            case "PTY":
                                weatherArr[index].setRainType(itemList.get(i).fcstValue);
                                break;
                            case "REH":
                                weatherArr[index].setHumidity(itemList.get(i).fcstValue);
                                break;
                            case "SKY":
                                weatherArr[index].setSky(itemList.get(i).fcstValue);
                                break;
                            case "T1H":
                                weatherArr[index].setTemp(itemList.get(i).fcstValue);
                                break;
                            default:
                                continue;
                        }
                        index++;
                    }

                    for (int i = 0; i <= 5; i++) {
                        weatherArr[i].setFcstTime(itemList.get(i).fcstTime);
                    }

                    // 리사이클러 뷰 연결
                    weatherRecyclerView.setAdapter(new WeatherAdapter(weatherArr));

                    Toast.makeText(getApplicationContext(), itemList.get(0).fcstDate + ", " + itemList.get(0).fcstTime + "의 날씨 정보입니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                TextView tvError = findViewById(R.id.tv_error);
                tvError.setText("API 연결 실패 : " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private String getBaseTime(String hour, String minute) {
        String result = "";
        if (Integer.parseInt(minute) < 50) {    // 50분 전
            if ("00".equals(hour)) {
                result = "2330";
            } else {
                int resultH = Integer.parseInt(hour) - 1;
                if (resultH < 10) {
                    result = "0" + resultH + "30";
                } else {
                    result = resultH + "30";
                }
            }
        } else {    // 50분 이후
            result = hour + "30";
        }
        return result;
    }
}