package jcse.app.ergclassroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
private final String DEBUG_TAG="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*try {
            File file = new File(getFilesDir(),"bunny.mp4");
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(bitmap);

        }catch (IOException IO){
            //
        }*/
        File file = new File(getFilesDir(),"bunny.mp4");
        VideoView videoView=(VideoView) findViewById(R.id.videoView);
        Uri videoUri = Uri.parse(file.toString());
        Log.d("Video Player", file.toString());
        videoView.setVideoURI(videoUri);
        videoView.seekTo(0);
        videoView.start();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
/*        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
    }
    public Bitmap loadBitmap(Context context, String picName){

        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();

        }
        catch (FileNotFoundException e) {
            Log.d(DEBUG_TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(DEBUG_TAG, "io exception");
            e.printStackTrace();
        }
        return b;
    }
}
