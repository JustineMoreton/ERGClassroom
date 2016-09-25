package jcse.app.ergclassroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class NavigateActivity extends AppCompatActivity {

//    Context mContext=getApplicationContext();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Connection connection = new Connection(this);
        setContentView(R.layout.activity_navigate);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
        final Button button =(Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener(){
                                      @Override
                                      public void onClick(View view){
                                          connection.createConnection();
                                           GetTextFromFile getTextFromFile= new GetTextFromFile(NavigateActivity.this,"timestamps.txt");

                                          String sendString="{\"useractivity\":["+(getTextFromFile.readFromFile())+"]}";
                                          Runnable sendJsonToServer = new SendJsonToServer(sendString,NavigateActivity.this);
                                          new Thread(sendJsonToServer).start();
                                          Intent intent = new Intent(view.getContext(),HttpActivity.class);
                                          startActivity(intent);
                                          button.setText(R.string.synced);
                                      }
                                  }
        );
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
/*        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(this);
        String lessonFile = getLessonFromJson.readFromFile();
        ArrayList<HashMap<String,String>> termList=getLessonFromJson.getTermList(lessonFile);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setMax(100);
        progressBar.setProgress(20);*/
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
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
