package jcse.app.ergclassroom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;

public class TabbedActivity extends AppCompatActivity {

    HashMap<String,int[]> map =new HashMap<String, int[]>();
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    int[] prepitems =new int[]{
            R.drawable.management_note_001,
            R.drawable.management_note_002
    };

    private ViewPager mViewPager;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    static int[] arrayname;
    HashMap<String,String> hashMap;
    static String[] slideNameArray;
    String slideName;
    int slideNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tabbed);
        Intent intent=getIntent();
        final int termId =intent.getIntExtra("termId",0);
        final int weekId= intent.getIntExtra("weekId",0);
        final int lessonId= intent.getIntExtra("lessonId",0);
        int flag = getIntent().getFlags();
        GetLessonFromJson getLessonFromJson = new GetLessonFromJson(this);
        String textFile=getLessonFromJson.readFromFile();
        ArrayList<HashMap<String,String>> arrayList=getLessonFromJson.getSlidesAndResourcesForLesson(textFile,termId,weekId,lessonId);
            hashMap=arrayList.get(0);
            slideNumber=Integer.parseInt(hashMap.get("slideNumber"));
            slideNameArray = new String[slideNumber];

        for(int i=0; i<arrayList.size(); i++){

            hashMap=arrayList.get(i);
            slideName=hashMap.get("slideFileName"+(i));
            slideNameArray[i]=termId+"_"+weekId+"_"+lessonId+"_"+slideName;

        }
          //  map = (HashMap<String, int[]>) getIntent().getSerializableExtra("maparray");
          //  arrayname = map.get("notelist");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final Bundle lessonArgs = new Bundle();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        if(flag==0) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), FinishLessonActivity.class);

                    startActivity(intent, lessonArgs);
                }
            });
        }
        if(flag==1) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PreparationListActivity.class);
                    map.put("notelist",prepitems);
                    intent.putExtra("maparray",map);
                    startActivity(intent, lessonArgs);
                }
            });
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Tabbed Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://jcse.app.ergclassroom/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Tabbed Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://jcse.app.ergclassroom/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1,slideNameArray);
        }


        @Override
        public int getCount() {
            //return (count)number of pages from the database based on lesson number
            // Show 3 total pages.
            return slideNameArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */



}
