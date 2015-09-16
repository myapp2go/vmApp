package com.timebyte.vm1;

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

public class AccountActivity extends Activity {

	protected SharedPreferences sharedPreferences;
	
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

			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
