package com.timebyte.vm1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

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
	
	ArrayList<String> recognizerResult = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initTTS();
		
		initRecognizer();
		
		final Button readMail = (Button) this.findViewById(R.id.readMail);
		readMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!isSetting) {
					settingNotice();
				} else {
					tts.speak(Constants.COMMAND_READ_SUBJECT, TextToSpeech.QUEUE_ADD, map);
					commandHelp = true;
					
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
			        /* 
					microphoneOn = true;
					tts.speak(Constants.COMMAND_READ_GREETING, TextToSpeech.QUEUE_ADD, map);
					*/
				}
			}
		});
		
		final Button readBodyMail = (Button) this.findViewById(R.id.readBodyMail);
		readBodyMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!isSetting) {
					settingNotice();
				} else {
					tts.speak(Constants.COMMAND_READ_SUBJECT_BODY, TextToSpeech.QUEUE_ADD, map);
					commandHelp = true;
					
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
/*
		final Button skipMail = (Button) this.findViewById(R.id.skipMail);
		skipMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tts.speak(Constants.COMMAND_SKIP_GREETING, TextToSpeech.QUEUE_FLUSH, map);
			}
		});
*/		
		final Button writeMail = (Button) this.findViewById(R.id.writeMail);
		writeMail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!isSetting) {
					settingNotice();
				} else {
					command = Constants.COMMAND_WRITE;
					subCommand = Constants.SUBCOMMAND_TO;
				
					microphoneOn = true;
					tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
				}
			}
		});
		
		final Button settings = (Button) this.findViewById(R.id.settings);
		settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startSettings();
				isSetting = true;
			}
		});
		
		final Button debugging = (Button) this.findViewById(R.id.debugging);
		debugging.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startDebugging();
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
	    startActivityForResult(intent, VOICE_RECOGNITION); 
	}
	
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
//								tts.speak(Constants.COMMAND_READ_ACTION, TextToSpeech.QUEUE_ADD, map);
								tts.playEarcon("beethoven", TextToSpeech.QUEUE_ADD, map);
								microphoneOn = true;
							} else {
								ttsCount++;
							}
							break;
						case Constants.READ_OPTION_SUBJECT_BODY:
							if (readBodyDone) {
								if (ttsCount == Constants.MAIL_PER_PAGE) {
									ttsCount = 0;
									waitBodyCommand = true;
									microphoneOn = true;
//									tts.speak(Constants.COMMAND_READ_ACTION, TextToSpeech.QUEUE_ADD, map);
									tts.playEarcon("beethoven", TextToSpeech.QUEUE_ADD, map);
								} else {
									if (!waitBodyCommand) {
										ttsCount++;
										readMessageBody();
									}
								}
							} else {
								ttsCount = 0;
								subCommand = Constants.SUBCOMMAND_MORE_SKIP;
								microphoneOn = true;								
//								tts.speak(Constants.COMMAND_READ_BODY_MORE_SKIP, TextToSpeech.QUEUE_ADD, map);
								tts.playEarcon("beethoven", TextToSpeech.QUEUE_ADD, map);
							}
							break;
						}
					}
	            case Constants.COMMAND_SETTING:
	            	break;
	            case Constants.COMMAND_STOP:
	            	break;	
	            default :								// INIT
	            	System.out.println("*** ERROR ");
	            	break;
	            }
			}

			@Override
			public void onStart(String utteranceId) {
//				System.out.println("onStart ");
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
		
		tts.addEarcon("money", "com.timebyte.vm1", R.raw.money);
		tts.addEarcon("beethoven", "com.timebyte.vm1", R.raw.beethoven);
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
            
            recognizerResult.add(matches.toString());
            
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
