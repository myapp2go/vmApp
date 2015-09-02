package com.pc.vm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ContactActivity extends Activity {
	protected SharedPreferences sharedPreferences;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);

		sharedPreferences = getApplicationContext().getSharedPreferences("VoiceMailPref", MODE_PRIVATE); 
		
		final Button settingButton = (Button) this.findViewById(R.id.setting);
		settingButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				savePreference();
				onBackPressed();
			}
		});
		
    }
    
	private void savePreference() {
		// TODO Auto-generated method stub
		
		Editor editor = sharedPreferences.edit();
		editor.putString("readOPtion", Constants.READ_OPTION_SUBJECT_ONLY);
		editor.putInt("increment", 10);
		editor.commit();

		String FILENAME = "pcVoiceMail";
		String string = "hello  world!";
		String del = "_____";
		
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");
		folder.mkdirs();
         
		FileOutputStream fos;
		try {
			folder.createNewFile();
//			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
	        fos = new FileOutputStream(new File(folder, FILENAME));
			string = ((TextView) findViewById(R.id.contactName1)).getText().toString() + ":" + ((TextView) findViewById(R.id.contactEmail1)).getText().toString() + del;
			fos.write(string.getBytes());
			string = ((TextView) findViewById(R.id.contactName2)).getText().toString() + ":" + ((TextView) findViewById(R.id.contactEmail2)).getText().toString() + del;
			fos.write(string.getBytes());
			string = ((TextView) findViewById(R.id.contactName3)).getText().toString() + ":" + ((TextView) findViewById(R.id.contactEmail3)).getText().toString() + del;
			fos.write(string.getBytes());
			string = ((TextView) findViewById(R.id.contactName4)).getText().toString() + ":" + ((TextView) findViewById(R.id.contactEmail4)).getText().toString() + del;
			fos.write(string.getBytes());
			string = ((TextView) findViewById(R.id.contactName5)).getText().toString() + ":" + ((TextView) findViewById(R.id.contactEmail5)).getText().toString() + del;
			fos.write(string.getBytes());

			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
