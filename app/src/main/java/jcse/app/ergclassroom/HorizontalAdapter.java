package jcse.app.ergclassroom;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by Justine on 2016/04/25.
 */
public class HorizontalAdapter extends ArrayAdapter<String> {

    private final Integer[] imgid;


    public HorizontalAdapter(Context context, int resource, String[] objects, Activity context1, Integer[] imgid) {
        super(context, resource, objects);
        context = context1;
        this.imgid = imgid;
    }
}
