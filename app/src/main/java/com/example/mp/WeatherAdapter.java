package com.example.mp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private final WeatherModel[] items;

    public WeatherAdapter(WeatherModel[] items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherModel item = items[position];
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_time;
        private final TextView tv_rainType;
        private final TextView tv_humidity;
        private final TextView tv_sky;
        private final TextView tv_temp;
        private final TextView tv_recommends;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_rainType = itemView.findViewById(R.id.tv_rainType);
            tv_humidity = itemView.findViewById(R.id.tv_humidity);
            tv_sky = itemView.findViewById(R.id.tv_sky);
            tv_temp = itemView.findViewById(R.id.tv_temp);
            tv_recommends = itemView.findViewById(R.id.tv_recommends);
        }

        public void setItem(WeatherModel item) {
            tv_time.setText(item.getFcstTime());
            tv_rainType.setText(getRainType(item.getRainType()));
            tv_humidity.setText(item.getHumidity());
            tv_sky.setText(getSky(item.getSky()));
            tv_temp.setText(item.getTemp() + "°");
            tv_recommends.setText(getRecommends(Integer.parseInt(item.getTemp())));
        }
    }

    private static String getRainType(String rainType) {
        switch (rainType) {
            case "0":
                return "없음";
            case "1":
                return "비";
            case "2":
                return "비/눈";
            case "3":
                return "눈";
            default:
                return "오류: 강수 : " + rainType;
        }
    }

    private static String getSky(String sky) {
        switch (sky) {
            case "1":
                return "맑음";
            case "3":
                return "구름 많음";
            case "4":
                return "흐림";
            default:
                return "오류: 날씨 : " + sky;
        }
    }

    private static String getRecommends(int temp) {
        if (temp >= 5 && temp <= 8) {
            return "울코트, 가죽자켓,\n기모, 레깅스";
        } else if (temp >= 9 && temp <= 11) {
            return "트렌치코트, 야상,\n점퍼, 니트, 스타킹";
        } else if (temp >= 12 && temp <= 16) {
            return "자켓, 가디건,\n면바지, 청바지";
        } else if (temp >= 17 && temp <= 19) {
            return "니트, 맨투맨, \n후드, 긴바지";
        } else if (temp >= 20 && temp <= 22) {
            return "블라우스, 긴팔티, \n슬랙스, 얇은가디건";
        } else if (temp >= 23 && temp <= 27) {
            return "얇은 셔츠, 반바지,\n면바지, 반팔";
        } else if (temp >= 28 && temp <= 40) {
            return "민소매, 반바지,\n반팔, 원피스";
        } else {
            return "패딩, 두꺼운코트,\n목도리, 기모";
        }
    }
}
