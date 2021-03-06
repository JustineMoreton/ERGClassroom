package jcse.app.ergclassroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class NavigateActivity extends AppCompatActivity {

    //    Context mContext=getApplicationContext();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigate);

        LocalBroadcastManager.getInstance(NavigateActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("NOW"));
        LocalBroadcastManager.getInstance(NavigateActivity.this).registerReceiver(resourceReceiver, new IntentFilter("RESNOW"));
        LocalBroadcastManager.getInstance(NavigateActivity.this).registerReceiver(connectionReceiver, new IntentFilter("SERVERCONNECT"));
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
        final Button button =(Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener(){
                                      @Override
                                      public void onClick(View view){

                                          Intent intent = new Intent(view.getContext(),ConnectActivity.class);
                                          intent.putExtra("fromActivity",2);
                                          startActivity(intent);
                                      }
                                  }
        );
        Button statsButton =(Button) findViewById(R.id.button2);
        statsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                     GetTextFromFile getTextFromFile= new GetTextFromFile(NavigateActivity.this,"timestamps.txt");
                     String sendString="{\"useractivity\":["+(getTextFromFile.readFromFile())+"]}";
                     Runnable sendJsonToServer = new SendJsonToServer(sendString,NavigateActivity.this);
                     new Thread(sendJsonToServer).start();

                     GetTextFromFile getResTextFromFile= new GetTextFromFile(NavigateActivity.this,"resourceUse.txt");
                     String sendResString="{\"resourceTracking\":["+(getResTextFromFile.readFromFile())+"]}";
                     Runnable sendResJsonToServer = new SendResJsonToServer(sendResString,NavigateActivity.this);
                     new Thread(sendResJsonToServer).start();
            }
        });
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
            TimeUseTracking timeUseTracking = new TimeUseTracking(NavigateActivity.this);
            Boolean isTerm=timeUseTracking.checkDates(stringStartDate,stringEndDate);
            if(isTerm){
                ArrayList<HashMap<String,String>> weekList=getLessonFromJson.getWeekList(lessonFile,Id);

                for(int w=0; w<weekList.size(); w++) {
                    weekSize=weekList.size();
                    HashMap<String, String> wHashMap;
                    wHashMap = weekList.get(w);
                    String stringWEndDate = wHashMap.get("enddate");
                    if(timeUseTracking.beforeEndDates(stringWEndDate)){
                        countFinWeeks =countFinWeeks +1;
                        //continue;
                    }else{
                        break;
                    }
                }
            }
        }

        ProgressBar secondProgressBar = (ProgressBar) findViewById(R.id.progressBarSecond);
        secondProgressBar.setMax(weekSize);
        secondProgressBar.setProgress(countFinWeeks);

        SeekBar seekBar = (SeekBar)findViewById(R.id.progressBar);
        seekBar.setMax(weekSize);
        seekBar.setProgress(countFinWeeks);
        seekBar.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
       // ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
       // progressBar.setProgress(50);

    /*Button progButton = (Button) findViewById(R.id.button);
        progButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),UserTrackingActivity.class);
                startActivity(intent);
            }
        });*/

    }

    public void goToClassroom(View view){
        Intent intent = new Intent(this, TermActivity.class);
        intent.setFlags(0);
        startActivity(intent);

    }
    public void progress(View view){
        Intent intent = new Intent(this,UserTrackingActivity.class);
        startActivity(intent);
    }

    public void profile(View view){
        Intent intent = new Intent(this, TermActivity.class);
        //intent.setFlags(1);
        startActivity(intent);
    }
    public void logout (View view){
        SharedPreferences prefs = getSharedPreferences("userPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("user");
        editor.remove("pass");
        editor.remove("userId");
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String response = intent.getStringExtra("TYPE");  //get the response of message from MyGcmListenerService 1 - lock or 0 -Unlock
            int response = intent.getIntExtra("TYPE",0);
            if (response == 201) // 1 == lock
            {
                Toast.makeText(getApplication(),"User info successfully sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplication(), "There was a problem sending the user info, please try again later", Toast.LENGTH_LONG).show();
            }
        }
    };

    private BroadcastReceiver resourceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String response = intent.getStringExtra("TYPE");  //get the response of message from MyGcmListenerService 1 - lock or 0 -Unlock
            int response = intent.getIntExtra("RESTYPE",0);
            if (response == 201) // 1 == lock
            {
                Toast.makeText(getApplication(),"Resource info successfully sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplication(), "There was a problem sending the resource info, please try again later", Toast.LENGTH_LONG).show();
            }
        }
    };
    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String response = intent.getStringExtra("TYPE");  //get the response of message from MyGcmListenerService 1 - lock or 0 -Unlock
            int response = intent.getIntExtra("NONE",0);
            if (response == 0) // 0 == no server connection
            {
                Toast.makeText(getApplication(),"Unable to connect to server, please try again later", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
