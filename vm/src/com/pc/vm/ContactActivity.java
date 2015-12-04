package com.pc.vm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

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
		
		final Button cancelButton = (Button) this.findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
    }
    
	private void savePreference() {	
		Editor editor = sharedPreferences.edit();
		editor.putString("readOPtion", Constants.READ_OPTION_SUBJECT_ONLY);
		editor.putInt("increment", 10);
		
		String FILENAME = "pcMailContacts";
		
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");
		folder.mkdirs();
         
		FileOutputStream fos;
		try {
			folder.createNewFile();
//			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
	        fos = new FileOutputStream(new File(folder, FILENAME));

	        setContact(fos, editor, R.id.contactName1, R.id.contactEmail1);
	        setContact(fos, editor, R.id.contactName2, R.id.contactEmail2);
	        setContact(fos, editor, R.id.contactName3, R.id.contactEmail3);
	        setContact(fos, editor, R.id.contactName4, R.id.contactEmail4);
	        setContact(fos, editor, R.id.contactName5, R.id.contactEmail5);
	        setContact(fos, editor, R.id.contactName6, R.id.contactEmail6);
	        
			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		editor.commit();		
	}

	private void setContact(FileOutputStream fos, Editor editor, int nameId, int contactId) throws IOException {	
		String name = ((TextView) findViewById(nameId)).getText().toString();
        String contact = ((TextView) findViewById(contactId)).getText().toString();
        if (name != null && contact != null) {
        	fos.write((name+":"+contact+Constants.CONTACT_MARKER).getBytes());
        	editor.putString((Constants.CONTACT_MARKER+name), contact);
        }
	}
}
