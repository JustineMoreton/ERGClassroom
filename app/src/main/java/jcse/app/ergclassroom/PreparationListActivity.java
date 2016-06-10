package jcse.app.ergclassroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;

public class PreparationListActivity extends AppCompatActivity {
    String[] items = new String[]{
            "Management notes",
            "Overview",
            "Core methodology",
    };
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
    int[] imgid3 = new int[]{
            R.drawable.core_methodology
    };
    HashMap<String,int[]> map =new HashMap<String, int[]>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparation_list);
        ListView listView = (ListView) findViewById(R.id.listView2);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        listView.setAdapter(itemsAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                        {
                                            @Override
                                            public void onItemClick(AdapterView<?> arg0, View view,
                                                                    int position, long id) {
                                                Intent intent = new Intent(view.getContext(), PreparationItemsActivity.class);
                                                intent.putExtra("position", position);
                                                intent.addFlags(1);
                                                switch(position){
                                                    case 0: map.put("notelist",imgid1);
                                                        break;
                                                    case 1: map.put("notelist",imgid2);
                                                        break;
                                                    case 2: map.put("notelist",imgid3);
                                                        break;
                                                    default: break;
                                                }
                                                intent.putExtra("maparray",map);
                                                startActivity(intent);
                                            }
                                        }
        );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NavigateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
