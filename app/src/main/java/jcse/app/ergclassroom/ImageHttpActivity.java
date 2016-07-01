package jcse.app.ergclassroom;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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
public class ImageHttpActivity extends IntentService{
    private static final String DEBUG_TAG = "IMAGEHttpActivity";
    ArrayList<HashMap<String, String>> directoryValue = new ArrayList<HashMap<String, String>>();

    HashMap<String, String>[] arrayofHashMaps;
    public ImageHttpActivity(){
        super("ImageHttpActivity");
    }

    public ImageHttpActivity(String name, ArrayList<HashMap<String, String>> directoryValue) {
        super(name);
        this.directoryValue = directoryValue;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(DEBUG_TAG,"image handle intent");
        Toast.makeText(this, "service IMAGE starting", Toast.LENGTH_SHORT).show();
        directoryValue = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("directoryValues");

        arrayofHashMaps = (HashMap<String, String>[]) directoryValue.toArray(new HashMap[directoryValue.size()]);

        getURl(arrayofHashMaps);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(HttpActivity.ImageResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("imageResponse","sent to Image receiver");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            // Perform the operation associated with our pendingIntent
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

    }




    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
Log.d(DEBUG_TAG,"image on create");

        //start resource activity, pass arrayList in intent
        //finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "service IMAGE FIN", Toast.LENGTH_SHORT).show();
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
            String downloadURlSuccess="no success";
            HttpURLConnection connection = null;
            InputStream is = null;
            HashMap<String,String> hashmap;
            int size =allHashmaps.length;
            for(int i =0; i<size; i++) {

                hashmap=allHashmaps[i];
                String termId = hashmap.get("termId");
                String weekId = hashmap.get("weekId");
                String lessonId = hashmap.get("lessonId");
                int numberofActivities = Integer.parseInt(hashmap.get("activitiesNumber"));
                for (int j=0; j<numberofActivities; j++) {
                    String url= hashmap.get("resourceSrcUrl"+(j));
                    String hashSlideName= hashmap.get("resourceRefName"+(j));
                    String modDate = hashmap.get("resourceSrcDate"+(j));
                    String type=hashmap.get("type"+(j));
                    String slidename=hashSlideName;


                    try {
                        URL get_url = new URL(url);
                        connection = (HttpURLConnection) get_url.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.connect();
                        is = new BufferedInputStream(connection.getInputStream());
                        if(type.equals("image")) {
                            final Bitmap bitmap = BitmapFactory.decodeStream(is);
                            saveImageToInternalStorage(bitmap, getApplicationContext(), slidename, modDate);
                        }
                        else if(type.equals("video")){
                            saveVideoToInternalStorage(is,getApplicationContext(),slidename,modDate);

                        }
                        else if(type.equals("pdf")){
                            saveVideoToInternalStorage(is,getApplicationContext(),slidename,modDate);

                        }
                        else if(type.equals("audio")){
                            saveVideoToInternalStorage(is,getApplicationContext(),slidename,modDate);

                        }


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        connection.disconnect();
                        try {
                            is.close();
                            downloadURlSuccess="url download success";
                        } catch (IOException e) {
                            e.printStackTrace();
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
         Log.d(DEBUG_TAG, " " +result);
        }

    }


}
