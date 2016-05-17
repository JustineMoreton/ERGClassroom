package jcse.app.ergclassroom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LessonActivity extends Activity {
    private LinearLayout mainLayout;
    int[] imgid= new int[]{
            R.drawable.activity_1,
            R.drawable.activity_2
    };

    private View imagesscroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_lesson);
        mainLayout = (LinearLayout) findViewById(R.id._linearLayout);
        for (int i = 0; i < imgid.length; i++) {

            imagesscroll = getLayoutInflater().inflate(R.layout.imagescroll, null);

            final ImageView imageView = (ImageView) imagesscroll.findViewById(R.id._image);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // do whatever you want ...
                   // Toast.makeText(TestingActivity.this,
                   //         (CharSequence) imageView.getTag(), Toast.LENGTH_SHORT).show();
                }
            });

            imageView.setTag("Image#"+(i+1));

            //text = (TextView) cell.findViewById(R.id._imageName);

            imageView.setImageResource(imgid[i]);
            //text.setText("Image#"+(i+1));

            mainLayout.addView(imagesscroll);

        }

    }

}
