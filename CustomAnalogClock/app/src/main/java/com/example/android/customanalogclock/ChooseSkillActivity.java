package com.example.android.customanalogclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.customanalogclock.adapter.ImageAdapter;

public class ChooseSkillActivity extends Activity {
    private int[] mImageList = {
            R.drawable.clock_dial,
            R.drawable.clock_dial_5
    };

    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_skill);
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setAdapter(new ImageAdapter(this, mImageList));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendData(mImageList[position]);
            }
        });
    }

    public void sendData(int skill) {
        Intent i = new Intent();
        i.putExtra(MainActivity.SKILL, skill);
        ChooseSkillActivity.this.setResult(Activity.RESULT_OK, i);
        ChooseSkillActivity.this.finish();
    }
}
