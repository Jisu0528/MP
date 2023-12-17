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

//        CalendarView calendarView = findViewById(R.id.calendarView);
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                // 선택된 날짜에 대한 글 작성 프래그먼트를 보여주는 로직 추가
//                displayDiaryFragment(year, month, dayOfMonth);
//            }
//        });
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

//    private void hideCalendar() {
//        CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag(CalendarFragment.TAG);
//        if (calendarFragment != null) {
//            getSupportFragmentManager().beginTransaction().hide(calendarFragment).commit();
//        }
//    }
//
//    private void displayDiaryFragment(int year, int month, int dayOfMonth) {
//        // 선택된 날짜 정보를 번들에 담아 프래그먼트에 전달
//        Bundle bundle = new Bundle();
//        bundle.putInt("year", year);
//        bundle.putInt("month", month);
//        bundle.putInt("dayOfMonth", dayOfMonth);
//
//        // 프래그먼트 인스턴스 생성 및 번들 전달
//        DiaryFragment writeEntryFragment = new DiaryFragment();
//        writeEntryFragment.setArguments(bundle);
//
//        // 프래그먼트를 트랜잭션을 통해 화면에 표시
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, writeEntryFragment)
//                .addToBackStack(null)
//                .commit();
//    }
//
//    void showCalendar() {
//        CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag(CalendarFragment.TAG);
//        if (calendarFragment != null) {
//            getSupportFragmentManager().beginTransaction().show(calendarFragment).commit();
//        }
//    }
}
