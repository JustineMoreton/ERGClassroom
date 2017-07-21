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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Justine on 2016/06/07.
 */
public class ImageHttpActivity extends Activity {
    private static final String DEBUG_TAG = "IMAGEHttpActivity";

    ArrayList<HashMap<String,String>> changeDirectoryValues = new ArrayList<>();
    HashMap<String,String>[] arrayofHashMaps;
    ProgressDialog progDialog;
    public static final String USER_PREFS="userPrefs";
    SharedPreferences prefs;
    SaveJsonToFile saveJsonToFile;
    HttpURLConnection connection = null;
    InputStream is = null;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        Log.d(DEBUG_TAG,"image on create");
        prefs=getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        saveJsonToFile= new SaveJsonToFile();

        //directoryValue = (ValueArray) intent.getSerializableExtra("directoryValues");
        changeDirectoryValues=ParseJsonObjectFromFile.directoryValues.getArrayList();
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
    private class DownloadWebpageTask extends AsyncTask<HashMap<String,String>, Integer, String> {

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
        protected void onProgressUpdate(Integer... progress) {
            progDialog.setProgress(progress[0]);
            progDialog.setMessage("Resources downloading..."+progress[0]+"%");
        }
        // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
        private String downloadUrl(HashMap<String,String>[] allHashmaps) throws IOException {
            String downloadURlSuccess="no success";

            HashMap<String,String> hashmap;
            int size =allHashmaps.length;
//change i to loop through slides after looping through hashmaps
            for(int i =0; i<size; i++) {

                    publishProgress((int) ((i / (float) size) * 100));


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


                    saveImageToInternalStorage(getApplicationContext(), slideName, modDate, get_url);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(connection!=null){
                        connection.disconnect();
                    }

                    try {
                        if(is!=null){
                            is.close();
                        }

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

                        if (type.equals("Image")) {
                            saveImageToInternalStorage(getApplicationContext(), hashResName, modResDate,get_url);
                        }
                        if (type.equals("Video")) {
                            saveVideoToInternalStorage(is, getApplicationContext(), hashResName, modResDate,get_url);

                        }
                        if (type.equals("Pdf")) {
                            saveVideoToInternalStorage(is, getApplicationContext(), hashResName, modResDate,get_url);

                        }
                        if (type.equals("Audio")) {
                            saveVideoToInternalStorage(is, getApplicationContext(), hashResName, modResDate,get_url);

                        }


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(connection!=null){
                            connection.disconnect();
                        }
                        try {
                            if(is!=null){
                                is.close();
                            }
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

        public boolean saveImageToInternalStorage(Context context, String imageName, String modDate, URL get_url) {
            Long lastModified;
            Long now;
            String lastModStr="";
            Boolean sameDate=false;
            File checkFile = new File(getFilesDir(),imageName);
            Boolean isAnInternalFile =checkFile.isFile();
            if(isAnInternalFile) {
                String lastModString =getFilesModifiedJSON(imageName);
                //if file exists in system but date is not saved in modifieddate txt file
                if(lastModString.isEmpty() || lastModString.length()<1){
                    lastModified = Long.parseLong(modDate);
                    saveDateToModifiedDateFile(imageName,modDate);
                    //dates will equal on filesmodified.txt and lesson plan. they do not need to download
                }else {
                    lastModified = Long.parseLong(lastModString);
                }
                now = Long.parseLong(modDate);
                sameDate=compareLongDates(now,lastModified);
                if (!sameDate) {
                    try {
//******Replace file with modified file && update modified date to match date on JSON file***/

                        connection = (HttpURLConnection) get_url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        is = new BufferedInputStream(connection.getInputStream());
                        saveDateToModifiedDateFile(imageName,modDate);
                        FileOutputStream fos = context.openFileOutput(imageName, Context.MODE_PRIVATE);
// Use the compress method on the Bitmap object to write image to
// the OutputStream
// Writing the bitmap to the output stream
                        final Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();

                        return true;
                    } catch (Exception e) {
                        Log.e("saveToInternalStorage()", e.getMessage());
                        return false;
                    }
                }else{
                    //file exists and has the same modified date
                    return true;
                }
            }else{
                try {

                    connection = (HttpURLConnection) get_url.openConnection();
                    connection.setDoInput(true);
                    //connection.setDoOutput(true);
                    connection.connect();
                    is = new BufferedInputStream(connection.getInputStream());
//******Create new file***/
                    FileOutputStream fos = context.openFileOutput(imageName, Context.MODE_PRIVATE);
// Use the compress method on the Bitmap object to write image to
// the OutputStream
// Writing the bitmap to the output stream
                    saveDateToModifiedDateFile(imageName,modDate);
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();

                    return true;
                } catch (Exception e) {
                    Log.e("saveToInternalStorage()", e.getMessage());
                    return false;
                }
            }


        }
        public boolean saveVideoToInternalStorage(InputStream is, Context context, String videoName, String modDate,URL get_url) {
            Long lastModified;
            Long now;
            String lastModStr="";
            Boolean sameDate;
            File checkFile = new File(getFilesDir(),videoName);
            Boolean isAnInternalFile =checkFile.isFile();
            if(isAnInternalFile) {
                String lastModString =getFilesModifiedJSON(videoName);
                if(lastModString.isEmpty() || lastModString.length()<1){
                    lastModified = Long.parseLong(modDate);
                    saveDateToModifiedDateFile(videoName,modDate);
                    //date exists on modefile.txt and matched lesson json. The first time an app is updated with the new code
                    //no new files will be updated
                }else{
                    lastModified=Long.parseLong(lastModString);
                }
                now = Long.parseLong(modDate);
                sameDate=compareLongDates(now,lastModified);
                if (!sameDate) {
                    try {
                        connection = (HttpURLConnection) get_url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        is = new BufferedInputStream(connection.getInputStream());
//******Replace file with modifed file***/
                        saveDateToModifiedDateFile(videoName,modDate);
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
                }else{
                    return true;
                }//file exists and has the same modified date
            }else{
                try {
                    connection = (HttpURLConnection) get_url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    is = new BufferedInputStream(connection.getInputStream());
//******Create new file***/
                    saveDateToModifiedDateFile(videoName,modDate);
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
public String getDateString(String time){
    Date date = new Date(Long.parseLong(time));
    return date.toString();
}
    public String getFilesModifiedJSON(String fileName) {
        ReadFromFile readFromFile = new ReadFromFile(getApplicationContext());
        String existingJSON =readFromFile.readFromFile("filesModified.txt");
        String jsonString="";
        if(existingJSON.isEmpty() || existingJSON.length()<1){
           jsonString= ("{\"filesmodified\":[]}");
            saveJsonToFile.appendAndModifyJsonFile(getApplicationContext(),jsonString , "filesModified.txt");
        }else{

            jsonString=existingJSON;
        }
        String modDate="";

        try {
            JSONObject jsonObject= new JSONObject(jsonString);
            JSONArray jsonArray =jsonObject.getJSONArray("filesmodified");
//check
            for(int i=0;i<jsonArray.length();i++){
               // Boolean fileInJson=jsonArray.optJSONObject(i).has(fileName);
               // if(fileInJson)
                //{
                    JSONObject innerJSONObject =jsonArray.getJSONObject(i);
                    Iterator<String> iterator = innerJSONObject.keys();
                    while(iterator.hasNext()) {
                        String currentKey = iterator.next();
                        if(currentKey.equals(fileName)){
                            modDate=innerJSONObject.getString(fileName);
                            return modDate;
                        }
                    }
                    //modDate=innerJSONObject.getString(fileName);

                //}

            }
            Log.d(DEBUG_TAG, jsonObject.toString());

        } catch (Throwable t) {
            Log.e(DEBUG_TAG, "Could not parse malformed JSON: \"" + jsonString + "\"");
        }
        return modDate;
    }
    public Boolean compareLongDates(Long modDate, Long lastmodifiedDate){
        if(modDate.equals(lastmodifiedDate)){
            return true;
        }else{
            return false;
        }

    }
    public void saveDateToModifiedDateFile(String filename, String modDate){
        ReadFromFile readFromFile = new ReadFromFile(getApplicationContext());
        String internalJSON =readFromFile.readFromFile("filesModified.txt");
        Boolean updated =false;
        int i;
        try {
            JSONObject jsonObject = new JSONObject(internalJSON);
            JSONArray jsonArray =jsonObject.getJSONArray("filesmodified");
            JSONObject innerJSONObject;
            int jsonlength=jsonArray.length();
            if(jsonlength<1){
                innerJSONObject=new JSONObject();
                innerJSONObject.put(filename,modDate);
                jsonArray.put(0,innerJSONObject);
            }else {
                for (i = 0; i < jsonArray.length(); i++) {

                   // Boolean fileInJson = jsonArray.optJSONObject(i).has(filename);
                    innerJSONObject =jsonArray.getJSONObject(i);
                    Iterator<String> iterator = innerJSONObject.keys();
                    while(iterator.hasNext()) {
                        String currentKey = iterator.next();
                        if(currentKey.equals(filename)){
                            innerJSONObject.put(filename, modDate);
                            jsonArray.put(i, innerJSONObject);
                            updated = true;
                            break;
                        }
                    }

                }

                if (updated == false) {
                    innerJSONObject = new JSONObject();
                    innerJSONObject.put(filename, modDate);
                    jsonArray.put(innerJSONObject);
                }
            }
            String newJsonString = jsonObject.toString();

            saveJsonToFile.appendAndModifyJsonFile(getApplicationContext(),newJsonString , "filesModified.txt");
        }catch(JSONException jsonException){
            Log.d(DEBUG_TAG,jsonException.getMessage());
        }
        catch(Exception exception){
        Log.d(DEBUG_TAG,exception.getMessage());
    }
    }
}
