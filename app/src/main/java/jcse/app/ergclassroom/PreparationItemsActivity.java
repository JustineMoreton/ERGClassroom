package jcse.app.ergclassroom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PreparationItemsActivity extends Activity {

    private LinearLayout mainLayout;
    int[] imgid1= new int[]{
            R.drawable.management_note_001,
            R.drawable.management_note_002
    };
    int[] imgid2= new int[]{
            R.drawable.overview_001,
            R.drawable.overview_002,
            R.drawable.overview_003,
            R.drawable.overview_004,
            R.drawable.overview_005,
            R.drawable.overview_006
    };

    private View imagesscroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparation_items);
        String notes = getIntent().getExtras().getString("notelist");
        mainLayout = (LinearLayout) findViewById(R.id._linearLayoutprep);
        for (int i = 0; i < imgid1.length; i++) {

            imagesscroll = getLayoutInflater().inflate(R.layout.imagescrollprep, null);

            final ImageView imageView = (ImageView) imagesscroll.findViewById(R.id._imageprep);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // do whatever you want ...
                    // Toast.makeText(TestingActivity.this,
                    //         (CharSequence) imageView.getTag(), Toast.LENGTH_SHORT).show();
                }
            });

            imageView.setTag("Image#" + (i + 1));

            //text = (TextView) cell.findViewById(R.id._imageName);

            imageView.setImageResource(imgid1[i]);
            //text.setText("Image#"+(i+1));

            mainLayout.addView(imagesscroll);
        }
    }

}
