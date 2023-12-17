package com.example.mp;

import android.os.Bundle;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CalendarActivity extends AppCompatActivity implements CalendarFragment.OnDateClickListener, DiaryFragment.OnSaveClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        showCalendarFragment();
    }

    @Override
    public void onDateClick(String selectedDate) {
        showDiaryFragment(selectedDate);
    }

    @Override
    public void onSaveClick() {
        showCalendarFragment();
    }

    private void showCalendarFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // CalendarFragment 생성 및 추가
        CalendarFragment calendarFragment = new CalendarFragment();
        fragmentTransaction.replace(R.id.fragment_container, calendarFragment);
        fragmentTransaction.commit();
    }

    private void showDiaryFragment(String selectedDate) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // DiaryFragment 생성 및 추가
        DiaryFragment diaryFragment = DiaryFragment.newInstance(selectedDate);
        fragmentTransaction.replace(R.id.fragment_container, diaryFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
