package com.timebyte.vm1;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class SettingActivity extends WriteMailActivity {
	
	protected void doSetting() {
//		SharedPreferences pref = getApplicationContext().getSharedPreferences("VoiceMailPref", MODE_PRIVATE); 
		Editor editor = sharedPreferences.edit();
		editor.putString("myEmail", "tapaulchen@gmail.com");
		editor.putString("myPassword", "Tanan1559");
		editor.putString("readOPtion", Constants.READ_OPTION_SUBJECT_ONLY);
		editor.putInt("increment", 10);
		editor.commit();	
	}
}
