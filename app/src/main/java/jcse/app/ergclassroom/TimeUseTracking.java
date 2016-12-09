package jcse.app.ergclassroom;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Justine on 12/7/2016.
 */
public class TimeUseTracking {
    Context mContext;
    public TimeUseTracking(Context context) {
        this.mContext=context;
    }
    public Boolean checkDates(String stringStartDate, String stringEndDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

            try {
                Date startDate= dateFormat.parse(stringStartDate);
                Date endDate=dateFormat.parse(stringEndDate);
                Date date = new Date();


               // String stringDate=nowDate.toString();
               // Date date = dateFormat.parse(stringDate);
                if((date.after(startDate)||date.equals(startDate)) && (date.before(endDate)||date.equals(endDate))){
                    return true;

                }
            }catch (ParseException parseException){
                Log.e("parse exception",parseException.getMessage());
            }
        return false;
        }



}
