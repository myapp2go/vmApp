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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.Button;

public abstract class MainActivity extends Activity implements OnInitListener  {

	public static MainActivity mainActivity;
	
	abstract protected void doReadMail(ArrayList<String> matches);
	abstract protected void readMessageBody();
	abstract protected void doWriteMail(ArrayList<String> matches);
	abstract protected void getPreferenceFromFile();
	abstract protected void settingNotice();
	
	private final int VOICE_RECOGNITION = 1234;
	protected SharedPreferences sharedPreferences;
	
	protected TextToSpeech tts;
	protected Intent intent;
	HashMap<String, String> map = new HashMap<String, String>();
	
	protected int ttsCount = 1;
	protected int mailCount = 0;
	protected int maxReadCount = 50;
    protected boolean readBodyDone = false;
    protected boolean waitBodyCommand = false;
    
	protected String command = Constants.COMMAND_INIT;
    protected String subCommand = Constants.COMMAND_INIT;
	protected String readMode = Constants.COMMAND_INIT;  
	
    protected boolean microphoneOn = false;
    protected boolean isSetting = false;
    protected boolean isSyncMail = false;
    
	protected HashMap<String, String> contacts = new HashMap<String, String>();
    
	private boolean commandHelp = true;
	private String commandType = Constants.ANSWER_CONTINUE;
	HashMap<String, String> commandMap = new HashMap<String, String>();
	
	ArrayList<String> recognizerResult = new ArrayList<String>();
	
	private Handler handler;
	private String lastReadType = Constants.READ_OPTION_SUBJECT_ONLY;
	
	private ProgressDialog processDialog;
	private static boolean commandDone = true;
	
	private int maxMpInputRetry = 5;	
	private int mpInputRetry = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mainActivity = this;
		
		handler = new Handler();
		
		initTTS();
		
		initRecognizer();
		
		getVoiceCommand();
		
		final Button readMail = (Button) this.findViewById(R.id.readMail);
		readMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!commandDone) {
					startDialog();
					return;
				}				
				commandDone = false;
				
				if (!isSetting) {
					settingNotice();
				} else {
					lastReadType = Constants.READ_OPTION_SUBJECT_ONLY;
					command = Constants.COMMAND_READ;

					ttsCount = 1;
					mailCount = 0;

					ArrayList<String> localArrayList = new ArrayList<String>();
					if (isSyncMail) {
						subCommand = Constants.SUBCOMMAND_RETRIEVE;
						localArrayList.add(Constants.ANSWER_CONTINUE);
					} else {
						subCommand = Constants.COMMAND_INIT;
						localArrayList.add(Constants.READ_OPTION_SUBJECT_ONLY);
					}					
			        doReadMail(localArrayList);
				}
			}
		});
		
		final Button readBodyMail = (Button) this.findViewById(R.id.readBodyMail);
		readBodyMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!commandDone) {
					startDialog();
					return;
				}				
				commandDone = false;
				
				if (!isSetting) {
					settingNotice();
				} else {
					lastReadType = Constants.READ_OPTION_SUBJECT_BODY;
					command = Constants.COMMAND_READ;

					ttsCount = 1;
					mailCount = 0;

					ArrayList<String> localArrayList = new ArrayList<String>();
					if (isSyncMail) {
						subCommand = Constants.SUBCOMMAND_RETRIEVE;
						localArrayList.add(Constants.ANSWER_CONTINUE);
					} else {
						subCommand = Constants.COMMAND_INIT;
						localArrayList.add(Constants.READ_OPTION_SUBJECT_BODY);
					}
			        doReadMail(localArrayList);
				}
			}
		});
		
		final Button writeMail = (Button) this.findViewById(R.id.writeMail);
		writeMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!commandDone) {
					startDialog();
					return;
				}				
				commandDone = false;
				
				if (!isSetting) {
					settingNotice();
				} else {
					command = Constants.COMMAND_WRITE;
					subCommand = Constants.SUBCOMMAND_TO;
					ttsAndMicrophone(Constants.COMMAND_TO_GREETING);
				}
			}
			
		});
		
		final Button settings = (Button) this.findViewById(R.id.settings);
		settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!commandDone) {
					startDialog();
					return;
				}				
				commandDone = false;
				
				startSettings();
				isSetting = true;
			}
		});
		
		final Button syncMail = (Button) this.findViewById(R.id.syncMail);
		syncMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!commandDone) {
					startDialog();
					return;
				}				
				commandDone = false;
				
				isSyncMail = true;
				subCommand = Constants.COMMAND_INIT;
				ArrayList<String> localArrayList = new ArrayList<String>();
				localArrayList.add(lastReadType);
				doReadMail(localArrayList);
			}
		});
	}

	public void startSettings() {
	    // Do something in response to button
		Intent ttsIntent = new Intent(this, SettingActivity.class);
		
		startActivity(ttsIntent);
	}

	public void startDebugging() {
	    // Do something in response to button
		Intent debugIntent = new Intent(this, DebuggingActivity.class);
		debugIntent.putExtra("command_list", recognizerResult);
		
		startActivity(debugIntent);
	}
	
	public void initRecognizer() {	
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);  
	    intent.putExtra(
	    	RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
	        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);  
//	    intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(50000000));
	}

	public void startRecognizer(int ms) {
		if (ms > 0) {
			SystemClock.sleep(ms);
		}
		
		if (mpInputRetry < maxMpInputRetry) {
			handler.postDelayed(checkRecognizer, 30000);
		}
	    startActivityForResult(intent, VOICE_RECOGNITION); 
	}

	private Runnable checkRecognizer = new Runnable() {
	    public void run() {	
	    	mpInputRetry++;
			ttsAndPlayEarcon("money");
	    }
	};
	
	private void initTTS() {		
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
		
		tts = new TextToSpeech(this, this);
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override
			public synchronized void onDone(String utteranceId) {
				if (commandHelp) {
					commandHelp = false;
					return;
				}
				
				if (microphoneOn) {
					startRecognizer(0);
					microphoneOn = false;
				}
				
	            switch (command) {
	            case Constants.COMMAND_WRITE : 

	            	break;
	            case Constants.COMMAND_READ:
					if (Constants.SUBCOMMAND_RETRIEVE.equals(subCommand)) {
						switch (readMode) {
						case Constants.READ_OPTION_SUBJECT_ONLY:
							if (ttsCount == Constants.MAIL_PER_PAGE) {
								ttsCount = 0;
								ttsAndPlayEarcon("beethoven");
							} else {
								ttsCount++;
							}
							break;
						case Constants.READ_OPTION_SUBJECT_BODY:
							if (readBodyDone) {
								if (ttsCount == Constants.MAIL_PER_PAGE) {
									ttsCount = 0;
									waitBodyCommand = true;
									ttsAndPlayEarcon("beethoven");
								} else {
									if (!waitBodyCommand) {
										ttsCount++;
										readMessageBody();
									}
								}
							} else {
								ttsCount = 0;
								ttsAndPlayEarcon("beethoven");
							}
							break;
						}
					}
	            case Constants.COMMAND_SETTING:
	            	break;
	            case Constants.COMMAND_STOP:
	            	break;	
	            default :								// INIT
	            	System.out.println("*** ERROR96 ");
	            	break;
	            }
			}

			@Override
			public void onStart(String utteranceId) {
//				System.out.println("onStart ");
			}

			@Override
			public void onError(String arg0) {
				// TODO Auto-generated method stub
				System.out.println("onError");				
			}
		});
		
		tts.addEarcon("money", "com.timebyte.vm1", R.raw.money);
		tts.addEarcon("beethoven", "com.timebyte.vm1", R.raw.beethoven);
		tts.addEarcon("jetsons", "com.timebyte.vm1", R.raw.jetsons);
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
		ttsNoMicrophone(Constants.COMMAND_READ_SUBJECT_BODY);
		commandDone = true;
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
    	super.onActivityResult(requestCode, resultCode, data);
    	if (handler != null) {
    		handler.removeCallbacks(checkRecognizer);
    	}
    	mpInputRetry = 0;
    	
        if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK)
        {  
            ArrayList<String> matches = data.getStringArrayListExtra
            		(RecognizerIntent.EXTRA_RESULTS); 
            recognizerResult.add(matches.toString());
            
            switch (command) {
            case Constants.COMMAND_WRITE : 
            	doWriteMail(matches);
            	break;
            case Constants.COMMAND_READ:
            	doReadMail(matches);
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
        	System.out.println("10 *** No Match ");
//        	ttsAndPlayEarcon("money");
        }
    }    

    public void commandRecord(String type) {
    	command = Constants.COMMAND_COMMAND_RECORD;
    	microphoneOn = true;
    	commandType = type;
    	
        switch (type) {
        case Constants.ANSWER_CONTINUE : 
        	ttsAndMicrophone(Constants.COMMAND_COMMAND1_GREETING);
        	break;
        case Constants.ANSWER_STOP :
        	ttsAndMicrophone(Constants.COMMAND_COMMAND2_GREETING);
        	break;
        case Constants.ANSWER_SKIP :
        	ttsAndMicrophone(Constants.COMMAND_COMMAND3_GREETING);
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
    	commandMap.put("1", Constants.ANSWER_CONTINUE);
    	commandMap.put("2", Constants.ANSWER_STOP);
    	commandMap.put("3", Constants.ANSWER_SKIP);
    }
    
    String FILENAME = "voiceCommand";
    private void getVoiceCommand() {
    	initCommandMap();
    			
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");

		File file = new File(folder, FILENAME);

		//Read text from file
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
					commandMap.put(key,  value);
				}
			}
		    		    
		    br.close();
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
			settingNotice();
			e.printStackTrace();
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
    	commandDone = false;
		microphoneOn = true;
		tts.speak(msg, TextToSpeech.QUEUE_ADD, map);
    }
    
    protected void ttsNoMicrophone(String msg) {
    	commandDone = false;
		microphoneOn = false;
		tts.speak(msg, TextToSpeech.QUEUE_ADD, map);
    }
    
    protected void ttsAndPlayEarcon(String msg) {
    	endDialog();
    	if (handler != null) {
    		handler.removeCallbacks(checkRecognizer);
    	}
    	
		microphoneOn = true;
		ttsCount = 0;
		tts.playEarcon(msg, TextToSpeech.QUEUE_ADD, map);
    }
    
    private void startDialog() {
		processDialog = new ProgressDialog(this);
		processDialog.setMessage("Process command, please wait...");
		processDialog.setIndeterminate(false);
		processDialog.setCancelable(false);
		processDialog.show();
    }
    
    protected void endDialog() {
    	if (processDialog != null) {
    		processDialog.dismiss();
    	}
    	
    	commandDone = true;
    }
}
