package com.example.mp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private String base_time = "1400";
    private String nx="55";
    private String ny="127";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvDate = findViewById(R.id.tv_date);
        weatherRecyclerView = findViewById(R.id.weatherRecyclerView);
        Button btnRefresh = findViewById(R.id.btn_refresh);

        // 리사이클러 뷰 매니저 설정
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        adapter = new WeatherAdapter(new WeatherModel[0]);
        weatherRecyclerView.setAdapter(adapter);
        weatherRecyclerView.setHasFixedSize(false);

        // 오늘 날짜 텍스트뷰 설정
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

        // nx, ny지점의 날씨 가져와서 설정하기
        setWeather(nx, ny);

        // <새로고침> 버튼 누를 때 날씨 정보 다시 가져오기
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeather(nx, ny);
            }
        });
    }

    // 날씨 가져와서 설정하기
    private void setWeather(String nx, String ny) {
        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        Calendar cal = Calendar.getInstance();
        base_date = new SimpleDateFormat("yyyyMMdd").format(cal.getTime()); // 현재 날짜
        String timeH = new SimpleDateFormat("HH").format(cal.getTime()); // 현재 시각
        String timeM = new SimpleDateFormat("mm").format(cal.getTime()); // 현재 분
        // API 가져오기 적당하게 변환
        base_time = getBaseTime(timeH, timeM);
        // 현재 시각이 00시이고 45분 이하여서 baseTime이 2330이면 어제 정보 받아오기
        if ("00".equals(timeH) && "2330".equals(base_time)) {
            cal.add(Calendar.DATE, -1);
            base_date = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        }

        // 날씨 정보 가져오기
        // (한 페이지 결과 수 = 60, 페이지 번호 = 1, 응답 자료 형식-"JSON", 발표 날짜, 발표 시각, 예보지점 좌표)
        Call<Weather> call = ApiObject.retrofitService.GetWeather(60, 1, "JSON", base_date, base_time, nx, ny);

        // 비동기적으로 실행하기
        call.enqueue(new retrofit2.Callback<Weather>() {
            // 응답 성공 시
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful()) {
                    // 날씨 정보 가져오기
                    List<ITEM> itemList = response.body().response.body.items.item;

                    // 현재 시각부터 1시간 뒤의 날씨 6개를 담을 배열
                    WeatherModel[] weatherArr = {new WeatherModel(), new WeatherModel(), new WeatherModel(),
                            new WeatherModel(), new WeatherModel(), new WeatherModel()};

                    // 배열 채우기
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

                    // 각 날짜 배열 시간 설정
                    for (int i = 0; i <= 5; i++) {
                        weatherArr[i].setFcstTime(itemList.get(i).fcstTime);
                    }

                    // 리사이클러 뷰에 데이터 연결
                    weatherRecyclerView.setAdapter(new WeatherAdapter(weatherArr));

                    // 토스트 띄우기
                    Toast.makeText(getApplicationContext(), itemList.get(0).fcstDate + ", " + itemList.get(0).fcstTime + "의 날씨 정보입니다.", Toast.LENGTH_SHORT).show();
                }
            }

            // 응답 실패 시
            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                TextView tvError = findViewById(R.id.tv_error);
                tvError.setText("api fail : " + t.getMessage() + "\n 다시 시도해주세요.");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    // 시간 설정
    private String getBaseTime(String h, String m) {
        String result = "";
        if (Integer.parseInt(m) < 45) {    // 45분 전
            if ("00".equals(h)) {
                result = "2330";
            } else {
                int resultH = Integer.parseInt(h) - 1;
                if (resultH < 10) {
                    result = "0" + resultH + "30";
                } else {
                    result = resultH + "30";
                }
            }
        } else {    // 45분 이후
            result = h + "30";
        }
        return result;
    }
}