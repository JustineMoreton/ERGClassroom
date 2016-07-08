package jcse.app.ergclassroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(this);
        String fileText=getLessonFromJson.readFromFile();
        ArrayList<HashMap<String,String>> weekList= getLessonFromJson.getWeekList(fileText,termId);
        for(int i=0; i<weekList.size(); i++){
            HashMap<String,String> hashMap;
            hashMap=weekList.get(i);
            Button button= new Button(this);
            final int Id =Integer.parseInt(hashMap.get("weekId"));
            button.setText(hashMap.get("weekName"));
            LinearLayout linearLayout=(LinearLayout)findViewById(R.id.content_week);
            Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.addView(button,layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DayActivity.class);
                    intent.putExtra("termId",termId);
                    intent.putExtra("weekId",Id);
                    startActivity(intent);
                }
            });
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}