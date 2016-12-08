package jcse.app.ergclassroom;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Justine on 12/7/2016.
 */
public class ProgressBarSetup {
    Context mContext;
    public ProgressBarSetup(Context context) {
        this.mContext=context;
    }
    public void checkTerms(){
        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(mContext);
        String lessonFile = getLessonFromJson.readFromFile();
        ArrayList<HashMap<String,String>> termList = getLessonFromJson.getTermList(lessonFile);
        for(int i =0; i<termList.size(); i++) {
            HashMap<String, String> hashMap;
            hashMap = termList.get(i);
            Date startDate = new Date();
            Date endDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.UK);
            try {
                String stringStartDate = hashMap.get("startDate" + (i));
                String stringEndDate = hashMap.get("endDate" + (i));
                startDate= dateFormat.parse(stringStartDate);
                endDate=dateFormat.parse(stringEndDate);
            }catch (ParseException parseException){
                Log.e("parse exception",parseException.getMessage());
            }
            Date date = new Date();
            date.getTime();

            if(date.after(startDate) && date.before(endDate)){
                //int progress =getResources().getInteger(R.integer.primary_progress);

            }
        }
    }
}
