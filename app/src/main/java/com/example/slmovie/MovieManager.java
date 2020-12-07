package com.example.slmovie;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MovieManager extends AppCompatActivity
{
    ImageView img;
    TextView name;
    TextView genre;
    Button button;
    CalendarView calendarView;
    long date;
    Context context;
    String name_m;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_manager);

        img = (ImageView) findViewById(R.id.imageView);
        name = (TextView) findViewById(R.id.textView);
        genre = (TextView) findViewById(R.id.textView10);
        button = (Button) findViewById(R.id.button);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        date = calendarView.getDate();

        final Intent intent = getIntent();
        final AlarmManager alarmManager = (AlarmManager)
                this.getSystemService(Context.ALARM_SERVICE);
        context = this;

        String id = intent.getStringExtra("id");
        for(Movie movie : User.movies)
        {
            if(movie.id.equals(id))
            {
                name_m = movie.name;
                name.setText(movie.name);
                String s = "Жанр: ";
                for(String genre : movie.genre)
                {
                    s += genre + ", ";
                }
                genre.setText(s.substring(0, s.length()-2));
                Picasso.get().load(movie.url_img).into(img);
                break;
            }
        }

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (date <= calendarView.getDate())
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Дата должна быть больше текущей", Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    Intent intent_alarm = new Intent(context, MovieReceiver.class);
                    intent_alarm.putExtra("movie", name_m);
                    PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent_alarm,0);
                    //alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, pi);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date, pi);
                    Toast toast = Toast.makeText(getApplicationContext(), "Будильник установлен", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day)
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                date = calendar.getTimeInMillis();
            }
        });
    }
}
