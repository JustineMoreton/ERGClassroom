package jcse.app.ergclassroom;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class UserTrackingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tracking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       //isTerm?, get all lessons, add checkboxes for each lesson
        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(this);
        String lessonFile = getLessonFromJson.readFromFile();
        ArrayList<HashMap<String,String>> termList=getLessonFromJson.getTermList(lessonFile);
        int countFinWeeks=0;
        int weekSize =0;
        for(int i =0; i<termList.size(); i++){
            HashMap<String,String> hashMap;
            hashMap=termList.get(i);
            final int Id = Integer.parseInt(hashMap.get("termId"+(i)));
            String stringStartDate = hashMap.get("startDate" + (i));
            String stringEndDate = hashMap.get("endDate" + (i));
            TimeUseTracking timeUseTracking = new TimeUseTracking(UserTrackingActivity.this);
            Boolean isTerm=timeUseTracking.checkDates(stringStartDate,stringEndDate);

                ArrayList<HashMap<String,String>> weekList=getLessonFromJson.getWeekList(lessonFile,Id);

                for(int w=0; w<weekList.size(); w++) {
                    weekSize=weekList.size();
                    HashMap<String, String> wHashMap;
                    wHashMap = weekList.get(w);
                    int weekId=Integer.parseInt(wHashMap.get("weekId"));
                    ArrayList<HashMap<String,String>> dayList =getLessonFromJson.getLessonsForDay(lessonFile,Id,weekId);
                    for(int d=0; d<dayList.size(); d++){
                        CheckBox checkBox = new CheckBox(UserTrackingActivity.this);
                        checkBox.setText("checkbox ");
                        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.content_user_tracking);
                        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.gravity= Gravity.RIGHT;
                        linearLayout.addView(checkBox,layoutParams);
                    }


                }

        }

    }

}
