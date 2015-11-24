package com.timebyte.vm1;

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
	
	abstract protected void doReadMail(ArrayList<String> matches);
	abstract protected void doSearchMail(ArrayList<String> matches);
	abstract protected void doSaveOffLines();
	abstract protected void readMessageBody();
	abstract protected void readOneMessage();
	abstract protected void doWriteMail(ArrayList<String> matches);
	abstract protected void doDebugMail(String myEmail, String myPassword, String mailTo, String mailSubject, String mailBody);
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
		
		getVoiceCommand();
		
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
						doReadMail(localArrayList);
					}
				}
			}
		});
		
		final Button writeMail = (Button) this.findViewById(R.id.writeMail);
		writeMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (isOffline) {
					ttsNoMicrophone(Constants.NETWORK_ERROR);
				} else {
					if (speechDone == null) {
						setFlag(readDone, true, false);

						if (!isSetting()) {
							ttsNoMicrophone(Constants.SETTING_ACCOUNT_NOTICE);
						} else {
							if (!syncContact() && contacts.isEmpty()) {
								ttsNoMicrophone(Constants.SETTING_CONTACT_NOTICE);
							} else {
								command = Constants.COMMAND_WRITE;
								subCommand = Constants.SUBCOMMAND_TO;
								ttsAndMicrophone(Constants.COMMAND_TO_GREETING);
							}
						}
					}
				}
			}			
		});
		
		final Button settings = (Button) this.findViewById(R.id.settings);
		settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (speechDone == null) {
					setFlag(readDone, true, false);
				
					startSettings();
				}
			}
		});
		
		final Button syncMail = (Button) this.findViewById(R.id.syncMail);
		syncMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (speechDone == null) {
					setFlag(false, false, true);

					if (!isSetting()) {
						ttsNoMicrophone(Constants.SETTING_ACCOUNT_NOTICE);
					} else {
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						isSyncMail = false;
						mailCount = 0;
						command = Constants.COMMAND_READ;
						subCommand = Constants.COMMAND_INIT;
						ArrayList<String> localArrayList = new ArrayList<String>();
						localArrayList.add(Constants.READ_OPTION_SUBJECT_ONLY);
						doReadMail(localArrayList);
					}
				}
			}
		});
		
		searchMail = (Button) this.findViewById(R.id.searchMail);
		searchMail.setVisibility(View.GONE);
		searchMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (speechDone == null) {
					setFlag(true, true, true);

					if (!isSetting()) {
						ttsNoMicrophone(Constants.SETTING_ACCOUNT_NOTICE);
					} else {
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						command = Constants.COMMAND_SEARCH;
						subCommand = Constants.COMMAND_INIT;
						mailCount = 0;
						ttsAndMicrophone(Constants.COMMAND_SEARCH_GREETING);
					}
				}
			}
		});

		offLine = (Button) this.findViewById(R.id.offLine);
		offLine.setVisibility(View.GONE);
		offLine.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doSaveOffLines();
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
		
		doDebugMail(sharedPreferences.getString("myEmail", ""), sharedPreferences.getString("myPassword", ""), "paulchennk@gmail.com", "VoiceMailDebug", msg);
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

		if (mapEarconID.equals(speechDone)) {
			if (ms > 0) {
				SystemClock.sleep(ms);
			}

			if ((Constants.COMMAND_READ.equals(command) && mailCount <= mailSize)
					|| (Constants.COMMAND_SEARCH.equals(command) && mailCount <= searchSize)) {
				handler.postDelayed(checkRecognizer, 10000);
			}

			startActivityForResult(intent, VOICE_RECOGNITION);
		}
	}

	private Runnable checkRecognizer = new Runnable() {
	    public void run() {	
	    	finishActivity(VOICE_RECOGNITION);
	    	
	    	if (readBodyDone) {
	    		readOneMessage();
	    	} else {
	    		readMessageBody();
	    	}
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
//				System.out.println("&&&&& " + utteranceId + "***********onDone " + android.os.Process.myTid() + " * " + command + " * " + speechDone + " * " + readBodyDone + " * " + mailCount + " * " + mailSize);
				if (!once) {
					endDialog();
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
		startDialog();
		
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
            	doReadMail(matches);
            	break;
            case Constants.COMMAND_WRITE : 
            	doWriteMail(matches);
            	break;
            case Constants.COMMAND_SEARCH:
            	if (Constants.COMMAND_INIT.equals(subCommand)) {
            		doSearchMail(matches);
            	} else {
            		doReadMail(matches);
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

    public void commandRecord(String type) {
    	command = Constants.COMMAND_COMMAND_RECORD;
    	commandType = type;
    	
        switch (type) {
        case Constants.ANSWER_CONTINUE : 
        	ttsAndMicrophone(Constants.COMMAND_COMMAND1_GREETING);
        	break;
        case Constants.ANSWER_DETAIL :
        	ttsAndMicrophone(Constants.COMMAND_COMMAND2_GREETING);
        	break;
        case Constants.ANSWER_SKIP :
        	ttsAndMicrophone(Constants.COMMAND_COMMAND3_GREETING);
        	break;
        case Constants.ANSWER_STOP :
        	ttsAndMicrophone(Constants.COMMAND_COMMAND4_GREETING);
        	break;
        case Constants.ANSWER_SAVE :
        	saveCommand();
        	break;	
        case Constants.ANSWER_CLEAN :
        	commandMap = new HashMap<String, String>();
        	initCommandMap();
        	break;	
        default :								// INIT
        	System.out.println("*** ERROR ");
        	break;
        }
    }
        
    private void initCommandMap() {
    	commandMap.put(Constants.ANSWER_DETAIL, Constants.ANSWER_DETAIL);
    	commandMap.put(Constants.ANSWER_SKIP, Constants.ANSWER_SKIP);
    	commandMap.put(Constants.ANSWER_STOP, Constants.ANSWER_STOP);
    	commandMap.put(Constants.ANSWER_CONTINUE, Constants.ANSWER_CONTINUE);
    }
    
    String FILENAME = "voiceCommand";
    private void getVoiceCommand() {
    	initCommandMap();
    			
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");

		File file = new File(folder, FILENAME);
		if (file.exists()) {
			// Read text from file
			StringBuilder text = new StringBuilder();

			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;

				while ((line = br.readLine()) != null) {
					text.append(line);
					text.append('\n');
				}

				String del = "_";
				StringTokenizer st = new StringTokenizer(text.toString(), del);
				while (st.hasMoreTokens()) {
					String str = st.nextToken();
					if (str.length() > 2) {
						String value = str.substring(0, 1);
						String key = str.substring(2, str.length());
						commandMap.put(key, value);
					}
				}

				br.close();
			} catch (IOException e) {
				// You'll need to add proper error handling here
				e.printStackTrace();
			}
		}
    }
    
    private void saveCommand() {
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
        
		FileOutputStream fos;
		try {
			folder.createNewFile();
//			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
	        fos = new FileOutputStream(new File(folder, FILENAME));
	        
	        Iterator it = commandMap.entrySet().iterator();
	        while (it.hasNext()) {
	            Map.Entry pair = (Map.Entry)it.next();
	            String str = pair.getValue() + "," + pair.getKey() + "_";
	            fos.write(str.getBytes());
	        }

			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		processDialog = new ProgressDialog(this);
		processDialog.setMessage("Start speech engine, please wait...");
		processDialog.setIndeterminate(false);
		processDialog.setCancelable(false);
		processDialog.show();
    }
    
    protected void endDialog() {
    	if (mailSize > 0) {
    		searchMail.setVisibility(View.VISIBLE);
    	}
//    	offLine.setVisibility(View.VISIBLE);
    	
    	if (processDialog != null) {
    		processDialog.dismiss();
    	}
    	
//    	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	readDone = true;
    }
    
    protected void setFlag(boolean cmdDone, boolean cmdStop, boolean cmdWrite) {			
    	readDone = cmdDone;
    	readStop = cmdStop;
    	writeStop = cmdWrite;
    	
    	tts.playEarcon("", TextToSpeech.QUEUE_FLUSH, mapEarcon);
    	if (readStop || writeStop) {
    		finishActivity(VOICE_RECOGNITION);
    	}
    	
        ConnectivityManager connMgr = (ConnectivityManager) 
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	
    	if (networkInfo == null) {
    		isOffline = true;
    		String str = sharedPreferences.getString("bodyDoneFlag", "");
    		if ("F".equals(str)) {
    			readBodyDone = false;
    		} else {
    			readBodyDone = true;
    		}
    	} else {
        	isOffline = false;
    	}
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
}
