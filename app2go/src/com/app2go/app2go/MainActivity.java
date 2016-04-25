package com.app2go.app2go;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public abstract class MainActivity extends Activity implements OnInitListener  {

	public static MainActivity mainActivity;
	
	abstract protected void doReadStockQuote(ArrayList<String> matches);
/*
	abstract protected void doSearchMail(ArrayList<String> matches);
	abstract protected void doSaveOffLines();
	abstract protected void readMessageBody();
	abstract protected void readOneMessage();
	abstract protected void doWriteMail(ArrayList<String> matches);
	abstract protected void doDebugMail(String myEmail, String myPassword, String mailTo, String mailSubject, String mailBody);
*/
	abstract protected void getPreferenceFromFile();
	
	private final int VOICE_RECOGNITION = 1234;
	protected SharedPreferences sharedPreferences;
	
	protected TextToSpeech tts;
	protected Intent intent;
	HashMap<String, String> mapTTS = new HashMap<String, String>();
	HashMap<String, String> mapTTSPhone = new HashMap<String, String>();
	HashMap<String, String> mapEarcon = new HashMap<String, String>();
	private static final String mapTTSID = "mapTTSID";
	private static final String mapTTSPhoneID = "mapTTSPhoneID";
	private static final String mapEarconID = "mapEarconID";
	
	protected int mailCount = 0;
	protected int mailSize = 0;
	protected int searchSize = 0;
	protected int maxReadCount = 200;
    protected boolean readBodyDone = true;
    
	protected String command = Constants.COMMAND_INIT;
    protected String subCommand = Constants.COMMAND_INIT;
	
    protected boolean isSyncMail = false;
    protected boolean isOffline = false;
    
	protected HashMap<String, String> contacts = new HashMap<String, String>();
    
	private String commandType = Constants.ANSWER_CONTINUE;
	HashMap<String, String> commandMap = new HashMap<String, String>();
	
	ArrayList<String> recognizerResult = new ArrayList<String>();
	
	private Handler handler;
	
	private ProgressDialog processDialog;
	private static boolean readDone = true;
	private static boolean readStop = false;
	private static boolean writeStop = false;
	private static String speechDone = null;
	
	protected Vector<String> logStr = new Vector<String>();
	private Button searchMail;
	private Button offLine;
	private boolean once = false;
	
	private String mailAccount = "";
	private String messageQueue = null;
	private String earconQueue = null;

	private SpeechRecognizer sr;
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (BuildConfig.DEBUG) {
			logStr.add("[onCreate called]");
		}
		
		mainActivity = this;
		
		handler = new Handler();
		
		initTTS();
		
		initRecognizer();
		
		final Button readMail = (Button) this.findViewById(R.id.readMail);
		readMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (speechDone == null) {
					ArrayList<String> localArrayList = new ArrayList<String>();
	
					doReadStockQuote(localArrayList);

				}
			}
		});
				
		final Button settings = (Button) this.findViewById(R.id.settings);
		settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (speechDone == null) {				
					startSettings();
				}
			}
		});
	
		final Button debugging = (Button) this.findViewById(R.id.debugging);
		debugging.setVisibility(View.GONE);
		debugging.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startDebugging();
			}
		});
		
		if (BuildConfig.DEBUG) {
			logStr.add("[onCreate done]");
		}
	}

	public void startSettings() {
		Intent ttsIntent = new Intent(this, SettingActivity.class);
		
		startActivity(ttsIntent);
	}

	public void startDebugging() {
		String msg = logStr.toString();
		
//		doDebugMail(sharedPreferences.getString("myEmail", ""), sharedPreferences.getString("myPassword", ""), "paulchennk@gmail.com", "VoiceMailDebug", msg);
	}
	
	public void initRecognizer() {	
        sr = SpeechRecognizer.createSpeechRecognizer(this);       
        sr.setRecognitionListener(new listener()); 
        
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5); 
        
        /*        
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);  
	    intent.putExtra(
	    	RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
	        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);  
	    intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(1000));
*/
	}

	public void startRecognizer(int ms) {
//    	System.out.println("******startRecognizer " + android.os.Process.myTid());

		if (mapEarconID.equals(speechDone)) {
			if (ms > 0) {
				SystemClock.sleep(ms);
			}

			if ((Constants.COMMAND_READ.equals(command) && mailCount <= mailSize)
					|| (Constants.COMMAND_SEARCH.equals(command) && mailCount <= searchSize)) {
				handler.postDelayed(checkRecognizer, 10000);
			}

//			startActivityForResult(intent, VOICE_RECOGNITION);
			
			sr.startListening(intent);
		}
	}

	private Runnable checkRecognizer = new Runnable() {
	    public void run() {	
	    	finishActivity(VOICE_RECOGNITION);
	    	
	    	if (readBodyDone) {
//	    		readOneMessage();
	    	} else {
//	    		readMessageBody();
	    	}
	    }
	};
	
	private void initTTS() {		
		mapTTS.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mapTTSID);
		mapTTSPhone.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mapTTSPhoneID);
		mapEarcon.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mapEarconID);
		
		tts = new TextToSpeech(this, this);
		
		tts.addEarcon("beep15", "com.timebyte.vm1", R.raw.beep15);
		tts.addEarcon("beep17", "com.timebyte.vm1", R.raw.beep17);
		tts.addEarcon("beep21", "com.timebyte.vm1", R.raw.beep21);

		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override
			public synchronized void onDone(String utteranceId) {				
//				logStr.add("************onDone " + command + " * " + speechDone + " * " + microphoneDone + " * " + microphoneOn + " * " + readBodyDone + " * " + mailCount + " * " + mailSize);
//				System.out.println("&&&&& " + utteranceId + "***********onDone " + android.os.Process.myTid() + " * " + command + " * " + speechDone + " * " + readBodyDone + " * " + mailCount + " * " + mailSize);
				if (!once) {
//					endDialog();
					once = true;
				}
				
				switch (utteranceId) {
				case mapTTSID :
					if (mapTTSID.equals(speechDone)) {
						speechDone = null;
						return;
					} else {
			    		System.out.println("***********ERROR_onDone_01, should not happen. " + speechDone);
					}
					break;
				case mapTTSPhoneID :
					if (mapTTSPhoneID.equals(speechDone)) {
						speechDone = null;
						// then call bell
					} else {
			    		System.out.println("***********ERROR_onDone_02, should not happen. " + speechDone);
					}
					break;		
				case mapEarconID :
					if (mapEarconID.equals(speechDone)) {
						speechDone = null;
						if (messageQueue != null) {
							ttsAndMicrophone(messageQueue);
							messageQueue = null;
						}
						return;
					} else {
			    		System.out.println("***********ERROR_onDone_03, should not happen. " + speechDone);
					}
					break;		
				default :
					System.out.println("***********ERROR_onDone_04, should not happen. " + speechDone);
					return;
				}
				
	            switch (command) {
	            case Constants.COMMAND_READ:
	            	ttsAndPlayEarcon("beep21");
					break;
	            case Constants.COMMAND_WRITE : 
	            	ttsAndPlayEarcon("beep21");
	            	break;
	            case Constants.COMMAND_SEARCH : 
	            	ttsAndPlayEarcon("beep21");
	            	break;
	            case Constants.COMMAND_SETTING :            	
	            	break;
	            case Constants.COMMAND_COMMAND_RECORD :
	            	ttsAndPlayEarcon("beep21");
	            	break;
	            case Constants.COMMAND_STOP:
	            	break;	
	            default :								// INIT
	            	System.out.println("*** ERROR96 ");
//	            	ttsNoMicrophone(Constants.NETWORK_ERROR);
	            	break;
	            }
			}

			@Override
			public void onStart(String utteranceId) {
//				System.out.println("&&&&& " + utteranceId + "***onStart " + android.os.Process.myTid());
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
		ttsNoMicrophone(Constants.COMMAND_READ_SUBJECT_BODY);
//		startDialog();
		
		sharedPreferences = getApplicationContext().getSharedPreferences("VoiceMailPref", MODE_PRIVATE); 
		getPreferenceFromFile();

		mailAccount = sharedPreferences.getString("myEmail", "");
		
		readDone = true;
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if (handler != null) {
    		handler.removeCallbacks(checkRecognizer);
    	}
    	
        if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK)
        {  
            ArrayList<String> matches = data.getStringArrayListExtra
            		(RecognizerIntent.EXTRA_RESULTS); 
            recognizerResult.add(matches.toString());
            
//            logStr.add("[** " + matches.toString() + " **]");
//            System.out.println("[** " + matches.toString() + " **]");
            
            switch (command) {
            case Constants.COMMAND_READ:
//            	doReadMail(matches);
            	break;
            case Constants.COMMAND_WRITE : 
//            	doWriteMail(matches);
            	break;
            case Constants.COMMAND_SEARCH:
            	if (Constants.COMMAND_INIT.equals(subCommand)) {
//            		doSearchMail(matches);
            	} else {
//            		doReadMail(matches);
            	}
            	break;
            case Constants.COMMAND_SETTING :
            	break;
            case Constants.COMMAND_STOP :
            	break;
            case Constants.COMMAND_COMMAND_RECORD :
            	for (int i = 0; i < matches.size(); i++) {
            		commandMap.put(matches.get(i), commandType);
            	}
            	startSettings();
            	break;	
            default :								// INIT
            	System.out.println("*** ERROR ");
            	break;
            }
        } else {
        	if (Constants.COMMAND_WRITE.equals(command)) {
//        		isOffline = true;
//        		ttsNoMicrophone(Constants.NETWORK_ERROR);
        	}
        	System.out.println("10 *** No Match " + command);
        }
    }    

        
    
    protected void ttsAndMicrophone(String msg) {
//    	System.out.println("******ttsAndMicrophone " + android.os.Process.myTid() + msg);

    	if (mapEarconID.equals(speechDone)) {
    		messageQueue = msg;
    		return;
    	}
 
    	// either mapTTSID or mapTTSPhoneID
    	if (speechDone != null) {
    		System.out.println("***********ERROR_01, should not happen. " + speechDone);
    	} else {
    		speechDone = mapTTSPhoneID;	
    		tts.speak(msg, TextToSpeech.QUEUE_ADD, mapTTSPhone);
    	}
    }
    
    protected void ttsNoMicrophone(String msg) {
    	tts.speak(msg, TextToSpeech.QUEUE_ADD, mapTTS);
    }
    
    protected void ttsAndPlayEarcon(String msg) {
    	System.out.println("******ttsAndPlayEarcon " + android.os.Process.myTid() +  " * " + msg);
/*
    		if (handler != null) {
    			handler.removeCallbacks(checkRecognizer);
    		}
*/    	
    		tts.playEarcon(msg, TextToSpeech.QUEUE_ADD, mapEarcon);
//    		startRecognizer(0);

    }
    
    private boolean isSetting() {
    	boolean flag = false;
    	
    	if (sharedPreferences != null) {
    		String myEmail = sharedPreferences.getString("myEmail", "");
    		String myPassword = sharedPreferences.getString("myPassword", "");
    		if ((myEmail != null) && (myPassword != null) && (myEmail.length() > 0) && (myPassword.length() > 0)) {
    			flag = true;
    		}
    	}
    	return flag;
    }
    
    private boolean syncContact() {
    	boolean flag = false;
    	
    	Map <String, String> map = (Map<String, String>) sharedPreferences.getAll();
    	if (map != null) {
    		Iterator it = map.entrySet().iterator();
    	    while (it.hasNext()) {
    	        Map.Entry pair = (Map.Entry)it.next();
    	        String key = pair.getKey().toString();
    	        int ind = key.indexOf(Constants.CONTACT_MARKER);
    	        if (ind == 0 && (key.length() > Constants.CONTACT_MARKER.length())) {
        	        String name = key.substring(Constants.CONTACT_MARKER.length(), key.length());
 //       	        System.out.println("***********NNN " + name);
    	        	contacts.put(name, pair.getValue().toString());
    	        	flag = true;
    	        }
    	    }
    	}
    	
    	return flag;
    }
    
    class listener implements RecognitionListener          
    {
             public void onReadyForSpeech(Bundle params)
             {
                      System.out.println("001 onReadyForSpeech");
             }
             public void onBeginningOfSpeech()
             {
             	System.out.println("02 onBeginningOfSpeech");
             }
             public void onRmsChanged(float rmsdB)
             {
             	System.out.println("03 onRmsChanged");
             }
             public void onBufferReceived(byte[] buffer)
             {
             	System.out.println("04 nonBufferReceived");
             }
             public void onEndOfSpeech()
             {
             	System.out.println("05 onEndofSpeech");
             }
             public void onError(int error)
             {
             	System.out.println("06 errorNum " +  error);
//                      mText.setText("error " + error);
             }
             public void onResults(Bundle results)                   
             {
                      String str = new String();
                      System.out.println("07 onResults " + results);
                      ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                      for (int i = 0; i < data.size(); i++)
                      {
                     	 System.out.println("result " + data.get(i));
                                str += data.get(i);
                      }
//                      mText.setText("results: "+String.valueOf(data.size()));        
             }
             public void onPartialResults(Bundle partialResults)
             {
             	System.out.println("08 onPartialResults");
             }
             public void onEvent(int eventType, Bundle params)
             {
             	System.out.println("09 onEvent " + eventType);
             }
    }

}
