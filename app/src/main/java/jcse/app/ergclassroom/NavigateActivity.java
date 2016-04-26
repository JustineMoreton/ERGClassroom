package jcse.app.ergclassroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class NavigateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void goToClassroom(View view){
        Intent intent = new Intent(this, ClassroomActivity.class);
        startActivity(intent);

    }
    public void preparation(View view){
        Intent intent = new Intent(this, PreparationActivity.class);
        startActivity(intent);
    }
}
