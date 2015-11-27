package com.pc.vm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
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
	
//	abstract protected void doWriteMail(ArrayList<String> matches);
	
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
	
	private static String speechDone = null;
	
	protected Vector<String> logStr = new Vector<String>();
	private boolean once = false;
	
	private String mailAccount = "";
	private String messageQueue = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (BuildConfig.DEBUG) {
			logStr.add("[onCreate called]");
		}
		
		mainActivity = this;
		
		getInstalledList();
		
		handler = new Handler();
		
		initTTS();
		
		initRecognizer();
		
		final Button readMail = (Button) this.findViewById(R.id.readMail);
		readMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (speechDone == null) {
					setFlag(false, false, true);

					if (!isSetting()) {
						ttsNoMicrophone(Constants.SETTING_ACCOUNT_NOTICE);
					} else {
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						command = Constants.COMMAND_READ;
						mailCount = 0;

						ArrayList<String> localArrayList = new ArrayList<String>();
						if (!mailAccount.equalsIgnoreCase(sharedPreferences.getString("myEmail", ""))) {
							isSyncMail = false;
						}
						if (isSyncMail) {
							subCommand = Constants.SUBCOMMAND_RETRIEVE;
							localArrayList.add(Constants.ANSWER_CONTINUE);
						} else {
							subCommand = Constants.COMMAND_INIT;
							localArrayList
									.add(Constants.READ_OPTION_SUBJECT_ONLY);
						}
//						doReadMail(localArrayList);
					}
				}
			}
		});
		
		if (BuildConfig.DEBUG) {
			logStr.add("[onCreate done]");
		}
	}

	public void getInstalledList() {
		ArrayList<String> aplist = new ArrayList<String>();
		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

		for(PackageInfo pack : packages) {
//	        PackageInfo p = packs.get(i);
//			System.out.println("***package " + pack.applicationInfo.loadLabel(getPackageManager()).toString());
//			System.out.println("***packageName " + pack.packageName);
			
		    ActivityInfo[] activityInfo;
			try {
				activityInfo = getPackageManager().getPackageInfo(pack.packageName, PackageManager.GET_ACTIVITIES).activities;
			    if(activityInfo!=null)
			    {
			        for(int i=0; i<activityInfo.length; i++)
			        {
//			            Log.i("PC",""+ activityInfo[i]);
	/*
			            if (myList != null)
			                myList = new ArrayList();

			            myList.add(pack.packageName);
			            */
			        }

			        aplist.add(pack.packageName);
			    }
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		    Log.i("Pranay", pack.packageName + " has total " + ((activityInfo==null)?0:activityInfo.length) + " activities");

		}
	}
		
	public void startDebugging() {
		String msg = logStr.toString();
		
	}
	
	public void initRecognizer() {	
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);  
	    intent.putExtra(
	    	RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
	        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);  
	    intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(1000));
	}

	public void startRecognizer(int ms) {
//    	System.out.println("******startRecognizer " + android.os.Process.myTid());

//		if (mapEarconID.equals(speechDone)) {
			if (ms > 0) {
				SystemClock.sleep(ms);
			}

			handler.postDelayed(checkRecognizer, 10000);

			startActivityForResult(intent, VOICE_RECOGNITION);
//		}
	}

	private Runnable checkRecognizer = new Runnable() {
	    public void run() {	
	    	finishActivity(VOICE_RECOGNITION);
	    	
	    	ttsAndPlayEarcon("beep21");
	    }
	};
	
	private void initTTS() {		
		mapTTS.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mapTTSID);
		mapTTSPhone.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mapTTSPhoneID);
		mapEarcon.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mapEarconID);
		
		tts = new TextToSpeech(this, this);
		
		tts.addEarcon("money", "com.timebyte.vm1", R.raw.money);
		tts.addEarcon("beethoven", "com.timebyte.vm1", R.raw.beethoven);
		tts.addEarcon("jetsons", "com.timebyte.vm1", R.raw.jetsons);
		tts.addEarcon("pinkpanther", "com.timebyte.vm1", R.raw.pinkpanther);
		tts.addEarcon("beep15", "com.timebyte.vm1", R.raw.beep15);
		tts.addEarcon("beep17", "com.timebyte.vm1", R.raw.beep17);
		tts.addEarcon("beep21", "com.timebyte.vm1", R.raw.beep21);

		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override
			public synchronized void onDone(String utteranceId) {				
//				logStr.add("************onDone " + command + " * " + speechDone + " * " + microphoneDone + " * " + microphoneOn + " * " + readBodyDone + " * " + mailCount + " * " + mailSize);
				System.out.println("&&&&&onDone " + utteranceId);
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
		ttsAndPlayEarcon("beep21");
		startDialog();
		
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
            System.out.println("[** " + matches.toString() + " **]");
            ttsAndPlayEarcon("beep21");
        } else {
        	System.out.println("10 *** No Match " + command);
        }
    }    

    public void commandRecord(String type) {

    }
        
    private void initCommandMap() {
    }
    
    String FILENAME = "voiceCommand";
    private void getVoiceCommand() {

    }
    
    private void saveCommand() {
		
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
//    	System.out.println("******ttsNoMicrophone " + android.os.Process.myTid());
 
    	if (mapEarconID.equals(speechDone)) {
    		messageQueue = msg;
    		return;
    	}
 
    	// either mapTTSID or mapTTSPhoneID
    	if (speechDone != null) {
    		System.out.println("***********ERROR_02, should not happen. " + speechDone);
    	} else {
    		speechDone = mapTTSID;
    		tts.speak(msg, TextToSpeech.QUEUE_ADD, mapTTS);
    	}
    }
    
    protected void ttsAndPlayEarcon(String msg) {
//    	System.out.println("******ttsAndPlayEarcon " + android.os.Process.myTid() +  " * " + msg);
    	speechDone = null;
    	
    	if (speechDone != null) {
    		System.out.println("***********ERROR_03, should not happen. " + speechDone);
    	} else {
    		speechDone = mapEarconID;

    		if (handler != null) {
    			handler.removeCallbacks(checkRecognizer);
    		}
    	
    		tts.playEarcon(msg, TextToSpeech.QUEUE_ADD, mapEarcon);
    		startRecognizer(0);
    	}
    }
    
    private void startDialog() {

    }
    
    protected void endDialog() {

    }
    
    protected void setFlag(boolean cmdDone, boolean cmdStop, boolean cmdWrite) {			

    }
    
    private boolean isSetting() {
    	boolean flag = false;

    	return flag;
    }
    
}
