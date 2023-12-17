package com.example.mp;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherInterface {
    @GET("getUltraSrtFcst?serviceKey=dVUD9sbwFYQajzthfDOfOb1k%2FaRqnoQJKWmRNG%2BPwISExYLYk9GpxXEMKbyzjOa%2BOyH5firravVrlYEITLkM3w%3D%3D")
    Call<Weather> GetWeather(@Query("numOfRows") int num_of_rows,
                             @Query("pageNo") int page_no,
                             @Query("dataType") String data_type,
                             @Query("base_date") String base_date,
                             @Query("base_time") String base_time,
                             @Query("nx") String nx,
                             @Query("ny") String ny);
}

    class Weather {
        public RESPONSE response;
    }

    class RESPONSE {
        public HEADER header;
        public BODY body;
    }

    class HEADER {
        public int resultCode;
        public String resultMsg;
    }

    class BODY {
        public String dataType;
        public ITEMS items;
        public int totalCount;
    }

    class ITEMS {
        public List<ITEM> item;
    }

    class ITEM {
        public String category;
        public String fcstDate;
        public String fcstTime;
        public String fcstValue;
    }

    class ApiObject {
        private static final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        public static WeatherInterface retrofitService = retrofit.create(WeatherInterface.class);
    }