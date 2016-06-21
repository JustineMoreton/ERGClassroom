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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    HashMap<String, String>[] arrayofHashMaps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        directoryValue = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("directoryValues");

        arrayofHashMaps = (HashMap<String, String>[]) directoryValue.toArray(new HashMap[directoryValue.size()]);

        getURl(arrayofHashMaps);
        //start resource activity, pass arrayList in intent
        finish();
    }

    // When user clicks button, calls AsyncTask.
    // Before attempting to fetch the URL, makes sure that there is a network connection.
    public void getURl(HashMap<String, String>[] arrayofHashMaps) {
        // Gets the URL from the UI's text field.
        //iterate through URLs

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(arrayofHashMaps);
        } else {

        }
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<HashMap<String,String>, Void, String> {


        @Override
        protected String doInBackground(HashMap<String,String>... urls) {
            try {

                return downloadUrl(urls);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
        private String downloadUrl(HashMap<String,String>[] allHashmaps) throws IOException {

            HttpURLConnection connection = null;
            InputStream is = null;
            HashMap<String,String> hashmap;
            int size =allHashmaps.length;
            for(int i =0; i<size; i++) {

                hashmap=allHashmaps[i];
                String termId = hashmap.get("termId");
                String weekId = hashmap.get("weekId");
                String lessonId = hashmap.get("lessonId");
                int numberofSlides = Integer.parseInt(hashmap.get("slideNumber"));
                for (int j=0; j<numberofSlides; j++) {
                    String url= hashmap.get("slideSrcUrl"+(j));
                    String hashSlideName= hashmap.get("slideFileName"+(j));
                    String modDate = hashmap.get("slideSrcDate"+(j));
                    String slidename=termId+"_"+weekId+"_"+lessonId+"_"+hashSlideName;


                    try {
                        URL get_url = new URL(url);
                        connection = (HttpURLConnection) get_url.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.connect();
                        is = new BufferedInputStream(connection.getInputStream());
                        final Bitmap bitmap = BitmapFactory.decodeStream(is);
                        saveImageToInternalStorage(bitmap, getApplicationContext(),slidename,modDate);


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
                }
            }

            return null;

        }

        public boolean saveImageToInternalStorage(Bitmap image, Context context, String imageName, String modDate) {
            Long lastModified=null;
            Long now=null;
            File checkFile = new File(getFilesDir(),imageName);
            if(checkFile.isFile()) {
                lastModified = checkFile.lastModified();
                now = Long.parseLong(modDate);
                if (lastModified != now) {
                    try {
//******Replace file with modifed file***/

                        FileOutputStream fos = context.openFileOutput(imageName, Context.MODE_PRIVATE);
// Use the compress method on the Bitmap object to write image to
// the OutputStream
// Writing the bitmap to the output stream
                        image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();

                        return true;
                    } catch (Exception e) {
                        Log.e("saveToInternalStorage()", e.getMessage());
                        return false;
                    }
                }else{return true;}//file exists and has the same modified date
            }else{
                try {

//******Create new file***/
                    FileOutputStream fos = context.openFileOutput(imageName, Context.MODE_PRIVATE);
// Use the compress method on the Bitmap object to write image to
// the OutputStream
// Writing the bitmap to the output stream
                    image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();

                    return true;
                } catch (Exception e) {
                    Log.e("saveToInternalStorage()", e.getMessage());
                    return false;
                }
            }


        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
         Log.d(DEBUG_TAG, " " +result);
        }

    }


}
