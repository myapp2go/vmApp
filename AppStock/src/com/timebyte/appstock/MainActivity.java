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
		
//		new ReadMailTask(MainActivity.this).execute();


/*		
        Boolean hasSD = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        String sdLoc = "";
        if (hasSD) {
        	sdLoc = Environment.getExternalStorageDirectory().toString() + "/DCIM/SQLite/";
        }
		DatabaseHandler db = new DatabaseHandler(this, sdLoc);
		 
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");
        db.addContact(new Contact("Ravi", "9100000000"));
        db.addContact(new Contact("Srinivas", "9199999999"));
        db.addContact(new Contact("Tommy", "9522222222"));
        db.addContact(new Contact("Karthik", "9533333333"));
 
        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Contact> contacts = db.getAllContacts();       
 
        for (Contact cn : contacts) {
            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
                // Writing Contacts to log
            System.out.println("Name: " + log);
        })
*/      
		System.out.println("NameB: ");
		new PCStock(this).execute();
		System.out.println("NameBA: ");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void stock(DatabaseHandler db) {
		//run on new thread because we cannot do network operation on main thread
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					
				//get the Document object from the site. Enter the link of site you want to fetch
				Document document = Jsoup.connect("http://javalanguageprogramming.blogspot.in/").get();
				
				//Get the title of blog using title tag
				title = document.select("h1.title").text().toString();
				//set the title of text view
				
				
				//Get all the elements with h3 tag and has attribute a[href]
				Elements elements = document.select("div.post-outer").select("h3").select("a[href]");
				int length = elements.size();
				
				for(int i=0; i<length; i++){
					//store each post heading in the string
					posts += elements.get(i).text() + "\n\n";
					
				}
				
				//Run this on ui thread because another thread cannot touch the views of main thread
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						//set both the text views
						titleText.setText(title);
						postText.setText(posts);
					}
				});
				
			}catch(Exception e){
				e.printStackTrace();
			}
			}
		}).start();
		
	}
}
