package com.timebyte.speakenglish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnInitListener {

	private final int VOICE_RECOGNITION = 1234;
//    private ListView mList;
    private TextView mEcho;
    private TextView mKey;
    private TextView mySpeak;
    
    private String[] phase;
    private String speakMode = Constants.SPEAK_MODE_TEAINING;
    private int phaseNo = 0;
    protected HashMap<String, List<String>> mapOfList = new HashMap<String, List<String>>();
    protected HashMap<String, String> mapPronunciation = new HashMap<String, String>();
    protected HashMap<String, String> mapWordData = new HashMap<String, String>();
    protected HashMap<String, String> mapDefinition = new HashMap<String, String>();
    private String[] keyArray = new String[75];
    private int keyIndex = 0;
    private List<String> listPhase;
    private int phaseSize = 0;   
    private Set<String> errorSet;
    private String[] errorArray;
    
	protected TextToSpeech tts;
	protected Intent intent;
	HashMap<String, String> map = new HashMap<String, String>();

    protected boolean speanOn = false;
    
	String del = " ";
	
	private Button pronunciation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initTTS();
		
		initRecognizer();
		
//		initTraining();
		
//		mList = (ListView) findViewById(R.id.list);
		
		mEcho = (TextView) findViewById(R.id.echo);
		mKey = (TextView) findViewById(R.id.key);
		mySpeak = (TextView) findViewById(R.id.mySpeak);
//		mKey.setVisibility(View.GONE);
		
		final TextView errorWord = (TextView) findViewById(R.id.errorWord);
		final TextView mouth1 = (TextView) findViewById(R.id.mouth1);
		final TextView mouth1Type = (TextView) findViewById(R.id.mouth1Type);
		final TextView mouth2 = (TextView) findViewById(R.id.mouth2);
		final TextView mouth2Type = (TextView) findViewById(R.id.mouth2Type);
		final TextView mouth3 = (TextView) findViewById(R.id.mouth3);
		final TextView mouth3Type = (TextView) findViewById(R.id.mouth3Type);
		final TextView mouth4 = (TextView) findViewById(R.id.mouth4);
		final TextView mouth4Type = (TextView) findViewById(R.id.mouth4Type);
		final TextView mouth5 = (TextView) findViewById(R.id.mouth5);
		final TextView mouth5Type = (TextView) findViewById(R.id.mouth5Type);
/*		
		final Button verifySpeeck = (Button) this.findViewById(R.id.verifySpeeck);
		verifySpeeck.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				speakMode = Constants.SPEAK_MODE_VERIFY;
				mKey.setText(" ");
				mEcho.setText(" ");
				mySpeak.setText(" ");
				startSpeak();
			}
		});
*/		
		pronunciation = (Button) this.findViewById(R.id.pronunciation);
		pronunciation.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Iterator<String> itr = errorSet.iterator();
		        while(itr.hasNext()) {
		        	String errStr = itr.next();
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
			
//				speakMode = Constants.SPEAK_MODE_TEAINING;
//				startTraining();
			}
		});	
		pronunciation.setVisibility(View.GONE);
		
		final Button training = (Button) this.findViewById(R.id.training);
		training.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pronunciation.setVisibility(View.GONE);
				
				EditText lesson = (EditText) findViewById(R.id.lessonNum);
				int i = Integer.parseInt(lesson.getText().toString());
		        if (i >= 0) {
		        	keyIndex = i;
		        }
		        
				speakMode = Constants.SPEAK_MODE_TEAINING;
				mKey.setText(keyArray[keyIndex]);
				listPhase = mapOfList.get(keyArray[keyIndex++]);
				phaseSize = listPhase.size();
				startTraining();
			}
		});
		
		final Button next = (Button) this.findViewById(R.id.next);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pronunciation.setVisibility(View.GONE);
				
				speakMode = Constants.SPEAK_MODE_TEAINING;
				phaseNo++;
				if (phaseNo ==phaseSize) {
					phaseNo = 0;
					mKey.setText(keyArray[keyIndex]);
					listPhase = mapOfList.get(keyArray[keyIndex++]);
					phaseSize = listPhase.size();	
				}
				startTraining();
			}
		});
		
		final Button tryAgain = (Button) this.findViewById(R.id.tryAgain);
		tryAgain.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pronunciation.setVisibility(View.GONE);
				
				speakMode = Constants.SPEAK_MODE_TEAINING;
				startTraining();
			}
		});
		
	}

	private void initTraining() {
		phase = new String[10];
		
		phase[0] = "please sit in this seat";
		phase[1] = "these shoes should fit your feet";
		phase[2] = "do you still steal";
		phase[3] = "those bins are for beans";
		phase[4] = "they ship sheep";
	}

	private void startTraining() {
		tts.speak(listPhase.get(phaseNo), TextToSpeech.QUEUE_ADD, map);	
		speanOn = true;	
	}
	
	private void startSpeak() {
		// TODO Auto-generated method stub
		tts.speak(Constants.COMMAND_SAY, TextToSpeech.QUEUE_ADD, map);	
		speanOn = true;
	}
	
	public void initRecognizer() {	
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);  
	    intent.putExtra(
	    	RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
	        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);  
	}

	public void startRecognizer(int ms) {
		if (ms > 0) {
			SystemClock.sleep(ms);
		}
	    startActivityForResult(intent, VOICE_RECOGNITION); 
	}
	
	private void initTTS() {		
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
		
		tts = new TextToSpeech(this, this);
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override
			public synchronized void onDone(String utteranceId) {
				System.out.println("ONDONE" + phaseNo);
				if (speanOn) {
					switch (speakMode) {
					case Constants.SPEAK_MODE_TEAINING :
//						startTraining();
//						phaseNo++;
						startRecognizer(0);
						break;
					case Constants.SPEAK_MODE_VERIFY :
						startRecognizer(0);
						break;						
					}
				}				
			}

			@Override
			public void onStart(String utteranceId) {
				System.out.println("onStart");
			}

//			@Override
			public void onError(String utteranceId, int errorCode) {
				System.out.println("onErrorNew " + errorCode);
			}

			@Override
			public void onError(String arg0) {
				// TODO Auto-generated method stub
				System.out.println("onError");				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		readData();
		readWordData();
		readPronunication();
		readDefinition();
		
		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
    	super.onActivityResult(requestCode, resultCode, data);  
		System.out.println("onActivityResult");
        if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK)  
        {  
            ArrayList<String> matches = data.getStringArrayListExtra
            		(RecognizerIntent.EXTRA_RESULTS); 

            if (Constants.SPEAK_MODE_VERIFY.equals(speakMode)) {
            	mEcho.setText(matches.get(0).toString());
            } else {
            	String text = compareSpeak(matches.get(0).toLowerCase());
            
            	mEcho.setText(listPhase.get(phaseNo));
            	mySpeak.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        	}
        }  
    } 
    
    private String compareSpeak(String fromRec) {
    	errorSet = new LinkedHashSet<String>();
    	String text = "";

		StringTokenizer echo = new StringTokenizer(listPhase.get(phaseNo), del);
		StringTokenizer matched = new StringTokenizer(fromRec.toString(), del);
		
		boolean reachEnd = false;
		boolean foundError = false;
		String strEcho = "";
		String strMatched = "";
		while (matched.hasMoreTokens()) {
			if (echo.hasMoreTokens()) {
				strEcho = echo.nextToken();
			} else {
				reachEnd = true;
			}
			strMatched = matched.nextToken();
			
			if (!reachEnd) {
				if (strEcho.equals(strMatched)) {
					text += "<font color='blue'>" + strMatched  + "</font> ";
				} else {
					errorSet.add(strEcho);
					text += "<font color='red'>" + strMatched  + "</font> ";
					foundError = true;
				}
			} else {
				errorSet.add(strEcho);
				reachEnd = true;
				text += "<font color='red'>" + strMatched  + "</font> ";
				foundError = true;
			}			
		}
		
		while (echo.hasMoreTokens()) {
			strEcho = echo.nextToken();
			errorSet.add(strEcho);
			foundError = true;
		}
		
		if (foundError) {
			errorArray = new String[errorSet.size()];
			Iterator<String> itr = errorSet.iterator();
			int ind = 0;
	        while(itr.hasNext()) {
	        	errorArray[ind++] = itr.next();
	        }
			pronunciation.setVisibility(View.VISIBLE);			
		}
		
    	return text;
    }
    
	protected void readData() {
		InputStream inputStream = null;
		
		try {
			inputStream = getResources().openRawResource(R.raw.speakdata);
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			List<String> responseData = null;
			String key = "";
			int ind = 0;
			while ((line = in.readLine()) != null) {
				System.out.println("DDD " + line);
				if (line.charAt(0) == '[') {
					if (responseData == null) {
						responseData = new ArrayList<String>();
					} else {
						mapOfList.put(key, responseData);
						responseData = new ArrayList<String>();
					}
					key = line.substring(1, line.length()-1);
					keyArray[ind++] = key;
				} else {
					responseData.add(line);
				}
			}
			mapOfList.put(key, responseData);
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
}
