package com.example.alarm_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SetDetails extends AppCompatActivity {

    int alrmDay;
    int alrmMonth;
    int alrmYear;

    PendingIntent pendingIntent;
    AlarmManager alarmManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_details);

        Button set = findViewById(R.id.button3);
        Button remove = findViewById(R.id.button6);
        int duration = Toast.LENGTH_SHORT;

        Integer[] times = {5,10,15};
        Spinner spinner = findViewById(R.id.spinner3);
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, times);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        TimePicker timePicker = findViewById(R.id.timePicker1);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        /*
        Get system alarm manager
         */
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Alarms.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);


        /*

       Handle Date Values
         */
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day_of_month) {
                alrmDay = day_of_month;
                alrmMonth = month;
                alrmYear = year;
            }
        });


        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                long date = calendarView.getDate();


                //get time from time picker
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                    int hour = timePicker.getHour();
                    int minutes = timePicker.getMinute();
                    calendar.setTimeInMillis(date);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minutes);
                    calendar.set(Calendar.DAY_OF_MONTH, alrmDay);
                    calendar.set(Calendar.YEAR, alrmYear);
                    calendar.set(Calendar.MONTH, alrmMonth);

                    int interval = (spinner.getSelectedItemPosition()+1)*5;

                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*interval, pendingIntent);

                    /*
                    Toast
                     */

                    Toast toast = Toast.makeText(getApplicationContext(), "Alarm Set!", duration);
                    toast.show();

                    Log.d("log", "alarm set");

                    /*
                    Add time to shared preference.
                     */
                    SharedPreferences sharedPreferences = getSharedPreferences("AlarmList", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Time" , hour + ":" + minutes);
                    editor.putString("Date", alrmMonth + "/" + alrmDay + "/" + alrmYear);
                    editor.commit();
                }



            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alarmManager != null){
                    alarmManager.cancel(pendingIntent);
                    Toast toast = Toast.makeText(getApplicationContext(), "Alarm Removed!", duration);
                    toast.show();
                }
            }
        });

    }








    
}