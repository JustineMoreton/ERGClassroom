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

import java.util.ArrayList;
import java.util.HashMap;

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
                Intent intent = new Intent(view.getContext(), NavigateActivity.class);
                startActivity(intent);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

    }

}
