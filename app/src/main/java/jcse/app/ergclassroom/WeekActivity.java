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

public class WeekActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        final int termId =intent.getIntExtra("termId",0);

        if(termId==5){
            getSupportActionBar().setTitle("Resources");
        }else{
            getSupportActionBar().setTitle("Term "+termId);
        }

        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(this);
        String fileText=getLessonFromJson.readFromFile();
        ArrayList<HashMap<String,String>> weekList= getLessonFromJson.getWeekList(fileText,termId);
        for(int i=0; i<weekList.size(); i++){
            HashMap<String,String> hashMap;
            hashMap=weekList.get(i);
            String stringStartDate = hashMap.get("startdate");
            String stringEndDate = hashMap.get("enddate");
            final String stringWeekName =hashMap.get("weekName");
            TimeUseTracking timeUseTracking = new TimeUseTracking(WeekActivity.this);
            final Boolean isWeek=timeUseTracking.checkDates(stringStartDate,stringEndDate);
            Button button= new Button(this);
            final int weekId =Integer.parseInt(hashMap.get("weekId"));
            if(isWeek){
                button.setBackgroundResource(R.color.true_week);
            }
            button.setText(hashMap.get("weekName"));
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
            LinearLayout linearLayout=(LinearLayout)findViewById(R.id.content_week);
            Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.addView(button,layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DayActivity.class);
                    intent.putExtra("weekName",stringWeekName);
                    intent.putExtra("isWeek",isWeek);
                    intent.putExtra("termId",termId);
                    intent.putExtra("weekId",weekId);
                    startActivity(intent);
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TermActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("termId",termId);
                startActivity(intent);
                finish();
            }
        });
        FloatingActionButton fabHome =(FloatingActionButton) findViewById(R.id.fabhome);
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

}
