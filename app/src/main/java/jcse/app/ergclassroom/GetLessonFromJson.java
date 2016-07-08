package jcse.app.ergclassroom;

import android.content.Context;
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
 * Created by Justine on 2016/07/06.
 */
public class GetLessonFromJson{
    Context mcontext;
    static final String DEBUG_TAG="";

    public GetLessonFromJson(Context context) {
        mcontext = context;
    }


    public String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream =mcontext.openFileInput("lessonStructure.txt");

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
    public ArrayList getTermList(String fileText){

        ArrayList arrayList = new ArrayList<HashMap<String,String>>();


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
                        HashMap<String,String> mapTerm = new HashMap<String,String>();
                        mapTerm.put("termId"+(i),termid);
                        mapTerm.put("startDate"+(i),startDate);
                        mapTerm.put("endDate"+(i),endDate);
                        arrayList.add(mapTerm);


                    }
                }

            }

        }catch (JSONException jsonException){
            Log.d(DEBUG_TAG, jsonException.getMessage());
        }
        return arrayList;
    }
    public ArrayList getWeekList(String fileText, int mTermId){
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String,String>>();

        try {
            JSONObject jsonObject = new JSONObject(fileText);

            JSONArray lessonStructure=jsonObject.getJSONArray("lessonStructure");

            if(lessonStructure != null) {

                for (int i = 0; i < lessonStructure.length(); i++) {
                    JSONObject term = lessonStructure.getJSONObject(i);
                    if(term != null){
                        String termid = term.getString("termId");
                        if(Integer.parseInt(termid)==mTermId) {
                            JSONArray weeks = term.getJSONArray("weeks");
                            HashMap<String, String> mapTerm = new HashMap<String, String>();
                            mapTerm.put("termId", termid);

                            if (weeks != null) {
                                for (int w = 0; w < weeks.length(); w++) {
                                    JSONObject week = weeks.getJSONObject(w);
                                    if (week != null) {
                                        String weekId = week.getString("weekId");
                                        String weekName = week.getString("weekName");
                                        HashMap<String, String> mapWeek = new HashMap<String, String>(mapTerm);
                                        mapTerm.putAll(mapWeek);
                                        mapWeek.put("weekId", weekId);
                                        mapWeek.put("weekName", weekName);
                                        arrayList.add(mapWeek);

                                    }

                                }
                            }

                        }
                    }
                }


            }

        }catch (JSONException jsonException){
            Log.d(DEBUG_TAG, jsonException.getMessage());
        }
        return arrayList;
    }
    public ArrayList getLessonActivities(String fileText, int mTermId, int mWeekId){
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
                        if(Integer.parseInt(termid)==mTermId){
                        JSONArray weeks =term.getJSONArray("weeks");
                        HashMap<String,String> mapTerm = new HashMap<String,String>();
                        mapTerm.put("termId",termid);
                            if(weeks!= null) {

                                for (int w = 0; w < weeks.length(); w++) {
                                    JSONObject week = weeks.getJSONObject(w);
                                    if (week != null) {
                                        String weekId = week.getString("weekId");
                                        if(Integer.parseInt(weekId)==mWeekId){
                                            String weekName = week.getString("weekName");
                                            HashMap<String, String> mapWeek = new HashMap<String, String>(mapTerm);
                                            mapTerm.putAll(mapWeek);
                                            mapWeek.put("weekId", weekId);
                                            mapWeek.put("weekName", weekName);
                                            JSONArray lessons = week.getJSONArray("lessons");
                                            if (lessons != null) {
                                                for (int l = 0; l < lessons.length(); l++) {
                                                    HashMap<String, String> directoryValues = new HashMap<String, String>();
                                                    directoryValues.putAll(mapWeek);
                                                    JSONObject lesson = lessons.getJSONObject(l);
                                                    if (lesson != null) {

                                                        String lessonId = lesson.getString("lessonId");
                                                        String lessonName = lesson.getString("lessonName");
                                                        directoryValues.put("lessonName", lessonName);
                                                        directoryValues.put("lessonId", lessonId);

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
                }

            }

        }catch (JSONException jsonException){
            Log.d(DEBUG_TAG, jsonException.getMessage());
        }
        return arrayList;
    }
}
