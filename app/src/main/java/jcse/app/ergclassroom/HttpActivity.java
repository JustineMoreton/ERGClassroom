package jcse.app.ergclassroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Justine on 2016/06/06.
 */
public class HttpActivity extends Activity {
    private static final String DEBUG_TAG = "HttpActivity";
    ProgressDialog progDialog;
    public static final String USER_PREFS="userPrefs";
    SharedPreferences prefs;
    Intent in;
    int fromActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        in = getIntent();
        fromActivity=in.getIntExtra("fromActivity",0);
        getURl();


        Log.d("httpActivity","in on create");
      //  finishAfterTransition();
    }

    private void showProgressDialog() {

        if (progDialog == null) {
            progDialog = new ProgressDialog(HttpActivity.this);
            progDialog.setMessage("Connecting to server");
            progDialog.setCanceledOnTouchOutside(false);
        }
        progDialog.show();
    }
    private void dismissProgressDialog() {
        if (progDialog != null && progDialog.isShowing()) {
            progDialog.dismiss();
        }
    }
    public void getURl() {
        // Gets the URL from the UI's text field.
        String stringUrl ="\n" +
        //       "http://egr2.jcse-himat.com/Slides/SyncResults?username=Egrs2JSON&password=egrTablet";
       "http://webtest1.jcse.org.za/webjson.html";
        //"http://egr.tshimologong.net/Slides/SyncResults?username=Egrs2JSON&password=egrTablet";
        new DownloadWebpageTask(HttpActivity.this).execute(stringUrl);

    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
HttpActivity mHttpActivity;
        public DownloadWebpageTask(HttpActivity httpActivity){
            mHttpActivity=httpActivity;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }


        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                mHttpActivity.finish();
                return "Unable to retrieve web page. URL may be invalid.";

            }catch (Exception e){
                mHttpActivity.finish();
                return "Connect to server"+e.getMessage();
            }
        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(DEBUG_TAG, result);
            try {
                JSONObject object = new JSONObject(result);

                JSONArray userArray = object.getJSONArray("users");
                String usersString ="{\"users\":"+userArray.toString()+"}";
                JSONArray lessonArray =object.getJSONArray("lessonStructure");
                String lessonString ="{\"lessonStructure\":"+lessonArray.toString()+"}";

                SaveJsonToFile saveJsonToFile = new SaveJsonToFile();
                saveJsonToFile.createJsonFile(getApplicationContext(),lessonString,"lessonStructure.txt");
                saveJsonToFile.createJsonFile(getApplicationContext(),usersString,"users.txt");

                ReadFromFile readFromFile = new ReadFromFile(getApplicationContext());
                //********File to track which files have been downloaded*************
                String existingJSON =readFromFile.readFromFile("filesModified.txt");

                if(existingJSON.isEmpty() || existingJSON.length()<1) {
                    String jsonString = ("{\"filesmodified\":[]}");

                    saveJsonToFile.appendAndModifyJsonFile(getApplicationContext(), jsonString, "filesModified.txt");
                }
                //****************Track which lessons the teacher have been in******
                String checkUserActivity =readFromFile.readFromFile("trackUsersLesson.txt");
                if(checkUserActivity.isEmpty() ||checkUserActivity.length()<1){
                    String username=prefs.getString("user",null);
                    String trackUserJson =("{\""+username+"\":[]}");
                    saveJsonToFile.appendAndModifyJsonFile(getApplicationContext(),trackUserJson,"trackUsersLesson.txt");
                }
                if(HttpActivity.this.isDestroyed()){
                    Intent intent = new Intent(getApplicationContext(),ParseJsonObjectFromFile.class);
                    startActivity(intent);
                }
                dismissProgressDialog();
                Intent intent = new Intent(getApplicationContext(),ParseJsonObjectFromFile.class);
                startActivity(intent);

                HttpActivity.this.finish();
            }catch (JSONException jsonException){
                Log.d(DEBUG_TAG, "cant read JSON object");
                sendIntent();
                if(mHttpActivity.isDestroyed()){
                    return;
                }
                dismissProgressDialog();
            }catch (Exception exception){
                Log.d(DEBUG_TAG,exception.getMessage());
                sendIntent();
                if(mHttpActivity.isDestroyed()){
                    return;
                }
                dismissProgressDialog();
            }
        }
public void sendIntent(){
    Intent in =null;
    if(fromActivity==1){
        in = new Intent(mHttpActivity, MainActivity.class);
    }if(fromActivity==2){
        in = new Intent(mHttpActivity, NavigateActivity.class);
    }
    in.setFlags(in.FLAG_ACTIVITY_SINGLE_TOP);
    in.putExtra("NONE",0);
    in.setAction("SERVERCONNECT");
    LocalBroadcastManager.getInstance(mHttpActivity).sendBroadcast(in);
    Log.d(DEBUG_TAG, "The response is: " + 0);
}
    }
    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(20000 /* milliseconds */);
            conn.setConnectTimeout(20000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        String json="";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                //String newline=line.trim();
                sb.append(line);
            }
            stream.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return json;
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onResume(){
        super.onResume();
      /*if(allowDownload){
          showProgressDialog();
      }*/
    }
    @Override
    protected void onStart(){
        super.onStart();

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        dismissProgressDialog();
        HttpActivity.this.finish();

    }





    }


