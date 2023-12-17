package com.example.mp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class CalendarFragment extends Fragment {

    public interface OnDateClickListener {
        void onDateClick(String selectedDate);
    }

    private OnDateClickListener onDateClickListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDateClickListener) {
            onDateClickListener = (OnDateClickListener) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // 클릭된 날짜를 YYYYMMDD 형식으로 변환
            String selectedDate = String.format(Locale.getDefault(), "%04d%02d%02d", year, month + 1, dayOfMonth);

            // 날짜 전달
            onDateClickListener.onDateClick(selectedDate);
        });

        return view;
    }
}
