package jcse.app.ergclassroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{
    public static final String DEBUG_TAG="mainActivity";
    public static final String USER_PREFS="userPrefs";

    SharedPreferences prefs;
    boolean userFirstLogin;
    boolean synced;
    final Connection connection = new Connection(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs=getSharedPreferences(USER_PREFS, MODE_PRIVATE);

        if(prefs.contains("first_login")){

        }else{userFirstLogin= prefs.getBoolean("first_login",true);}



            synced = prefs.getBoolean("first_synced", false);

        SharedPreferences.Editor editor = prefs.edit();

        if(userFirstLogin!=false){
            editor.putBoolean("first_login",true);
            editor.apply();
        setContentView(R.layout.activity_main_first_sync);
        }else{
            setContentView(R.layout.activity_main);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Log.d(DEBUG_TAG,"inActivityResult");

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first_synced",true);
            editor.apply();
            setContentView(R.layout.activity_main);
        }

    }
    public void loggedIn(View view){

        Boolean trueUser=false;
        EditText name=(EditText) findViewById(R.id.editText);

        EditText pass=(EditText) findViewById(R.id.editText2);
        SharedPreferences.Editor editor =prefs.edit();

        if(synced==true && userFirstLogin==true){
            if(isEmpty(name)){
                Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
                return;
            }

            if(isEmpty(pass)){
                Toast.makeText(this, "You did not enter a password", Toast.LENGTH_SHORT).show();
                return;
            }
            editor.putString("user",getEditText(name));
            editor.putString("password",getEditText(pass));
            editor.apply();

            trueUser =compareUser(getEditText(name),getEditText(pass));

        }
        if(synced==true && userFirstLogin==false){

            trueUser =compareUser(getEditText(name),getEditText(pass));
        }
        if(trueUser==true) {
            Intent intent = new Intent(this, NavigateActivity.class);
            startActivityForResult(intent, 1, null);
        }
        if(trueUser==false){
            Toast.makeText(this, "Either your user name or password is wrong", Toast.LENGTH_SHORT).show();
            return;
        }

    }
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    private String getEditText(EditText edText){
        String ret="";
        ret=edText.getText().toString().trim();
        return ret;
    }
    private Boolean compareUser(String user, String pass){
        ReadFromFile readFromFile = new ReadFromFile(getApplicationContext());
        String userString =readFromFile.readFromFile("users.txt");
        Boolean foundUser=false;
        Boolean passMatch=false;
        Boolean returnBool=false;
        try {
            JSONObject object = new JSONObject(userString.toString());
            JSONArray userArray=object.getJSONArray("users");

            for(int i=0; i<userArray.length(); i++){
                JSONObject userObject = userArray.getJSONObject(i);
                if(userObject.getString("userName").equals(user)){
                    foundUser=true;
                    if(userObject.getString("password").equals(pass)){
                        passMatch=true;
                        continue;
                    }
                }

            }
        }catch (JSONException e){
            Log.d(DEBUG_TAG,e.getMessage());
            return null;
        }
        if(passMatch==true && foundUser==true){
            returnBool=true;
        }

        return returnBool;

    }
    public void firstSync(View view){
        Button button=(Button) findViewById(R.id.sync_button);
        connection.createConnection();
        Intent intent = new Intent(view.getContext(),HttpActivity.class);
        startActivityForResult(intent,0,null);
        button.setText(R.string.synced);
    }



}
