package com.app2go.app2go;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class SettingActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        
        /* TabHost will have Tabs */
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        
        /* TabSpec used to create a new tab. 
         * By using TabSpec only we can able to setContent to the tab.
         * By using TabSpec setIndicator() we can set name to tab. */
        
        /* tid1 is firstTabSpec Id. Its used to access outside. */
        TabSpec quotesTabSpec = tabHost.newTabSpec("quotes");
//        TabSpec contactTabSpec = tabHost.newTabSpec("contact");
//        TabSpec commandTabSpec = tabHost.newTabSpec("command");
        
        /* TabSpec setIndicator() is used to set name for the tab. */
        /* TabSpec setContent() is used to set content for a particular tab. */
        quotesTabSpec.setIndicator("Quotes").setContent(new Intent(this, SetQuotesActivity.class));
//        contactTabSpec.setIndicator("Contact").setContent(new Intent(this, ContactActivity.class));
//        commandTabSpec.setIndicator("Command").setContent(new Intent(this, CommandActivity.class));
        
        /* Add tabSpec to the TabHost to display. */
        tabHost.addTab(quotesTabSpec);
//       tabHost.addTab(contactTabSpec);
//        tabHost.addTab(commandTabSpec);
    }
}
