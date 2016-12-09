package jcse.app.ergclassroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NavigateActivity extends AppCompatActivity {

//    Context mContext=getApplicationContext();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Connection connection = new Connection(this);
        setContentView(R.layout.activity_navigate);

        LocalBroadcastManager.getInstance(NavigateActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("NOW"));
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
        final Button button =(Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener(){
                                      @Override
                                      public void onClick(View view){
                                          connection.createConnection();

                                          Intent intent = new Intent(view.getContext(),HttpActivity.class);
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
            }
        });

        ProgressBar secondProgressBar = (ProgressBar) findViewById(R.id.progressBarSecond);
        secondProgressBar.setProgress(80);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(50);


    }

    public void goToClassroom(View view){
        Intent intent = new Intent(this, TermActivity.class);
        intent.setFlags(0);
        startActivity(intent);

    }

    public void profile(View view){
        Intent intent = new Intent(this, TermActivity.class);
        intent.setFlags(1);
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
}
