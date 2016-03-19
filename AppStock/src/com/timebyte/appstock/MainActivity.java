package com.timebyte.appstock;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView titleText, postText;
	String title, posts = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		titleText = (TextView)findViewById(R.id.titleText);
		postText = (TextView)findViewById(R.id.postsText);
		
        Boolean hasSD = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        String sdLoc = "";
        if (hasSD) {
        	sdLoc = Environment.getExternalStorageDirectory().toString() + "/DCIM/SQLite/";
        }
		DatabaseHandler db = new DatabaseHandler(this, sdLoc);
    
		new AndroidStock().execute(db, postText);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
