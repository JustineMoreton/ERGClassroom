package jcse.app.ergclassroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.login_button);
        button.setOnClickListener(this);

    }

    public void loggedIn(View view){
        Intent intent = new Intent(this, NavigateActivity.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
    ReadFromFile readFromFile = new ReadFromFile(getApplicationContext());
        String userString =readFromFile.readFromFile("user.txt");
    }


}
