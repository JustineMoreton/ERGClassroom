package jcse.app.ergclassroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    ValueArray directoryValue = new ValueArray();
    ArrayList<HashMap<String,String>> changeDirectoryValues = new ArrayList<>();
    HashMap<String,String>[] arrayofHashMaps;
    ProgressDialog progDialog;
    public static final String USER_PREFS="userPrefs";
    SharedPreferences prefs;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        Log.d(DEBUG_TAG,"image on create");
        prefs=getSharedPreferences(USER_PREFS, MODE_PRIVATE);
         Intent intent = getIntent();

        directoryValue = (ValueArray) intent.getSerializableExtra("directoryValues");
        changeDirectoryValues=directoryValue.getArrayList();
        arrayofHashMaps = (HashMap<String, String>[]) changeDirectoryValues.toArray(new HashMap[changeDirectoryValues.size()]);

        getURl(arrayofHashMaps);

    }
    public void startJsonService() {
        startService(new Intent(getBaseContext(), ParseJsonObjectFromFile.class));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        finish();
    }
    private void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(ImageHttpActivity.this);
            progDialog.setMessage("Resources downloading...");
            progDialog.setCanceledOnTouchOutside(false);
        }
        progDialog.show();
    }
    private void dismissProgressDialog() {
        if (progDialog != null && progDialog.isShowing()) {
            progDialog.dismiss();
        }
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

        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }


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
            String downloadURlSuccess="no success";
            HttpURLConnection connection = null;
            InputStream is = null;
            HashMap<String,String> hashmap;
            int size =allHashmaps.length;
//change i to loop through slides after looping through hashmaps
            for(int i =0; i<size; i++) {

                hashmap = allHashmaps[i];
                String slideNumber = hashmap.get("slideNumber");
                for(int k=0; k<Integer.parseInt(slideNumber); k++){
                String termId = hashmap.get("termId");
                String weekId = hashmap.get("weekId");
                String lessonId = hashmap.get("lessonId");
                String slideUrl = hashmap.get("slideSrcUrl" + (k));
                String hashSlideName = hashmap.get("slideFileName" + (k));
                String modDate = hashmap.get("slideSrcDate" + (k));
                String slideName = termId + "_" + weekId + "_" + lessonId + "_" + hashSlideName;
                String newSlideUrl=slideUrl.replaceAll(" ","%20");
                try {

                    URL get_url = new URL(newSlideUrl);
                    connection = (HttpURLConnection) get_url.openConnection();
                    connection.setDoInput(true);
                    // connection.setDoOutput(true);
                    connection.connect();
                    is = new BufferedInputStream(connection.getInputStream());

                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    saveImageToInternalStorage(bitmap, getApplicationContext(), slideName, modDate);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                    try {
                        is.close();
                        downloadURlSuccess = "slide download success";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                int resourceNumber = Integer.parseInt(hashmap.get("resourceNumber"+(k)));
                for (int j = 0; j < resourceNumber; j++) {
                    String url = hashmap.get((k) + "resourceSrcUrl" + (j));
                    String hashResName = hashmap.get((k) + "resourceRefName" + (j));
                    String modResDate = hashmap.get((k) + "resourceSrcDate" + (j));
                    String type = hashmap.get((k) + "type" + (j));
                    String newUrl=url.replaceAll(" ","%20");

                    try {
                        URL get_url = new URL(newUrl);
                        connection = (HttpURLConnection) get_url.openConnection();
                        connection.setDoInput(true);
                        //connection.setDoOutput(true);
                        connection.connect();
                        is = new BufferedInputStream(connection.getInputStream());
                        if (type.equals("Image")) {
                            final Bitmap bitmap = BitmapFactory.decodeStream(is);
                            saveImageToInternalStorage(bitmap, getApplicationContext(), hashResName, modResDate);
                        }
                        if (type.equals("Video")) {
                            saveVideoToInternalStorage(is, getApplicationContext(), hashResName, modResDate);

                        }
                        if (type.equals("Pdf")) {
                            saveVideoToInternalStorage(is, getApplicationContext(), hashResName, modResDate);

                        }
                        if (type.equals("Audio")) {
                            saveVideoToInternalStorage(is, getApplicationContext(), hashResName, modResDate);

                        }


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        connection.disconnect();
                        try {
                            is.close();
                            downloadURlSuccess = "url download success";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            }

            return downloadURlSuccess;

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
        public boolean saveVideoToInternalStorage(InputStream is, Context context, String videoName, String modDate) {
            Long lastModified=null;
            Long now=null;

            File checkFile = new File(getFilesDir(),videoName);
            if(checkFile.isFile()) {
                lastModified = checkFile.lastModified();
                now = Long.parseLong(modDate);
                if (lastModified != now) {
                    try {
//******Replace file with modifed file***/

                        FileOutputStream fos = context.openFileOutput(videoName, Context.MODE_WORLD_READABLE);
// Use the compress method on the Bitmap object to write image to
// the OutputStream
// Writing the bitmap to the output stream

                        byte[] buffer = new byte[1024];
                        int len1 = 0;
                        while ((len1 = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);
                        }
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
                    FileOutputStream fos = context.openFileOutput(videoName, Context.MODE_WORLD_READABLE);
                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);
                    }
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
            prefs=getSharedPreferences(USER_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first_login",true);
            editor.putBoolean("first_synced",true);
            editor.apply();
            if (ImageHttpActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                dismissProgressDialog();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
            dismissProgressDialog();
            Log.e(DEBUG_TAG,result);
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


}
