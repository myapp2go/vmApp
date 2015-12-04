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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class AccountActivity extends Activity {

	protected SharedPreferences sharedPreferences;
	private String bodyDoneFlag = "T";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

		sharedPreferences = getApplicationContext().getSharedPreferences("VoiceMailPref", MODE_PRIVATE); 
		((TextView)findViewById(R.id.myEmail)).setText(sharedPreferences.getString("myEmail", ""));
		((TextView)findViewById(R.id.myPassword)).setText(sharedPreferences.getString("myPassword", ""));		
        
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
		
		final CheckBox chkBody = (CheckBox) findViewById(R.id.chkBody);
		chkBody.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// is chkIos checked?
				if (((CheckBox) v).isChecked()) {
					bodyDoneFlag = "F";
				} else {
					bodyDoneFlag = "T";
				}
			}
		});
    }
    
	private void savePreference() {
		// TODO Auto-generated method stub
		String myEmail = ((TextView) findViewById(R.id.myEmail)).getText().toString();
		String myPassword = ((TextView) findViewById(R.id.myPassword)).getText().toString();
		
		Editor editor = sharedPreferences.edit();
		editor.putString("myEmail", myEmail);
		editor.putString("myPassword", myPassword);
		editor.putString("readOPtion", Constants.READ_OPTION_SUBJECT_ONLY);
		editor.putInt("increment", 10);
		editor.putString("bodyDoneFlag", bodyDoneFlag);
		editor.commit();

		String FILENAME = "pcMailAccount";
		String string = "hello  world!";
		String del = "_____";
		
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
         
		FileOutputStream fos;
		try {
			folder.createNewFile();
//			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
	        fos = new FileOutputStream(new File(folder, FILENAME));
	        string = "myEmail:" + myEmail + del;
			fos.write(string.getBytes());
	        string = "myPassword:" + myPassword + del;
			fos.write(string.getBytes());
	        string = "bodyDoneFlag:" + bodyDoneFlag + del;
			fos.write(string.getBytes());
			
			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
