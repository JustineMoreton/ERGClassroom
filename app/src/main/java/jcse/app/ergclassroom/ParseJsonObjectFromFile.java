package jcse.app.ergclassroom;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Justine on 2016/06/09.
 */
public class ParseJsonObjectFromFile extends IntentService{
    private static final String DEBUG_TAG = "ParseJsonObject";
    int mStartMode;
    String getFileText;
    ArrayList<HashMap<String,String>> directoryValues;

    public ParseJsonObjectFromFile() {
        super("ParseJsonObjectFromFile");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Toast.makeText(this, "service JSON starting", Toast.LENGTH_SHORT).show();
        getFileText= readFromFile();
        directoryValues= createJsonObject((getFileText));
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(HttpActivity.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("directoryValues",directoryValues);
        broadcastIntent.putExtra("finishedParse",true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            // Perform the operation associated with our pendingIntent
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        HttpActivity.ResponseReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(DEBUG_TAG, "Json ON HANDLE INTENT");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "JSON object Parsed", Toast.LENGTH_LONG).show();
    }

    private String readFromFile() {

            String ret = "";

            try {
                InputStream inputStream =openFileInput("lessonStructure.txt");

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString;
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();

                }
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }

            return ret;
        }
    public ArrayList createJsonObject(String fileText){
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String,String>>();


        HashMap<String,String> mapLesson = new HashMap<String,String>();
        HashMap<String,String> mapSlide = new HashMap<String,String>();
        HashMap<String,String> mapResources = new HashMap<String,String>();
        try {
            JSONObject jsonObject = new JSONObject(fileText);

            JSONArray lessonStructure=jsonObject.getJSONArray("lessonStructure");

            if(lessonStructure != null) {

                for (int i = 0; i < lessonStructure.length(); i++) {
                    JSONObject term = lessonStructure.getJSONObject(i);
                    if(term != null){
                        String termid = term.getString("termId");
                        String startDate = term.getString("startDate");
                        String endDate = term.getString("endDate");
                        JSONArray weeks =term.getJSONArray("weeks");
                        HashMap<String,String> mapTerm = new HashMap<String,String>();
                        mapTerm.put("termId",termid);
                        if(weeks.length()>0){
                            for(int w = 0; w<weeks.length(); w++){
                                JSONObject week = weeks.getJSONObject(w);
                                if(week != null){
                                    String weekId = week.getString("weekId");
                                    String weekName=week.getString("weekName");
                                    HashMap<String,String> mapWeek = new HashMap<String,String>(mapTerm);
                                    mapTerm.putAll(mapWeek);
                                    mapWeek.put("weekId",weekId);
                                    mapWeek.put("weekName",weekName);
                                    JSONArray lessons=week.getJSONArray("lessons");
                                    if(lessons.length()>0){
                                        for(int l=0; l<lessons.length();l++){
                                            HashMap<String,String> directoryValues = new HashMap<String,String>();
                                            directoryValues.putAll(mapWeek);
                                            JSONObject lesson=lessons.getJSONObject(l);
                                            if(lesson != null){

                                                String lessonId =lesson.getString("lessonId");
                                                String lessonName=lesson.getString("lessonName");
                                                directoryValues.put("lessonName",lessonName);
                                                directoryValues.put("lessonId",lessonId);
                                                JSONArray slides = lesson.getJSONArray("slides");
                                                if(slides.length()>0){
                                                    int slideNumber=slides.length();
                                                    directoryValues.put("slideNumber",""+(slideNumber));
                                                    for(int s =0; s<slides.length(); s++){
                                                        JSONObject slide=slides.getJSONObject(s);
                                                        if(slide != null){
                                                            String slideId=slide.getString("slideId");
                                                            String slideSrcUrl=slide.getString("slideSrcUrl");
                                                            directoryValues.put("slideSrcUrl"+ (s),slideSrcUrl);
                                                            String slideSrcDate=slide.getString("slideSrcDate");
                                                            directoryValues.put("slideSrcDate"+(s),slideSrcDate);
                                                            String slideFileName=slide.getString("slideFileName");
                                                            directoryValues.put("slideFileName"+(s),slideFileName);
                                                            JSONArray resources = slide.getJSONArray("resources");
                                                            if(resources.length()>0){
                                                                int resourceNumber=resources.length();
                                                                directoryValues.put("resourceNumber"+(s),""+(resourceNumber));
                                                                for(int r =0; r<resources.length(); r++){
                                                                    JSONObject oneResource=resources.getJSONObject(r);
                                                                    if(oneResource != null){
                                                                        String type = oneResource.getString("type");
                                                                        String resourceId = oneResource.getString("resourceId");
                                                                        String resourceScreenName = oneResource.getString("resourceScreenName");
                                                                        String resourceSrcUrl = oneResource.getString("resourceSrcUrl");
                                                                        String resourceSrcDate = oneResource.getString("resourceSrcDate");
                                                                        String resourceRefName = oneResource.getString("resourceRefName");
                                                                        String resourcePermanent = oneResource.getString("resourcePermanent");
                                                                        directoryValues.put((s)+"type"+(r),type);
                                                                        directoryValues.put((s)+"resourceScreenName"+(r),resourceScreenName);
                                                                        directoryValues.put((s)+"resourceSrcUrl"+(r),resourceSrcUrl);
                                                                        directoryValues.put((s)+"resourceRefName"+(r),resourceRefName);
                                                                        directoryValues.put((s)+"resourceSrcDate"+(r),resourceSrcDate);
                                                                        directoryValues.put((s)+"resourcePermanent"+(r),resourcePermanent);
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    }

                                                }
                                            }

                                            arrayList.add(directoryValues);
                                        }


                                    }
                                    mapWeek.clear();
                                }

                            }
                        }
                        mapTerm.clear();
                    }
                }

            }

        }catch (JSONException jsonException){
            Log.d(DEBUG_TAG, jsonException.getMessage());
        } catch (Exception e) {
            Log.d("Json parse", e.getLocalizedMessage());
        }
        return arrayList;
    }
}
