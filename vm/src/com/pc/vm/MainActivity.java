package com.pc.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public abstract class MainActivity extends Activity implements OnInitListener {

	abstract protected void doReadMail(ArrayList<String> matches);
	abstract protected void doWriteMail(ArrayList<String> matches);
	abstract protected void doSetting();
	abstract protected void doSettingRead();
	abstract protected void doSettingWrite();
	
	protected static int increment = 10;
	private final int VOICE_RECOGNITION = 1234;
	protected int ttsCount = 0;

	protected TextToSpeech tts;
	protected HashMap<String, String> map;
		
	private boolean initRecognizerFlag = false;
	private Intent intent;
	
	protected String command = Constants.COMMAND_INIT;
    protected String subCommand = Constants.COMMAND_INIT;
    protected String answer = Constants.COMMAND_INIT;
    
    protected String mailTo ="";
    protected String mailSubject = "";
    protected String mailBody = "";
   
    protected boolean checkYesNo = false;
    protected String strLastGreeting = "";

	protected HashMap<String, String> contacts = new HashMap<String, String>();

    protected boolean checkReadMode = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tts = new TextToSpeech(this, this);
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override
			public synchronized void onDone(String utteranceId) {
				System.out.println("ONDONE");
				if (!initRecognizerFlag) {
					initRecognizer();
					initRecognizerFlag = true;
				} 
				
				if (Constants.COMMAND_READ.equals(command)) {
					ttsCount++;
					if (ttsCount == increment) {
						tts.speak(Constants.COMMAND_READ_GREETING, TextToSpeech.QUEUE_ADD, map);
						startRecognizer();
						ttsCount = 0;
					}
				}
			}

			@Override
			public void onStart(String utteranceId) {
			}

			@Override
			@Deprecated
			public void onError(String utteranceId) {
			}
		});
       
		PackageManager pm = getPackageManager();
		List<ResolveInfo> listResolveInfo = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (listResolveInfo.size() == 0) {
			System.out.println("Recognize Error");
		}		
		final Button sendMail = (Button) this.findViewById(R.id.readMail);
		sendMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			}
		});
		
		String str = getApplicationContext().getFilesDir().toString();
System.out.println("************************URL " + str);
	}
	
	public void initRecognizer() {	
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);  
	    intent.putExtra(
	    	RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
	        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); 
	    startActivityForResult(intent, VOICE_RECOGNITION); 
	}
	
	public void startRecognizer() {	
	    startActivityForResult(intent, VOICE_RECOGNITION); 
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
	public void onInit(int status) {
// PC522 only for testing		
		doSetting();
		doSettingRead();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
		    
		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);		
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
    	super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK)
        {  
            ArrayList<String> matches = data.getStringArrayListExtra
            		(RecognizerIntent.EXTRA_RESULTS); 
      
            boolean found = false;
            switch (command) {
            case Constants.COMMAND_WRITE : 
            	doWriteMail(matches);
            	break;
            case Constants.COMMAND_READ:
            	checkReadMode = true;
            	doReadMail(matches);
            	break;
            case Constants.COMMAND_SETTING:
            	break;
            case Constants.COMMAND_STOP:
            	break;	
            default :								// INIT
            	found = matchCommand(matches);
                if (!found) {
            		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
            		startRecognizer();
                } else {
                	procNewCommand(matches);
                }
            	break;
            }
        } else {
        	System.out.println("********PC522 ERROR");
        }
    }

    private void procNewCommand(ArrayList<String> matches) {
        switch (command) {
        case Constants.COMMAND_WRITE : 
        	doWriteMail(matches);
        	break;
        case Constants.COMMAND_READ:
        	doReadMail(matches);
        	break;
        case Constants.COMMAND_STOP:
        	break;
        case Constants.COMMAND_SETTING:
        	break;
        } 
    }
    
    protected void matchYesNo(ArrayList<String> matches) {
        boolean found = false;
        answer = Constants.COMMAND_NONE;
		System.out.println("matchYesNo " );
		
        for (int i = 0; !found && (i < matches.size()); i++) {
    		System.out.println("matchYesNoanswerRECCCC " + matches.get(i));
        	switch (matches.get(i)) {
        	case Constants.COMMAND_YES :
        		found = true;
        		answer = Constants.COMMAND_YES;
        		break;
        	case Constants.COMMAND_NO :
        		found = true;
        		answer = Constants.COMMAND_NO;
        		break;	
        	}
        }
// PC522        
        answer = Constants.COMMAND_YES;
		System.out.println("matchYesNoanswer " + answer);
	}
    
	private boolean matchCommand(ArrayList<String> matches) {
        boolean found = false;

        for (int i = 0; !found && (i < matches.size()); i++) {
        	switch (matches.get(i)) {
        	case Constants.COMMAND_READ:
        		command = Constants.COMMAND_READ;
        		ttsCount = 0;
        		found = true;
        		break;
        	case Constants.COMMAND_WRITE:
        		command = Constants.COMMAND_WRITE;
        		ttsCount = 0;
        		found = true;
        		break; 
        	case Constants.COMMAND_STOP:
        		command = Constants.COMMAND_STOP;
        		ttsCount = 0;
        		found = true;
        		break; 
            case Constants.COMMAND_SETTING:
        		command = Constants.COMMAND_SETTING;
        		ttsCount = 0;
        		found = true;
            	break;		
        	}
        }

        return found;
    }
}
