package com.timebyte.speakenglish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DataActivity extends SpeakActivity {
    
	protected void initDefinitionData() {
		readWordData();
		readPronunication();
		readDefinition();
	}

	@Override
	protected void initDefinitionView() {
		super.initDefinitionView();
		
		mouth1 = (TextView) findViewById(R.id.mouth1);
		mouth1Def = (TextView) findViewById(R.id.mouth1Def);
		mouth1Type = (Button) findViewById(R.id.mouth1Type);
		mouth1Type.setOnClickListener(new View.OnClickListener() {
			boolean toggle = true;
			public void onClick(View v) {
				if (toggle) {
					String type = mouth1Type.getText().toString();
					String def = mapDefinition.get(type);
					mouth1Def.setText(def);
					mouth1Def.setVisibility(View.VISIBLE);
					toggle = false;
				} else {
					mouth1Def.setVisibility(View.GONE);
					toggle = true;
				}
			}
		});

		mouth2 = (TextView) findViewById(R.id.mouth2);
		mouth2Type = (Button) findViewById(R.id.mouth2Type);
		mouth2Def = (TextView) findViewById(R.id.mouth2Def);
		mouth3 = (TextView) findViewById(R.id.mouth3);
		mouth3Type = (Button) findViewById(R.id.mouth3Type);
		mouth3Def = (TextView) findViewById(R.id.mouth3Def);
		mouth4 = (TextView) findViewById(R.id.mouth4);
		mouth4Type = (Button) findViewById(R.id.mouth4Type);
		mouth4Def = (TextView) findViewById(R.id.mouth4Def);
		mouth5 = (TextView) findViewById(R.id.mouth5);
		mouth5Type = (Button) findViewById(R.id.mouth5Type);
		mouth5Def = (TextView) findViewById(R.id.mouth5Def);
	}
	
	protected void readWordData() {
		InputStream inputStream = null;
		
		try {
			inputStream = getResources().openRawResource(R.raw.worddata);
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			List<String> responseData = null;
			String key = "";
			int ind = 0;
			while ((line = in.readLine()) != null) {
				System.out.println("DDD " + line);
				if (line.charAt(0) == '[') {
					key = line.substring(1, line.length()-1);
				} else {
					mapWordData.put(line, key);
				}
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
//		Set<String> keySet = mapOfList.keySet();
//		keyArray = keySet.toArray(new String[keySet.size()]);
	}

	protected void readPronunication() {
		InputStream inputStream = null;
		
		try {
			inputStream = getResources().openRawResource(R.raw.pronunciation);
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			String key = "";
			int ind = 0;
			while ((line = in.readLine()) != null) {
				System.out.println("DDD " + line);
				if (line.charAt(0) == '[') {
					key = line.substring(1, line.length()-1);
				} else {
					System.out.println(line + " * " + key);
					mapPronunciation.put(key, line);
				}
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
//		Set<String> keySet = mapOfList.keySet();
//		keyArray = keySet.toArray(new String[keySet.size()]);
	}
	
	protected void readDefinition() {
		InputStream inputStream = null;
		
		try {
			inputStream = getResources().openRawResource(R.raw.definition);
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			String key = "";
			int ind = 0;
			while ((line = in.readLine()) != null) {
				System.out.println("DDD " + line);
				if (line.charAt(0) == '[') {
					key = line.substring(1, line.length()-1);
				} else {
					System.out.println(line + " * " + key);
					mapDefinition.put(key, line);
				}
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
//		Set<String> keySet = mapOfList.keySet();
//		keyArray = keySet.toArray(new String[keySet.size()]);
	}


	protected void procError(int index) {
    	String errStr = errorArray[index];
    	errorWord.setText(errStr);
    	String wordStr = mapWordData.get(errStr);
    	if (wordStr != null) {		        		
    		String pronunciationStr = mapPronunciation.get(wordStr);
    		if (pronunciationStr != null) {
    			if (pronunciationStr.charAt(0) == '1') {
    				System.out.println("*********** ITR1111 " + pronunciationStr);
					mouth1.setText("Vertical Position: ");
					mouth2.setText("Horizontal Position: ");
					mouth3.setText("Lip Rounding: ");
					mouth4.setText("Dithphongization: ");
					mouth5.setText("Tenseness: ");
					
					StringTokenizer type = new StringTokenizer(pronunciationStr, "_");
					String[] array = new String[6];
					int ind = 0;
					while (type.hasMoreTokens()) {
						array[ind++] = type.nextToken();
					}
					mouth1Type.setText(array[1]);
					mouth2Type.setText(array[2]);
					mouth3Type.setText(array[3]);
					mouth4Type.setText(array[4]);
					mouth5Type.setText(array[5]);
    			}
    		}
    	}
	}
}
