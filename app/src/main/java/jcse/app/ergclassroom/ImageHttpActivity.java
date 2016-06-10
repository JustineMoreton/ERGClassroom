package jcse.app.ergclassroom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Justine on 2016/06/07.
 */
public class ImageHttpActivity extends Activity {
    private static final String DEBUG_TAG = "IMAGEHttpActivity";


    ArrayList<HashMap<String, String>> directoryValue = new ArrayList<HashMap<String, String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        directoryValue = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("directoryValues");
        getURl();
        finish();
    }

    // When user clicks button, calls AsyncTask.
    // Before attempting to fetch the URL, makes sure that there is a network connection.
    public void getURl() {
        // Gets the URL from the UI's text field.
        //iterate through URLs
        HashMap<String, String> getLessonMap = directoryValue.get(0);

        String stringUrl = getLessonMap.get("slideSrcUrl0");
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

            HttpURLConnection connection = null;
            InputStream is = null;

            try {
                URL get_url = new URL(myurl);
                connection = (HttpURLConnection) get_url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();
                is = new BufferedInputStream(connection.getInputStream());
                final Bitmap bitmap = BitmapFactory.decodeStream(is);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        // Makes sure that the InputStream is closed after the app is
        // finished using it.





        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {

            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");

            char[] buffer = new char[len];
            reader.read(buffer);

            Log.d(DEBUG_TAG,"stream read");
            return new String(buffer);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(DEBUG_TAG, result);
        }
        public Bitmap loadBitmap(Context context, String picName){
            Bitmap b = null;
            FileInputStream fis;
            try {
                fis = context.openFileInput(picName);
                b = BitmapFactory.decodeStream(fis);
                fis.close();

            }
            catch (FileNotFoundException e) {
                Log.d(DEBUG_TAG, "file not found");
                e.printStackTrace();
            }
            catch (IOException e) {
                Log.d(DEBUG_TAG, "io exception");
                e.printStackTrace();
            }
            return b;
        }
    }


}
