package com.timebyte.vm1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.widget.Toast;

public abstract class SharedPreferencesActivity extends MainActivity {

	protected void doSetting() {
//		doSettingWrite();
//		doSettingRead();
	}

	protected void getPreferenceFromFile() {
		getPreferenceFromFile("pcMailAccount");
		getPreferenceFromFile("pcMailContacts");
	}
	
	protected void getPreferenceFromFile(String filename) {
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");

		File file = new File(folder, filename);
		if (file.exists()) {
			// Read text from file
			StringBuilder text = new StringBuilder();

			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;

				while ((line = br.readLine()) != null) {
					text.append(line);
					text.append('\n');
				}
				br.close();
			} catch (IOException e) {
				// You'll need to add proper error handling here
				e.printStackTrace();
			}

			setupPreferences(text);
		}
	}

	private void setupPreferences(StringBuilder text) {
		// TODO Auto-generated method stub
		String del = "_____";
		
		StringTokenizer st = new StringTokenizer(text.toString(), del);
		
		Editor editor = sharedPreferences.edit();
		
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			int ind = str.indexOf(":");
			if (ind > 0) {
				String name = str.substring(0, ind);
				String value = str.substring(ind+1);
				switch (name) {
				case "myEmail" :
					logStr.add("***getPreferenceFromFilemyEmail " + value);
					editor.putString("myEmail", value);
					break;
				case "myPassword" :
					editor.putString("myPassword", value);
					break;
				case "bodyDoneFlag" :
					editor.putString("bodyDoneFlag", value);
					break;
				default :
					contacts.put(name, value);
					break;
				}
			}
		}
		
		editor.commit();
	}

	protected void settingNoticeOld() {
		popupDialogOld();
	}
	
	private void popupDialogOld() {
		new AlertDialog.Builder(this)
	    .setTitle("Setting Account")
	    .setMessage("You did not set up your account yet!")
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
	}
	
}
