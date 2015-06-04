package com.timebyte.speakenglish;

import java.io.IOException;
import java.io.InputStream;

public class DataActivity extends MainActivity {

	protected void readData() {
		InputStream inputStream = null;
		
		try {
			inputStream = getResources().openRawResource(R.raw.speakdata);
			byte[] reader = new byte[inputStream.available()];
			while (inputStream.read(reader) != -1) {
				System.out.println("&&& " + reader);
			}
		} catch(IOException e) {

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {

				}
			}
		}
	}
}
