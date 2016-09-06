package jcse.app.ergclassroom;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class TermActivity extends AppCompatActivity {
private final String DEBUG_TAG="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent mIntent=getIntent();
        final int flag=mIntent.getFlags();
        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(this);
        String lessonFile = getLessonFromJson.readFromFile();
        ArrayList<HashMap<String,String>> termList=getLessonFromJson.getTermList(lessonFile);
        for(int i =0; i<termList.size(); i++){
            HashMap<String,String> hashMap;
            hashMap=termList.get(i);
            Button button = new Button(this);
            final int Id = Integer.parseInt(hashMap.get("termId"+(i)));
            button.setText("Term "+hashMap.get("termId"+(i)));
            LinearLayout linearLayout =(LinearLayout) findViewById(R.id.content_profile);
            Toolbar.LayoutParams layoutParams =new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
            linearLayout.addView(button,layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), WeekActivity.class);
                    intent.setFlags(flag);
                    intent.putExtra("termId",Id);
                    startActivity(intent);
                }
            });
        }

        /*try {
            JSONObject jsonObject = new JSONObject(jsonString);
        }catch (JSONException jsonException){
            Log.e("profile html",jsonException.getMessage());
        }*/
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
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream =openFileInput("lessonStructure.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    public Bitmap loadBitmap(Context context, String picName){

        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();

        }
        catch (FileNotFoundException e) {
            Log.d(DEBUG_TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(DEBUG_TAG, "io exception");
            e.printStackTrace();
        }
        return b;
    }
}
