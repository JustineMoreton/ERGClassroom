package jcse.app.ergclassroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
    static String mFileName;
    static String[] mResourcesNames;
    static String[] mResourceFiles;
    static String[] mResourceTypes;
    static String[] mResourcePermanence;
    NoDefaultSpinner spinner=null;
    ArrayAdapter<String> spinnerAdapter=null;
    View rootView;
    public static final String USER_PREFS="userPrefs";
    SharedPreferences prefs;
    JSONObject jsonObject;
    public PlaceholderFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        prefs= this.getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber,String fileName, String[] resourceNames, String[]resourceFiles, String[]resourceTypes, String[] resourcePermanence) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        //mFileName=fileName;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("fileName",fileName);
        args.putStringArray("resourceNames",resourceNames);
        args.putStringArray("resourceFiles",resourceFiles);
        args.putStringArray("resourceTypes",resourceTypes);
        args.putStringArray("resourcePermanence",resourcePermanence);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, menuInflater);
        mResourcesNames=getArguments().getStringArray("resourceNames");
        mResourceFiles=getArguments().getStringArray("resourceFiles");
        mResourceTypes=getArguments().getStringArray("resourceTypes");
        mResourcePermanence=getArguments().getStringArray("resourcePermanence");

        menuInflater.inflate(R.menu.android_action_bar_spinner_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (NoDefaultSpinner) MenuItemCompat.getActionView(item);
        //ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_item,mResourcesNames);
        spinnerAdapter= new CustomArrayAdapter(getActivity(),R.layout.custom_spinner_item,mResourcesNames,mResourcePermanence);

        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        SpinnerInteractionListener listener = new SpinnerInteractionListener();
        spinner.setOnTouchListener(listener);
        spinner.setOnItemSelectedListener(listener);




    }
    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (userSelect) {
                String type = mResourceTypes[pos];
                String fileName =mResourceFiles[pos];
                File file = new File(getActivity().getFilesDir(),fileName );

                Uri uriPath = Uri.parse(file.toString());
                Long timeStampLong = System.currentTimeMillis()/1000;
                String timeStamp = timeStampLong.toString();

                String user =prefs.getString("user","no user");
                String userId=prefs.getString("userId","no user Id");
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("User", user);
                    jsonObject.put("Userid",userId);
                    jsonObject.put("FileName",fileName);
                    jsonObject.put("TimeStamp",timeStamp);
                }catch (JSONException jsonException){
                    Log.e("resourse log",jsonException.getMessage());
                }
                String timeStampsJson = jsonObject.toString();
                SaveJsonToFile saveJsonToFile = new SaveJsonToFile();
                saveJsonToFile.appendJsonFile(getActivity(),timeStampsJson,"resourceUse.txt");
                saveJsonToFile.appendExternalJsonFile(getActivity(),timeStampsJson,"resourceUse.txt");

                SlideFragmentDialog slideFragmentDialog = SlideFragmentDialog.newInstance(type,uriPath);


                slideFragmentDialog.show(getFragmentManager(), "fragmentDialog");
                userSelect = false;
                spinner.setAdapter(spinnerAdapter);

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFileName=getArguments().getString("fileName");
        mResourcesNames=getArguments().getStringArray("resourceNames");
       // mResourceFiles=getArguments().getStringArray("resourceFiles");
        rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);

        File file = new File(getActivity().getFilesDir(),mFileName);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;



            ImageView imageView = (ImageView) rootView.findViewById(R.id.tabbedImageView);
            imageView.setImageBitmap(decodeSampledBitmapFromResource(file,R.id.tabbedImageView, 800, 600));

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