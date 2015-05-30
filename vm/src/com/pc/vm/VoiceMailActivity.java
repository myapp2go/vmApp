package com.pc.vm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;

public class VoiceMailActivity extends WriteMailActivity {

	protected void doSetting() {
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
	
	protected void doSettingRead() {
		//Get the text file
		String FILENAME = "pcVoiceMail";
		File file = new File("/data/data/com.pc.vm/files", FILENAME);

		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
		    }
		    br.close();
		    System.out.println("************************TEXT " + text);
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
			e.printStackTrace();
		}
	}
	
	protected void doSettingWrite() {
		String FILENAME = "pcVoiceMail";
		String string = "hello world!";
				

		FileOutputStream fos;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(string.getBytes());
			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
