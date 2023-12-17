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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryFragment extends Fragment {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private String imagePath;
    private DiaryDAO diaryDAO;
    private ImageView iv_preview;
    private Button btn_album;
    private EditText et_content;
    private Button btn_save;
    private TextView tv_date;

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

        diaryDAO.open();

        String temperature = "온도"; // 적절한 값을 설정
        String rainType = "강수형태"; // 적절한 값을 설정
        String sky = "하늘상태"; // 적절한 값을 설정

        Diary diary = new Diary(selectedDate, content, imagePath, temperature, rainType, sky);
        diaryDAO.insertEntry(diary);

        diaryDAO.close();

        if (onSaveClickListener != null) {
            onSaveClickListener.onSaveClick();
        }

        et_content.setText("");
        iv_preview.setImageDrawable(null);

//        Diary newDiary = new Diary();
//        newDiary.setDate();
//        newDiary.setContent();
//        newDiary.setRainType();
//        newDiary.setSky();
//        newDiary.setTemperature();
//
//        long insertedId = diaryDAO.insertEntry(newDiary);
//        List<Diary> allDiaries = diaryDAO.getAllEntries();
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

}
