package com.timebyte.speakenglish;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnInitListener {

	private final int VOICE_RECOGNITION = 1234;
//    private ListView mList;
    private TextView mEcho;
    private TextView mySpeak;
    
    private String[] phase;
    private String speakMode = Constants.SPEAK_MODE_TEAINING;
    private int phaseNo = 0;
    
	protected TextToSpeech tts;
	protected Intent intent;
	HashMap<String, String> map = new HashMap<String, String>();

    protected boolean speanOn = false;
    
	String del = " ";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initTTS();
		
		initRecognizer();
		
		initTraining();
		
//		mList = (ListView) findViewById(R.id.list);
		mEcho = (TextView) findViewById(R.id.echo);
		mySpeak = (TextView) findViewById(R.id.mySpeak);
		
		final Button verifySpeeck = (Button) this.findViewById(R.id.verifySpeeck);
		verifySpeeck.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				speakMode = Constants.SPEAK_MODE_VERIFY;
				startSpeak();
			}
		});
		
		final Button training = (Button) this.findViewById(R.id.training);
		training.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				speakMode = Constants.SPEAK_MODE_TEAINING;
				startTraining();
			}
		});
		
		final Button next = (Button) this.findViewById(R.id.next);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				speakMode = Constants.SPEAK_MODE_TEAINING;
				phaseNo++;
				startTraining();
			}
		});
		
		final Button tryAgain = (Button) this.findViewById(R.id.tryAgain);
		tryAgain.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
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
		tts.speak(phase[phaseNo], TextToSpeech.QUEUE_ADD, map);	
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
            /*
            mList.setAdapter(new ArrayAdapter<String>(
            		this, android.R.layout.simple_list_item_1, matches));  
            		*/
            String text = compareSpeak(matches.get(0).toString());
            
            mEcho.setText(phase[phaseNo]);
            mySpeak.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

 /*
            String text = "This is <font color='red'>red</font>. This is <font color='blue'>blue</font>.";
            mText.setText(text);
            mText.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
 */
        }  
    } 
    
    private String compareSpeak(String fromRec) {
    	String text = "";

		StringTokenizer echo = new StringTokenizer(phase[phaseNo], del);
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
					text += "<font color='red'>" + strMatched  + "</font> ";
					foundError = true;
				}
			} else {
				reachEnd = true;
				text += "<font color='red'>" + strMatched  + "</font> ";
				foundError = true;
			}			
		}
		
    	return text;
    }
}
