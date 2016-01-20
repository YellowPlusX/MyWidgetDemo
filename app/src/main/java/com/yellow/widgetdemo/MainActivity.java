package com.yellow.widgetdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String TITLE = "title";
    private static final String SUMMARY = "summary";
    private static final String[] mFrom = {TITLE, SUMMARY};
    private static final int[] mTo = {R.id.title, R.id.summary};

    private Map mActionMap;
    private ListView mListView;
    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionMap = getActionMap();
        mListView = (ListView) findViewById(R.id.my_listview);
        mMyAdapter = new MyAdapter(this, getData(), R.layout.listview_itme_layout, mFrom, mTo);
        mListView.setAdapter(mMyAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent((String) mActionMap.get(position));
                startActivity(intent);
            }
        });
    }


    private class MyAdapter extends SimpleAdapter {
        public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
    }

    private List<Map<String, String>> getData() {
        List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

        String title[] = getResources().getStringArray(R.array.widgetTitle);
        String summary[] = getResources().getStringArray(R.array.widgetSummary);

        int count = summary.length < title.length ? summary.length : title.length;

        for (int i = 0; i < count; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(TITLE, title[i]);
            map.put(SUMMARY, summary[i]);
            listData.add(map);
        }
        return listData;
    }

    private Map getActionMap() {
        Map<Integer, String> actionMap = new HashMap<Integer, String>();
        String aciton[] = getResources().getStringArray(R.array.action);
        for (int i = 0, count = aciton.length; i < count; i++) {
            actionMap.put(i, aciton[i]);
        }
        return actionMap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
