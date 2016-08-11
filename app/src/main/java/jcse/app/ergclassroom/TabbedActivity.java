package jcse.app.ergclassroom;

import android.content.Intent;
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


    private ViewPager mViewPager;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    static String[] resourceFiles;
    static String[] resourceNames;
    static String[] resourceTypes;
    static String[] resourcePermanence;

    HashMap<String,String> hashMap;
    static String[] slideNameArray;
    String slideName;
    int slideNumber;
    int resourceNumber;
    String resourceFile;
    String resourceName;
    String resourceType;
    String resourcePermanent;
    HashMap<String,String[]> resourceArrays;
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
        resourceArrays=new HashMap<>();

        for(int i=0; i<slideNumber; i++){

            slideName=hashMap.get("slideFileName"+(i));
            slideNameArray[i]=termId+"_"+weekId+"_"+lessonId+"_"+slideName;
            // Get resource Name for clickable listView
            //Get resource File Name for
            resourceNumber=Integer.parseInt(hashMap.get("resourceNumber"+(i)));
            resourceFiles =new String[resourceNumber];
            resourceNames =new String[resourceNumber];
            resourceTypes=new String[resourceNumber];
            resourcePermanence=new String[resourceNumber];
            HashMap<String,String[]> resourceForArray=new HashMap<String, String[]>();
            for(int j =0; j<resourceNumber; j++){
                resourceName=hashMap.get((i)+"resourceScreenName"+(j));
                resourceNames[j]=resourceName;
                resourceFile=hashMap.get((i)+"resourceRefName"+(j));
                resourceFiles[j]=resourceFile;
                resourceType=hashMap.get((i)+"type"+(j));
                resourceTypes[j]=resourceType;
                resourcePermanent=hashMap.get((i)+"resourcePermanent"+(j));
                resourcePermanence[j]=resourcePermanent;
                resourceForArray.put("resourceNames"+(i),resourceNames);
                resourceForArray.put("resourceFiles"+(i),resourceFiles);
                resourceForArray.put("resourceTypes"+(i),resourceTypes);
                resourceForArray.put("resourcePermanent"+(i),resourcePermanence);
            }
            resourceArrays.putAll(resourceForArray);
        }

        //for(int i=0; i<)
          //  map = (HashMap<String, int[]>) getIntent().getSerializableExtra("maparray");
          //  arrayname = map.get("notelist");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),slideNameArray,resourceArrays);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;

            fab.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DayActivity.class);
                    intent.putExtra("termId",termId);
                    intent.putExtra("weekId",weekId);
                    startActivity(intent);
                }
            });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


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




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm, String[] array, HashMap<String,String[]> resourceArrays) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            String sendFileName =slideNameArray[position];
            String[] resourceNames=resourceArrays.get("resourceNames"+(position));
            String[] resourceFiles=resourceArrays.get("resourceFiles"+(position));
            String[] resourceTypes=resourceArrays.get("resourceTypes"+(position));
            String[] resourcePermanence=resourceArrays.get("resourcePermanent"+(position));
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position,sendFileName,resourceNames,resourceFiles,resourceTypes,resourcePermanence);
        }



        @Override
        public int getCount() {
            //return (count)number of pages from the database based on lesson number
            // Show 3 total pages.
            return slideNameArray.length;
        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */



}
