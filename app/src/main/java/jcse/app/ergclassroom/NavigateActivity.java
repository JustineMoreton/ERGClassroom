package jcse.app.ergclassroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class NavigateActivity extends AppCompatActivity {


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
                                          Intent intent = new Intent(view.getContext(),HttpActivity.class);
                                          startActivity(intent);
                                          button.setText(R.string.synced);
                                      }
                                  }
        );
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
    public void profile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    public void tabbed(View view){
        Intent intent = new Intent(this,TabbedActivity.class);
        startActivity(intent);
    }
}
