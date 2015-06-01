package com.timebyte.vm1;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class ReadMailTask extends AsyncTask {

	private ReadMailActivity readMailActivity;
	
	public ReadMailTask(ReadMailActivity mainActivity) {
		readMailActivity = mainActivity;
	}
	
	@Override
	protected Object doInBackground(Object... arg0) {

		SharedPreferences pref  = (SharedPreferences) arg0[0];
		// TODO Auto-generated method stub
//		SharedPreferences pref = getApplicationContext().getSharedPreferences("VoiceMailPref", MODE_PRIVATE); 
		String myEmail = pref.getString("myEmail", "");
		String myPassword = pref.getString("myPassword", "");
		String readOPtion = pref.getString("readOPtion", "");

		System.out.println("*************************TASK " + myEmail + " * " + myPassword + " * " + readOPtion);

		return null;
	}

}
