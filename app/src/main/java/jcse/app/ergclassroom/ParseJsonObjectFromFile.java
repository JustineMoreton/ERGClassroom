package jcse.app.ergclassroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
public class ParseJsonObjectFromFile extends Activity {
    public ParseJsonObjectFromFile() {
    }

    private static final String DEBUG_TAG = "ParseJsonObject";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String getFileText = readFromFile();
        ArrayList<HashMap<String,String>> directoryValues= createJsonObject((getFileText));
        Intent intent = new Intent(this,ImageHttpActivity.class);
        intent.putExtra("directoryValues", directoryValues);
        startActivity(intent);
        finish();
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
        HashMap<String,String> directoryValues = new HashMap<String,String>();
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
                        if(weeks!= null){
                            for(int w = 0; w<weeks.length(); w++){
                                JSONObject week = weeks.getJSONObject(w);
                                if(week != null){
                                    String weekId = week.getString("weekId");
                                    JSONArray lessons=week.getJSONArray("lessons");
                                    if(lessons != null){
                                        for(int l=0; l<lessons.length();l++){
                                            JSONObject lesson=lessons.getJSONObject(l);
                                            if(lesson != null){
                                                directoryValues.put("termid",termid);
                                                directoryValues.put("weekId",weekId);
                                                String lessonId =lesson.getString("lessonId");
                                                directoryValues.put("lessonid",lessonId);
                                                JSONArray slides = lesson.getJSONArray("slides");
                                                if(slides != null){
                                                    int slideNumber=slides.length();
                                                    directoryValues.put("slideNumber",""+(slideNumber));
                                                    for(int s =0; s<slides.length(); s++){
                                                        JSONObject slide=slides.getJSONObject(s);
                                                        if(slide != null){
                                                            String slideId=slide.getString("slideId");
                                                            String slideSrcUrl=slide.getString("slideSrcUrl");
                                                            directoryValues.put("slideSrcUrl"+ (s),slideSrcUrl);
                                                            String slideSrcDate=slide.getString("slideSrcDate");
                                                            String slideFileName=slide.getString("slideFileName");
                                                            directoryValues.put("slideFileName"+(s),slideFileName);
                                                            JSONArray resources = slide.getJSONArray("resources");
                                                            if(resources != null){
                                                                for(int r =0; r<resources.length(); r++){
                                                                    JSONObject oneResource=resources.getJSONObject(r);
                                                                    if(oneResource != null){
                                                                        String type = oneResource.getString("type");
                                                                        String resourceSrcUrl = oneResource.getString("resourceSrcUrl");
                                                                        String resourceSrcDate = oneResource.getString("resourceSrcDate");
                                                                        String resourceRefName = oneResource.getString("resourceRefName");

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
                                }

                            }
                        }
                    }
                }

            }

        }catch (JSONException jsonException){
            Log.d(DEBUG_TAG, "Json Exception");
        }
        return arrayList;
    }
}
