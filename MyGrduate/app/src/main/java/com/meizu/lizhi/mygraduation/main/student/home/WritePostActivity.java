package com.meizu.lizhi.mygraduation.main.student.home;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.meizu.lizhi.mygraduation.R;

public class WritePostActivity extends Activity {

    ActionBar mActionBar;

    private View mCustomView;

    private int mActionBarOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        mActionBar =getActionBar();
        mCustomView = LayoutInflater.from(this).inflate(R.layout.write_post_custom_title_view, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActionBar.setDisplayOptions(mActionBarOptions, ActionBar.DISPLAY_SHOW_CUSTOM | mActionBarOptions);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mActionBarOptions = mActionBar.getDisplayOptions();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
    }
}
