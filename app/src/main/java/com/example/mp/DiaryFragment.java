package com.example.mp;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class DiaryFragment extends Fragment {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private String imagePath;
    private DiaryDAO diaryDAO;
    private ImageView iv_preview;
    private Button btn_album;
    private EditText et_content;
    private Button btn_save;
    private TextView tv_date;
    private TextView tv_sky;
    private TextView tv_temperate;
    private TextView tv_rain;
    private String sky;
    private String temperature;
    private String rainType;

    public interface OnSaveClickListener {
        void onSaveClick();
    }

    private OnSaveClickListener onSaveClickListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSaveClickListener) {
            onSaveClickListener = (OnSaveClickListener) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        iv_preview = view.findViewById(R.id.iv_preview);
        btn_album = view.findViewById(R.id.btn_album);
        et_content = view.findViewById(R.id.et_content);
        btn_save = view.findViewById(R.id.btn_save);
        tv_date = view.findViewById(R.id.tv_date);
        tv_rain = view.findViewById(R.id.tv_rain);
        tv_sky = view.findViewById(R.id.tv_sky);
        tv_temperate = view.findViewById(R.id.tv_temperate);

        // 이미지 선택
        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // 저장
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiary();
            }
        });

        diaryDAO = new DiaryDAO(getActivity());

        String selectedDate = getArguments().getString("selectedDate");
        tv_date.setText(formatDate(selectedDate) + " OOTD");

        setWeather("55", "127", selectedDate);


        return view;
    }

    public static DiaryFragment newInstance(String selectedDate) {
        DiaryFragment fragment = new DiaryFragment();
        Bundle args = new Bundle();
        args.putString("selectedDate", selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                String newImagePath = saveBitmapToFile(bitmap);
                imagePath = newImagePath;
                iv_preview.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String saveBitmapToFile(Bitmap bitmap) {
        String newImagePath = getNewImagePath();

        try {
            FileOutputStream out = new FileOutputStream(newImagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newImagePath;
    }

    private String getNewImagePath() {
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imageFile = new File(storageDir, imageFileName + ".jpg");
        return imageFile.getAbsolutePath();
    }

    private void saveDiary() {
        String selectedDate = getArguments().getString("selectedDate");
        String content = et_content.getText().toString();

        if (content.isEmpty()) {
            Toast.makeText(getActivity(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nx = "55";
        String ny = "127";
        setWeather(nx, ny, selectedDate);

        diaryDAO.open();

        Diary diary = new Diary(selectedDate, content, imagePath, temperature, rainType, sky);
        diaryDAO.insertEntry(diary);

        diaryDAO.close();

        if (onSaveClickListener != null) {
            onSaveClickListener.onSaveClick();
        }

        et_content.setText("");
        iv_preview.setImageDrawable(null);
    }

    private String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());

            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate; // 변환 실패 시 원본 반환
        }
    }

    private void setWeather(String nx, String ny, String selectedDate) {
        Calendar cal = Calendar.getInstance();
        String timeH = new SimpleDateFormat("HH", Locale.getDefault()).format(cal.getTime());
        String timeM = new SimpleDateFormat("mm", Locale.getDefault()).format(cal.getTime());

        Call<Weather> call = ApiObject.retrofitService.GetWeather(60, 1, "JSON", selectedDate, getBaseTime(timeH, timeM), nx, ny);

        // 비동기적 실행
        call.enqueue(new retrofit2.Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful()) {
                    List<ITEM> itemList = response.body().response.body.items.item;
                    WeatherModel weatherModel = new WeatherModel();

                    for (int i = 0; i < itemList.size(); i++) {
                        switch (itemList.get(i).category) {
                            case "PTY":
                                weatherModel.setRainType(itemList.get(i).fcstValue);
                                break;
                            case "SKY":
                                weatherModel.setSky(itemList.get(i).fcstValue);
                                break;
                            case "T1H":
                                weatherModel.setTemp(itemList.get(i).fcstValue);
                                break;
                            default:
                                continue;
                        }
                    }

                    sky = getSky(weatherModel.getSky());
                    temperature = weatherModel.getTemp();
                    rainType = getRainType(weatherModel.getRainType());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_temperate.setText(temperature + "°C");
                            tv_rain.setText(rainType);
                            tv_sky.setText(sky);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String getBaseTime(String timeH, String timeM) {
        return timeH + timeM;
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
}
