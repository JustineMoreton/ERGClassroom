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
    String isUser;
    @Override
    protected void onResume() {
        super.onResume();
       // LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("NOW"));

    }
    @Override
            protected void onPause(){
        super.onPause();
      //  unregisterReceiver(broadcastReceiver);
    }

    boolean synced;
    String userId;
    final Connection connection = new Connection(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs=getSharedPreferences(USER_PREFS, MODE_PRIVATE);
       // LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("NOW"));
        if(prefs.contains("first_login")){
            isUser=prefs.getString("user",null);

            if(isUser!=null && !isUser.equals("no user")){
                Intent intent=new Intent(this,NavigateActivity.class);
                startActivity(intent);
                return;
            }
        }else{
            userFirstLogin= prefs.getBoolean("first_login",true);
        }



            synced = prefs.getBoolean("first_synced", false);

        SharedPreferences.Editor editor = prefs.edit();

        if(userFirstLogin!=false){
            editor.putBoolean("first_login",true);
            editor.apply();
        setContentView(R.layout.activity_main_first_sync);
        }else{
            /*String packageName = this.getPackageName();
            ComponentName componentName = new ComponentName(packageName,packageName+".AliasActivity");
            this.getPackageManager().setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,0);
            */
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
                Toast.makeText(this, "You did not enter a username", Toast.LENGTH_LONG).show();
                return;
            }

            if(isEmpty(pass)){
                Toast.makeText(this, "You did not enter a password", Toast.LENGTH_LONG).show();
                return;
            }
            editor.putString("user",getEditText(name));
            editor.putString("password",getEditText(pass));
            editor.putInt(getEditText(name),0);
            editor.apply();

            trueUser =compareUser(getEditText(name),getEditText(pass));

        }
        if(synced==false && userFirstLogin==true){
            trueUser =compareUser(getEditText(name),getEditText(pass));
        }
        if(synced==true && userFirstLogin==false){
            trueUser =compareUser(getEditText(name),getEditText(pass));
        }
        if(trueUser==true) {
            editor.putString("user",getEditText(name));
            editor.putString("password",getEditText(pass));
            editor.putString("userId",userId);
            editor.apply();
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
                       userId = userObject.getString("userId");
                        passMatch=true;
                        i=userArray.length();
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
/*    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String response = intent.getStringExtra("TYPE");  //get the response of message from MyGcmListenerService 1 - lock or 0 -Unlock
            int response = intent.getIntExtra("TYPE",0);
            if (response == 201) // 1 == lock
            {
                Toast.makeText(getApplication(),"User info successfully sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplication(), "There was a problem send the user info, please try again later", Toast.LENGTH_LONG).show();
            }
        }
    };*/


}
