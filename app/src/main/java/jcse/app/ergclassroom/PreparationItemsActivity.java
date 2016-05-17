package jcse.app.ergclassroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;

public class PreparationItemsActivity extends Activity {

    private LinearLayout mainLayout;

    HashMap<String,int[]> map;
    public int[] getImgid() {
        Intent intent =getIntent();
        map=(HashMap<String,int[]>)intent.getSerializableExtra("maparray");
        int[] arrayname = map.get("notelist");
        return arrayname;
    }

    private View imagesscroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparation_items);

        mainLayout = (LinearLayout) findViewById(R.id._linearLayoutprep);
        for (int i = 0; i < getImgid().length; i++) {

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

            imageView.setImageResource(getImgid()[i]);
            //text.setText("Image#"+(i+1));

            mainLayout.addView(imagesscroll);
        }
    }

}
