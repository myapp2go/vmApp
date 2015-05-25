package com.pc.vm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public abstract class MainActivity extends Activity implements OnInitListener {

	abstract protected void doReadMail();
	abstract protected void doWriteMail();
	abstract protected void doSetting();
	
	protected static int increment = 10;
	private static int msgCount = 0;
	private static final int VOICE_RECOGNITION = 1234;
	private static int ttsCount = 0;

	protected TextToSpeech tts;
	protected HashMap<String, String> map;
		
	private boolean initRecognizerFlag = false;
	private Intent intent;
	
//	public String currentCommand = "init";
    private String command = Constants.COMMAND_NONE;
    protected String subCommand = Constants.COMMAND_NONE;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tts = new TextToSpeech(this, this);
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override
			public synchronized void onDone(String utteranceId) {
				System.out.println("^^^^^^^^^^^^^^^^PC3344 ^onDone*********** " );
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
//			startRecognizer();
		}		
		final Button sendMail = (Button) this.findViewById(R.id.readMail);
		sendMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*
				String myEmail = ((TextView) findViewById(R.id.myEmail)).getText().toString();
				String myPassword = ((TextView) findViewById(R.id.myPassword)).getText().toString();
				new ReadMailTask(MainActivity.this).execute(msgCount, tts, myEmail, myPassword);
				*/
			}
		});
	}
	
	public void initRecognizer() {	
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);  
	    intent.putExtra(
	    	RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
	        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); 
	    startActivityForResult(intent, VOICE_RECOGNITION); 
	    System.out.print("^^^^^^^^^^^^^^^^^initRecognizer*********** " );
	}
	
	public void startRecognizer() {	
	    startActivityForResult(intent, VOICE_RECOGNITION); 
	    System.out.print("^^^^^^^^^^^^^^^^^startRecognizer999*********** " );
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
		// TODO Auto-generated method stub
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
		    
//		tts.speak("mail number :" + (i + 1) + message.getSubject(), TextToSpeech.QUEUE_ADD, null);
		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
		
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
//    	super.onActivityResult(requestCode, resultCode, data);
    	super.onActivityResult(requestCode, resultCode, data);
    	System.out.print("onActivityResult*********** " );
        if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK)
        {  
            ArrayList<String> matches = data.getStringArrayListExtra
            		(RecognizerIntent.EXTRA_RESULTS); 
           
            System.out.println("************ " + command);
            boolean found = false;

            switch (command) {
            case Constants.COMMAND_WRITE : 
            	found = true;
            	break;
            case Constants.COMMAND_READ:
            	break;
            case Constants.COMMAND_SETTING:
            	break;
            case Constants.COMMAND_STOP:
            	break;	
            default :
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
                    System.out.print(" " + matches.get(i)); 
                }
            	break;
            }
            
            System.out.println("***********CMD* " + command + " " + found);
            if (!found) {
        		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
        		startRecognizer();
            } else {
                switch (command) {
                case Constants.COMMAND_WRITE :  
                	doWriteMail();
//            		tts.speak(Constants.COMMAND_SUBJECT_GREETING, TextToSpeech.QUEUE_ADD, map);
//            		startRecognizer();
                	break;
                case Constants.COMMAND_READ:
                	doReadMail();
                	break;
                case Constants.COMMAND_STOP:
                	break;
                case Constants.COMMAND_SETTING:
                	break;
                	/*
                default :
                	if (command.equals(Constants.COMMAND_WRITE)) {
                		System.out.println("************SUBJECT " + matches.get(0));
                		tts.speak(Constants.COMMAND_SUBJECT_GREETING, TextToSpeech.QUEUE_ADD, map);
                		startRecognizer();
                	} else {
                		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
                		startRecognizer();
                	}
                	break;
                	*/
                }            	
            }
/*            

            
            if (Constants.COMMAND_READ.equals(matches.get(0))) {
            	currentCommand = Constants.COMMAND_READ;
            	doReadMail();
            } else if (Constants.COMMAND_STOP.equals(matches.get(0))) {
            } else {
            	if (currentCommand.equals(Constants.COMMAND_WRITE)) {
            		System.out.println("************SUBJECT " + matches.get(0));
            	} else {
            		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
            		startRecognizer();
            	}
            }
            */
//            if ("stop".equals(matches.get(0))) {
//            	msgCount += increment;
//            	readMessage(msgCount, msgLength);
            	
//				startRecognizer();
            	//            	rmTask.cancel(true);
            	
//            	for (int j = 0; j < 10; j++)
//            	if (!rmTask.isCancelled()) rmTask.cancel(true);
//            	System.out.println("******************************Cancel: " + rmTask.isCancelled());
//            }
            
//            SystemClock.sleep(7000);
//            createRecognizer();
            System.out.println("******************************Restart: ");
        }
    }
    
}
