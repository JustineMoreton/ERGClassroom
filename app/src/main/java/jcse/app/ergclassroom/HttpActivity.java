package jcse.app.ergclassroom;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Justine on 2016/06/06.
 */
public class HttpActivity extends Activity {
    private static final String DEBUG_TAG = "HttpActivity";
    private ResponseReceiver receiver;
    private ImageResponseReceiver imageResponseReceiver;
    ArrayList<HashMap<String,String>> directoryValues;
    String imagessent;
    Boolean parseStatus=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getURl();
        //Intent intent = new Intent(getApplicationContext(),ParseJsonObjectFromFile.class);
        //startService(intent);

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        startJsonService();



        IntentFilter intentFilter = new IntentFilter(ImageResponseReceiver.ACTION_RESP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        imageResponseReceiver = new ImageResponseReceiver();
        registerReceiver(imageResponseReceiver, intentFilter);
        Intent sendIntent =getIntent();
        sendIntent.putExtra("imagesent",imagessent);
        setResult(Activity.RESULT_OK,sendIntent);
         finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.receiver);
        unregisterReceiver(this.imageResponseReceiver);

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

        unregisterReceiver(this.receiver);
        unregisterReceiver(this.imageResponseReceiver);
    }

    public void startJsonService() {
        startService(new Intent(getBaseContext(), ParseJsonObjectFromFile.class));

    }
    public void startImageService(){
        Intent intent =new Intent(this,ImageHttpActivity.class);
        intent.putExtra("directoryValues",directoryValues);
        getApplicationContext().startService(intent);

    }

    // When user clicks button, calls AsyncTask.
    // Before attempting to fetch the URL, makes sure that there is a network connection.
    public void getURl() {
        // Gets the URL from the UI's text field.
        String stringUrl ="http://www.json-generator.com/api/json/get/bUVALBJJLm?indent=2";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {

        }
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {


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
                conn.setReadTimeout(10000 /* milliseconds */);
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

            }catch (JSONException jsonException){
                Log.d(DEBUG_TAG, "cant read JSOn object");
            }
        }

    }

    public class ResponseReceiver extends WakefulBroadcastReceiver{

        public static final String ACTION_RESP =
                "jcse.app.ergclassroom.BroadCastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            directoryValues=
                    (ArrayList<HashMap<String, String>>)intent.getSerializableExtra("directoryValues");
            parseStatus=intent.getBooleanExtra("finishedParse",false);
            Intent service = new Intent(context,ImageHttpActivity.class);
            service.putExtra("directoryValues",directoryValues);
            if(parseStatus==true){
              startWakefulService(context,service);
            }


        }
    }
    public class ImageResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP =
                "jcse.app.ergclassroom.ImageBroadCastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            imagessent=intent.getStringExtra("imageResponse");

        }
    }

    }


