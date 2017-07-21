package jcse.app.ergclassroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class DayActivity extends AppCompatActivity {
    public static final String USER_PREFS="userPrefs";
    SharedPreferences prefs;
    SaveJsonToFile saveJsonToFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();

        final int termId =intent.getIntExtra("termId",0);
        final int weekId= intent.getIntExtra("weekId",0);
        final String weekName=intent.getStringExtra("weekName");
        if(termId==5){
            getSupportActionBar().setTitle("Resources - "+weekName);
        }else{
            getSupportActionBar().setTitle("Term "+termId+" - "+ weekName);
        }
        final Boolean isWeek=intent.getBooleanExtra("isWeek",false);
        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(this);
        String fileText=getLessonFromJson.readFromFile();
        ArrayList<HashMap<String,String>> dayList =getLessonFromJson.getLessonsForDay(fileText,termId,weekId);
        for(int j =0; j<dayList.size(); j++){
            final HashMap<String,String> hashMap;
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
                        if(isWeek){
                            if (dayOfWeek(hashMap.get("lessonName"))){
                                setTrackerTrue(hashMap.get("lessonName"),weekName,hashMap.size());
                            }
                        }
                        Intent intent = new Intent(v.getContext(), TabbedActivity.class);
                        intent.putExtra("termId",termId);
                        intent.putExtra("weekId",weekId);
                        intent.putExtra("lessonId",lessonId);
                        intent.putExtra("weekName",weekName);
                        intent.putExtra("isWeek",isWeek);
                        intent.putExtra("isDayOfWeek",dayOfWeek(hashMap.get("lessonName")));
                        startActivity(intent);

                }
            });

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WeekActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("termId",termId);
                intent.putExtra("weekId",weekId);
                intent.putExtra("isWeek",isWeek);
                startActivity(intent);
                finish();

            }
        });
        FloatingActionButton fabHome =(FloatingActionButton) findViewById(R.id.fabhomeDay);
        fabHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(view.getContext(), NavigateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
    public void setTrackerTrue(String lessonName, String weekName, int lessonLength){
        prefs=getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String userName=prefs.getString("user",null);
        ReadFromFile readFromFile = new ReadFromFile(getApplicationContext());
        String checkUserActivity =readFromFile.readFromFile("trackUsersLesson.txt");
        String newUserActivity=checkUserActivity;
        saveJsonToFile=new SaveJsonToFile();
        //If no users exist
        if(checkUserActivity.isEmpty() ||checkUserActivity.length()<1){
            String username=prefs.getString("user",null);
            try {
                JSONObject dayObject = new JSONObject();
                dayObject.put(lessonName,1);
                JSONObject weekLength = new JSONObject();
                weekLength.put(weekName,dayObject);
                JSONObject userObject = new JSONObject();
                userObject.put(username,weekLength);
                String userString = userObject.toString();
                saveJsonToFile.appendAndModifyJsonFile(getApplicationContext(),userString,"trackUsersLesson.txt");
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            JSONObject jsonObject= new JSONObject(newUserActivity);
            JSONObject findObject =jsonObject.optJSONObject(userName);
            //If the user did not exist, add user with week and lesson
            if(findObject==null){
                JSONObject dayObject = new JSONObject();
                dayObject.put(lessonName,1);
                JSONObject weekLength = new JSONObject();
                weekLength.put(weekName,dayObject);
                JSONObject userObject = new JSONObject();
                userObject.put(userName,weekLength);
                String userString = userObject.toString();
                saveJsonToFile.appendAndModifyJsonFile(getApplicationContext(),userString,"trackUsersLesson.txt");
                return;
            }
            //find user exists

            if (findObject.length()>0) {
                Iterator<String> iterator =findObject.keys();
                while(iterator.hasNext()) {
                    String currentKey = iterator.next();
                    if(currentKey.equals(weekName)){
                        JSONObject lessonObject =findObject.getJSONObject(weekName);
                        Iterator<String> lessonIterator =lessonObject.keys();
                        while(lessonIterator.hasNext()){
                            String lessonKey =lessonIterator.next();
                            if(lessonKey.equals(lessonName)){
                                return;
                            }else {
                                lessonObject.put(lessonName,1);
                                String userString = jsonObject.toString();
                                saveJsonToFile.appendAndModifyJsonFile(getApplicationContext(),userString,"trackUsersLesson.txt");
                                return;
                            }
                        }
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
