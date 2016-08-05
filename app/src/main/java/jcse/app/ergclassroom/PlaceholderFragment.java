package jcse.app.ergclassroom;

import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

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
    static String mFileName;
    static String[] mResourcesNames;
    static String[] mResourceFiles;
    static String[] mResourceTypes;
    //Spinner spinner;
    View rootView;
    public PlaceholderFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber,String fileName, String[] resourceNames, String[]resourceFiles, String[]resourceTypes) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        //mFileName=fileName;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("fileName",fileName);
        args.putStringArray("resourceNames",resourceNames);
        args.putStringArray("resourceFiles",resourceFiles);
        args.putStringArray("resourceTypes",resourceTypes);
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

        menuInflater.inflate(R.menu.android_action_bar_spinner_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        NoDefaultSpinner spinner = (NoDefaultSpinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_item,mResourcesNames);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = mResourceTypes[position];
                File file = new File(getActivity().getFilesDir(), mResourceFiles[position]);

                Uri uriPath = Uri.parse(file.toString());
                SlideFragmentDialog slideFragmentDialog = SlideFragmentDialog.newInstance(type,uriPath);
                slideFragmentDialog.show(getFragmentManager(), "fragmentDialog");

                /*final Dialog dialog = new Dialog(getActivity());// add here your class name
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.vdeo_layout);//add your own xml with defied with and height of videoview
                dialog.setCancelable(true);
                dialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                lp.copyFrom(dialog.getWindow().getAttributes());
                dialog.getWindow().setAttributes(lp);
                //String file= getActivity().getFilesDir().getAbsolutePath()+"/"+mResourceFiles[position];
                if (type.equals("video")) {
                    getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
                    Log.v("Vidoe-URI", uriPath + "");
                    final VideoView videoView = (VideoView) dialog.findViewById(R.id.dialogVideoView);
                    videoView.findViewById(R.id.dialogVideoView);
                    videoView.setZOrderOnTop(true);
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setVolume(1.0f, 1.0f);

                        }
                    });
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.reset();
                            mp.release();
                        }
                    });
                    videoView.setVideoURI(uriPath);
                    videoView.seekTo(0);
                    videoView.start();
                } else if (type.equals("audio")) {
                    dialog.setTitle("Audio file: you might need to use headphones");
                    MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), uriPath);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            dialog.cancel();
                            mp.release();

                        }
                    });
                    try {
                        // mediaPlayer.setDataSource(getActivity(), uriPath);
                        mediaPlayer.setVolume(1.0f, 1.0f);
                        // mediaPlayer.prepare();
                    } catch (Exception ioException) {
                        Log.e("Audio_uri", uriPath + "" + ioException);
                    }

                    mediaPlayer.start();
                } else if (type.equals("pdf")) {
                    // create a new renderer
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.dialogImageView);
                    Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_4444);
                    PdfRenderer renderer;
                    try {
                        renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
                        final int pageCount = renderer.getPageCount();
                        for (int i = 0; i < pageCount; i++) {
                            PdfRenderer.Page page = renderer.openPage(i);

                            // say we render for showing on the screen
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                            // do stuff with the bitmap

                            // close the page
                            page.close();
                        }

                        // close the renderer
                        renderer.close();
                    } catch (IOException io) {


                        imageView.setImageBitmap(bitmap);
                        imageView.invalidate();
                    }
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        //spinner = (Spinner) rootView.findViewById(R.id.spinner);
        /*ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.custom_spinner_dropdown_item,mResourcesNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(spinnerAdapter);*/
        ListView listView = (ListView) rootView.findViewById(R.id.section_label);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,mResourcesNames);
        listView.setAdapter(itemsAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        File file = new File(getActivity().getFilesDir(),mFileName);
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