package jcse.app.ergclassroom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Justine on 7/29/2016.
 */
public class SlideFragmentDialog extends DialogFragment{
    Context mContext;
    String mType;
    Uri mUriPath;
    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ImageView image;
    private Button btnPrevious;
    private Button btnNext;
    private Button btnClose;
    private File file;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
    }

    public SlideFragmentDialog(){
        mContext = getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        mType= getArguments().getString("type");
        mUriPath= getArguments().getParcelable("uriPath");
        file = new File(mUriPath.getPath());
        file.getAbsolutePath();

        View view = null;
        if (mType.equals("video")||mType.equals("audio")||mType.equals("image")) {
            view = getActivity().getLayoutInflater().inflate(R.layout.vdeo_layout, new RelativeLayout(getActivity()), false);
        }
        if(mType.equals("pdf")){
            view = getActivity().getLayoutInflater().inflate(R.layout.pdf_layout, new LinearLayout(getActivity()),false);
        }
        // Build dialog
        final Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        builder.setContentView(view);

        if (mType.equals("video")) {
            getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
            Log.v("Vidoe-URI", mUriPath + "");
            final VideoView videoView = (VideoView) builder.findViewById(R.id.dialogVideoView);
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
                    builder.cancel();
                    mp.release();
                }
            });
            videoView.setVideoURI(mUriPath);
            videoView.seekTo(0);
            videoView.start();}
        if (mType.equals("pdf")) {

            // create a new renderer
            //ImageView imageView = (ImageView) builder.findViewById(R.id.dialogImageView);
            /*Bitmap bitmap = Bitmap.createBitmap(2480, 3508, Bitmap.Config.ARGB_4444);
            PdfRenderer renderer;
            ParcelFileDescriptor parcelFileDescriptor;*/
           // LinearLayout linearLayout1 = (LinearLayout) builder.findViewById(R.id.linearLayout1);
            /*try{
                openRenderer();}
            catch (IOException io){
                Log.e("open renderer",io.getMessage());
            }*/
             try{

                 fileDescriptor=mContext.getAssets().openFd(file.getName()).getParcelFileDescriptor();

                 pdfRenderer=new PdfRenderer(fileDescriptor);}
            catch (IOException io){
                Log.e("open renderer",io.getMessage());
            }

//                final int pageCount = pdfRenderer.getPageCount();

                    image = (ImageView) view.findViewById(R.id.pdfImage);
                  //  ImageView pdfImageView = new ImageView(getActivity());
                    btnPrevious = (Button) view.findViewById(R.id.buttonPrev);
                    btnNext = (Button) view.findViewById(R.id.buttonNext);
                    btnPrevious.setOnClickListener(onActionListener(-1)); //previous button clicked
                    btnNext.setOnClickListener(onActionListener(1)); //next button clicked

                int index = 0;
                // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
                if (null != savedInstanceState) {
                    index = savedInstanceState.getInt("current_page", 0);
                }
                showPage(index);
                    // close the page





        }
        return builder;
    }
    public static SlideFragmentDialog newInstance(String type, Uri uriPath) {
        SlideFragmentDialog slideFragmentDialog= new SlideFragmentDialog();
        Bundle args = new Bundle();
        args.putString("type",type);
        args.putParcelable("uriPath",uriPath);
        slideFragmentDialog.setArguments(args);
        return slideFragmentDialog;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
/*        try {
            openRenderer(activity);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Fragment", "Error occurred!");
            Log.e("Fragment", e.getMessage());
            activity.finish();
        }*/
    }

    @Override
    public void onDestroy() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != currentPage) {
            outState.putInt("current_page", currentPage.getIndex());
        }
    }

    private void openRenderer() throws IOException {
        // Reading a PDF file from the assets directory.
        fileDescriptor=mContext.getAssets().openFd(file.getName()).getParcelFileDescriptor();

        pdfRenderer=new PdfRenderer(fileDescriptor);
    }

    /**
     * Closes PdfRenderer and related resources.
     */
    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            currentPage.close();
        }
        pdfRenderer.close();
        fileDescriptor.close();
    }

    /**
     * Shows the specified page of PDF file to screen
     * @param index The page index.
     */
    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != currentPage) {
            currentPage.close();
        }
        //open a specific page in PDF file
        currentPage = pdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // showing bitmap to an imageview
        image.setImageBitmap(bitmap);
        updateUIData();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    private void updateUIData() {
        int index = currentPage.getIndex();
        int pageCount = pdfRenderer.getPageCount();
        btnPrevious.setEnabled(0 != index);
        btnNext.setEnabled(index + 1 < pageCount);
        //getActivity().setTitle(getString(R.string.app_name, index + 1, pageCount));
    }

    private View.OnClickListener onActionListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (i < 0) {//go to previous page
                    showPage(currentPage.getIndex() - 1);
                } else {
                    showPage(currentPage.getIndex() + 1);
                }
            }
        };
    }
}

