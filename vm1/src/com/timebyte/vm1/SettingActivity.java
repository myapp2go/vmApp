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
		
		contacts.put("paul", "paultchan@yahoo.com");		
		contacts.put("tony", "paulchennk@yahoo.com");
		contacts.put("david", "davidchennk@gmail.com");		
		contacts.put("davis", "davischennk@gmail.com");
		contacts.put("san", "samchennk@gmail.com");		
		contacts.put("tanan", "tananpaulchen@gmail.com");
		contacts.put("allen", "paulchen1559@gmail.com");		
		contacts.put("pilot", "pilotstockfirst@gmail.com");
		contacts.put("chen", "tonychennk@gmail.com");		
		contacts.put("john", "johnchennk@yahoo.com");
		contacts.put("andy", "andy1chennk@yahoo.com");		
		contacts.put("lee", "andy2chennk@yahoo.com");
		contacts.put("ken", "samchennk@yahoo.com");		
		contacts.put("joe", "davidchennk@yahoo.com");
		contacts.put("josh", "davischennk@yahoo.com");		
		contacts.put("tom", "tapaulchen@gmail.com");
	}
}
