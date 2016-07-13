package jcse.app.ergclassroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Justine on 7/13/2016.
 */
public class PlaceholderFragment extends Fragment {
    Context mContext;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    static String[] mArrayname;

    public PlaceholderFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, String[] arrayname) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        mArrayname=arrayname;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putStringArray("arrayname",arrayname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        File file = new File(getActivity().getFilesDir(),mArrayname[(getArguments().getInt(ARG_SECTION_NUMBER)) - 1]);
        try{
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));



            ImageView imageView = (ImageView) rootView.findViewById(R.id.tabbedImageView);
            imageView.setImageBitmap(bitmap);
        }catch (IOException IO){
            Log.e("PlaceHolderFragmeant",IO.getMessage());
        }
            /*try {
            File file = new File(getFilesDir(),arrayname[(getArguments().getInt(ARG_SECTION_NUMBER)) - 1]);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(bitmap);

        }catch (IOException IO){
            //
        }*/
        return rootView;
    }
}