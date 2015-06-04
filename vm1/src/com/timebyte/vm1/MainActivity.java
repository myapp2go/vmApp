package com.timebyte.vm1;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public abstract class MainActivity extends Activity implements OnInitListener  {

	abstract protected void doReadMail(ArrayList<String> matches);
	abstract protected void doWriteMail(ArrayList<String> matches);
	abstract protected void getPreferenceFromFile();

	private final int VOICE_RECOGNITION = 1234;
	protected SharedPreferences sharedPreferences;
	
	protected TextToSpeech tts;
	protected Intent intent;
	HashMap<String, String> map = new HashMap<String, String>();
	
	private int ttsCount = 0;
	
	protected String command = Constants.COMMAND_INIT;
    protected String subCommand = Constants.COMMAND_INIT;
    protected String answer = Constants.COMMAND_INIT;
    
    protected boolean speanOn = false;
    
	protected HashMap<String, String> contacts = new HashMap<String, String>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initTTS();
		
		initRecognizer();
		
		final Button readMail = (Button) this.findViewById(R.id.readMail);
		readMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				command = Constants.COMMAND_READ;
				subCommand = Constants.COMMAND_INIT;
				speanOn = true;
				
				tts.speak(Constants.COMMAND_READ_GREETING, TextToSpeech.QUEUE_ADD, map);		
			}
		});
		
		final Button writeMail = (Button) this.findViewById(R.id.writeMail);
		writeMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				command = Constants.COMMAND_WRITE;
				subCommand = Constants.SUBCOMMAND_TO;
				speanOn = true;
				
				tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
			}
		});
		
		final Button settings = (Button) this.findViewById(R.id.settings);
		settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startSettings();
			}
		});
	}

	public void startSettings() {
	    // Do something in response to button
		Intent ttsIntent = new Intent(this, SettingActivity.class);
		
		startActivity(ttsIntent);
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
				System.out.println("ONDONE " + ttsCount);
				if (speanOn) {
					startRecognizer(0);
				}
				
	            switch (command) {
	            case Constants.COMMAND_WRITE : 

	            	break;
	            case Constants.COMMAND_READ:
	            	if (ttsCount == Constants.MAIL_PER_PAGE) {
	            		System.out.println("READDDDD ONDONE " + ttsCount);
	            		ttsCount = 0;
	            		tts.speak(Constants.COMMAND_READ_ACTION, TextToSpeech.QUEUE_ADD, map);
	            		speanOn = true;
	            	} else {
	            		ttsCount++;
	            	}
	            	break;
	            case Constants.COMMAND_SETTING:
	            	break;
	            case Constants.COMMAND_STOP:
	            	break;	
	            default :								// INIT
	            	System.out.println("*** ERROR ");
	            	break;
	            }

/*				
				if (Constants.COMMAND_READ.equals(command)) {
					ttsCount++;
					if (ttsCount == increment) {
						checkReadMode = true;
						ttsCount = 0;
						System.out.println("before sp");
						tts.speak(Constants.COMMAND_READ_GREETING, TextToSpeech.QUEUE_ADD, map);
//						SystemClock.sleep(2000);
						System.out.println("after sp");
						startRecognizer();						
					}
				}
				*/
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
		sharedPreferences = getApplicationContext().getSharedPreferences("VoiceMailPref", MODE_PRIVATE); 

		getPreferenceFromFile();
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
    	super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK)
        {  
            ArrayList<String> matches = data.getStringArrayListExtra
            		(RecognizerIntent.EXTRA_RESULTS); 
            
            switch (command) {
            case Constants.COMMAND_WRITE : 
            	doWriteMail(matches);
            	break;
            case Constants.COMMAND_READ:
            	doReadMail(matches);
            	break;
            case Constants.COMMAND_SETTING:
            	break;
            case Constants.COMMAND_STOP:
            	break;	
            default :								// INIT
            	System.out.println("*** ERROR ");
            	break;
            }
        }
    }    

}