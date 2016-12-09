package jcse.app.ergclassroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        // checks connection first
                        // downloads json file in async task
                        getURl();


                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(HttpActivity.this);
        builder.setMessage("Are you sure you want to dowload the entire lesson plan? This process may take up to an hour and you will not be able to use the tablet.").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

        Log.d("httpActivity","in on create");
      //  finishAfterTransition();
    }
    public void getURl() {
        // Gets the URL from the UI's text field.
        String stringUrl ="\n" +
                "http://www.json-generator.com/api/json/get/bYSHrmtCwi?indent=2";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            Toast.makeText(this, "There is no network connection, please try again later", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();


        }


        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
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
                conn.setConnectTimeout(15000 /* milliseconds */);
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
                if(HttpActivity.this.isDestroyed()){
                    Intent intent = new Intent(getApplicationContext(),ParseJsonObjectFromFile.class);
                    startActivity(intent);

                }
                dismissProgressDialog();
                Intent intent = new Intent(getApplicationContext(),ParseJsonObjectFromFile.class);
                startActivity(intent);

                HttpActivity.this.finish();
            }catch (JSONException jsonException){
                Log.d(DEBUG_TAG, "cant read JSOn object");
                if(HttpActivity.this.isDestroyed()){
                    return;
                }
                dismissProgressDialog();
            }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();


    }
    @Override
    protected void onResume(){
        super.onResume();
//        EventBus.getDefault().register(this);
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
    private void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(HttpActivity.this);
            progDialog.setMessage("Connecting");
            progDialog.setCanceledOnTouchOutside(false);
        }
        progDialog.show();
    }
    private void dismissProgressDialog() {
        if (progDialog != null && progDialog.isShowing()) {
            progDialog.dismiss();
        }
    }




    }


