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
                Long timeInMillis = System.currentTimeMillis();
                Date nowDate = new Date(timeInMillis);
                calendar.setTime(nowDate);
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                Date date = calendar.getTime();
                if((date.after(startDate)||date.equals(startDate)) && (date.before(endDate)||date.equals(endDate))){
                    return true;

                }
            }catch (ParseException parseException){
                Log.e("parse exception",parseException.getMessage());
            }
        return false;
        }
    public Boolean beforeEndDates(String stringEndDate){
        Boolean endDateBool= false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

        try {
            Date endDate=dateFormat.parse(stringEndDate);
            Long timeInMillis = System.currentTimeMillis();
            Date nowDate = new Date(timeInMillis);
            calendar.setTime(nowDate);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            Date date = calendar.getTime();
            if((endDate.before(date)||endDate.equals(date))){
                endDateBool =true;

            }
        }catch (ParseException parseException){
            Log.e("parse exception",parseException.getMessage());
        }
        return endDateBool;
    }


}
