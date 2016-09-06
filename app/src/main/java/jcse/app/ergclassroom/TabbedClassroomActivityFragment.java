package jcse.app.ergclassroom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * A placeholder fragment containing a simple view.
 */
public class TabbedClassroomActivityFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    static String mFileName;


    View rootView;
    public TabbedClassroomActivityFragment() {
    }
    public static TabbedClassroomActivityFragment newInstance(int sectionNumber,String fileName) {
        TabbedClassroomActivityFragment fragment = new TabbedClassroomActivityFragment();
        //mFileName=fileName;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("fileName",fileName);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFileName=getArguments().getString("fileName");
        rootView = inflater.inflate(R.layout.fragment_tabbed_classroom, container, false);

        File file = new File(getActivity().getFilesDir(),mFileName);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            ImageView imageView = (ImageView) rootView.findViewById(R.id.tabbedImageView);
            imageView.setImageBitmap(decodeSampledBitmapFromResource(file,R.id.tabbedImageView, 1280, 800));


        return rootView;
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromResource(File file, int resId,
                                                         int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //BitmapFactory.decodeResource(res, resId, options);
        Bitmap bitmap = null;
        try {
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            //return BitmapFactory.decodeResource(res, resId, options);
           bitmap = BitmapFactory.decodeStream(new FileInputStream(file),null,options);
        }catch (FileNotFoundException fileNotFound){
            Log.e("Classroom fragment", fileNotFound.getMessage());
        }
        return bitmap;
    }
}
