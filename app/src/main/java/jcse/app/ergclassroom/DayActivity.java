package jcse.app.ergclassroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        final int flag= intent.getFlags();
        final int termId =intent.getIntExtra("termId",0);
        final int weekId= intent.getIntExtra("weekId",0);
        final Boolean isWeek=intent.getBooleanExtra("isWeek",false);
        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(this);
        String fileText=getLessonFromJson.readFromFile();
        ArrayList<HashMap<String,String>> dayList =getLessonFromJson.getLessonsForDay(fileText,termId,weekId);
        for(int j =0; j<dayList.size(); j++){
            HashMap<String,String> hashMap;
            hashMap=dayList.get(j);
            Button button= new Button(this);
            final int lessonId =Integer.parseInt(hashMap.get("lessonId"));
            button.setText(hashMap.get("lessonName"));
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
            if(isWeek) {
                if (dayOfWeek(hashMap.get("lessonName"))) {
                    button.setBackgroundResource(R.color.true_week);
                }
            }
            LinearLayout linearLayout=(LinearLayout)findViewById(R.id.content_day);
            Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.addView(button,layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag==1){
                        Intent intent = new Intent(v.getContext(), TabbedActivity.class);
                        intent.setFlags(flag);
                        intent.putExtra("termId",termId);
                        intent.putExtra("weekId",weekId);
                        intent.putExtra("lessonId",lessonId);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(v.getContext(), TabbedClassroomActivity.class);
                        intent.setFlags(flag);
                        intent.putExtra("termId",termId);
                        intent.putExtra("weekId",weekId);
                        intent.putExtra("lessonId",lessonId);
                        startActivity(intent);
                    }


                }
            });

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WeekActivity.class);
                intent.setFlags(1);
                intent.putExtra("termId",termId);
                intent.putExtra("weekId",weekId);
                startActivity(intent);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
    public Boolean dayOfWeek(String lessonName){
        Boolean isDay = false;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

            Calendar calendar = Calendar.getInstance();
            String weekDay = dayFormat.format(calendar.getTime());

        if (weekDay.equalsIgnoreCase(lessonName)){
            isDay=true;
        }
        return isDay;
    }
}
