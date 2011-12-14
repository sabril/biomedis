package com.sabril.biomedis;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class BiomedisActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        // tab output grafik
        intent = new Intent().setClass(this, GraphActivity.class);
        spec = tabHost.newTabSpec("graphics").setIndicator(null, res.getDrawable(R.drawable.ic_tab_chart)).setContent(intent);
        tabHost.addTab(spec);
        
        // tab settings
        intent = new Intent().setClass(this, SettingsActivity.class);
        spec = tabHost.newTabSpec("settings").setIndicator("Settings", res.getDrawable(R.drawable.ic_tab_settings)).setContent(intent);
        tabHost.addTab(spec);
       
        //tab about
        intent = new Intent().setClass(this, AboutActivity.class);
        spec = tabHost.newTabSpec("about").setIndicator("About", res.getDrawable(R.drawable.ic_tab_about)).setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);
        
    }
}