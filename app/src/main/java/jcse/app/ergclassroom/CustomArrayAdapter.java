package jcse.app.ergclassroom;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Justine on 8/11/2016.
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {
String[] mPermanence;
    public CustomArrayAdapter(Context context, int resource,  String[] objects, String[] permanence) {
        super(context, resource, objects);
        mPermanence=permanence;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        if(mPermanence[position].equals("true")){
            textView.setTextColor(Color.BLACK);
        }
        return textView;
    }

}
