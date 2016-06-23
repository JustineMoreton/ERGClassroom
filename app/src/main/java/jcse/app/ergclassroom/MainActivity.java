package jcse.app.ergclassroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{
    public static final String USER_PREFS="userPrefs";
    final Connection connection = new Connection(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        boolean userFirstLogin= prefs.getBoolean("first_login", true);
        if(userFirstLogin==true){
        setContentView(R.layout.activity_main_first_sync);
        }

        setContentView(R.layout.activity_main);

    }

    public void loggedIn(View view){
       ReadFromFile readFromFile = new ReadFromFile(getApplicationContext());
       String userString =readFromFile.readFromFile("users.txt");
        Intent intent = new Intent(this, NavigateActivity.class);
        startActivityForResult(intent,1,null);

    }
    public void firstSync(View view){
        Button button=(Button) findViewById(R.id.sync_button);
        connection.createConnection();
        Intent intent = new Intent(view.getContext(),HttpActivity.class);
        startActivity(intent);
        button.setText(R.string.synced);
    }



}
