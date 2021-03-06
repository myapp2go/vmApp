package com.app2go.app2go;

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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.widget.Toast;

public abstract class SharedPreferencesActivity extends MainActivity {

//	static Logger log = ALogger.getLogger(SharedPreferencesActivity.class);
	
	public static Map<String, String> map = new LinkedHashMap<String, String>();
	public static String quoteList;
	
	protected void doSetting() {
//		doSettingWrite();
//		doSettingRead();
	}

	protected void getPreferenceFromFile() {
		getPreferenceFromFile(MainActivity.preferenceFile);
	}
	
	protected void getPreferenceFromFile(String filename) {
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/Quotes");
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

//			Editor editor = sharedPreferences.edit();
//			editor.putString("Quotes", text.toString());
//			editor.commit();
			String str = text.toString();
			int ind = str.indexOf("#");
			sleepTime = Integer.parseInt(str.substring(0, ind));
			quoteList = str.substring(ind+1);
//			log.debug("Quote List: " + quoteList);
			setupPreferences(quoteList);
		}
	}

	private void setupPreferences(String text) {	
		StringTokenizer st = new StringTokenizer(text, Constants.CONTACT_MARKER);

		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			int ind = str.indexOf(":");
			if (ind > 0) {
				String key = str.substring(0, ind);
				String value = str.substring(ind+1);
				map.put(key, value);
			}
		}
	}
	
}
