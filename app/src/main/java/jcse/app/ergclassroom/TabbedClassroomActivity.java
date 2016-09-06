package jcse.app.ergclassroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TabbedClassroomActivity extends AppCompatActivity {
    HashMap<String,String> hashMap;
    static String[] slideNameArray;
    String slideName;
    int slideNumber;
    SharedPreferences prefs;
    public static final String USER_PREFS="userPrefs";
    String timeStamp;
    JSONObject jsonObject;
    int flag;
    int termId;
    int weekId;
    int lessonId;

    private ClassSectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */


    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        flag = getIntent().getFlags();
        setContentView(R.layout.activity_tabbed_classroom);
        termId = intent.getIntExtra("termId", 0);
        weekId = intent.getIntExtra("weekId", 0);
        lessonId = intent.getIntExtra("lessonId", 0);

        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(this);
        String textFile = getLessonFromJson.readFromFile();
        ArrayList<HashMap<String, String>> arrayList = getLessonFromJson.getSlidesAndResourcesForLesson(textFile, termId, weekId, lessonId);
        hashMap = arrayList.get(0);
        slideNumber = Integer.parseInt(hashMap.get("slideNumber"));
        slideNameArray = new String[slideNumber];

        for(int i=0; i<slideNumber; i++) {

            slideName = hashMap.get("slideFileName" + (i));
            slideNameArray[i] = termId + "_" + weekId + "_" + lessonId + "_" + slideName;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DayActivity.class);
                intent.setFlags(0);
                intent.putExtra("termId",termId);
                intent.putExtra("weekId",weekId);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mSectionsPagerAdapter = new ClassSectionsPagerAdapter(getSupportFragmentManager(),slideNameArray);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }
    @Override
    protected void onStart() {
        super.onStart();
        Long timeStampLong = System.currentTimeMillis()/1000;
        timeStamp = timeStampLong.toString();
        jsonObject = new JSONObject();

    }
    @Override
    protected void onStop() {
        super.onStop();
        Long timeStampLong = System.currentTimeMillis()/1000;
        String endTimeStamp = timeStampLong.toString();

        prefs=getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String user =prefs.getString("user","no user");
        try {
            jsonObject.put("user",user);
            jsonObject.put("TypeOfActivity",flag);
            jsonObject.put("TermId", termId);
            jsonObject.put("WeekId", weekId);
            jsonObject.put("LessonId", lessonId);
            jsonObject.put("StartTime", timeStamp);
            jsonObject.put("EndTime", endTimeStamp);

        }catch(JSONException jsonEx){
            Log.e("Write Json", jsonEx.getMessage());
        }
        String timeStampsJson = jsonObject.toString();
        SaveJsonToFile saveJsonToFile = new SaveJsonToFile();
        saveJsonToFile.appendJsonFile(getApplicationContext(),timeStampsJson,"timestamps.txt");
        try {
            ReadFromFile readFromFile = new ReadFromFile(getApplicationContext());
            String timstampString =readFromFile.readFromFile("timestamps.txt");
            System.out.print(timstampString);
            System.out.print("");
        }catch (Exception io){
            Log.d("read File", io.getMessage());
        }
    }
    public class ClassSectionsPagerAdapter extends FragmentStatePagerAdapter {

        public ClassSectionsPagerAdapter(FragmentManager fm, String[] array) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            String sendFileName =slideNameArray[position];
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return TabbedClassroomActivityFragment.newInstance(position,sendFileName);
        }



        @Override
        public int getCount() {
            //return (count)number of pages from the database based on lesson number
            // Show 3 total pages.
            return slideNameArray.length;
        }


    }

}
