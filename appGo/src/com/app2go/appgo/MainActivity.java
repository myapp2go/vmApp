package com.app2go.appgo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;

import android.util.Log;

public class MainActivity extends Activity implements OnClickListener, OnInitListener {

	private TextView mText;
	private SpeechRecognizer sr;

	protected TextToSpeech tts;
	protected Intent intent;
	HashMap<String, String> mapTTS = new HashMap<String, String>();
	HashMap<String, String> mapTTSPhone = new HashMap<String, String>();
	HashMap<String, String> mapEarcon = new HashMap<String, String>();
	private static final String mapTTSID = "mapTTSID";
	private static final String mapTTSPhoneID = "mapTTSPhoneID";
	private static final String mapEarconID = "mapEarconID";

	private static String speechDone = null;
	
	private String messageQueue = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initTTS();
		
		Button speakButton = (Button) findViewById(R.id.readMail);
		mText = (TextView) findViewById(R.id.title);
		speakButton.setOnClickListener(this);
		
		sr = SpeechRecognizer.createSpeechRecognizer(this);
		sr.setRecognitionListener(new listener());
	}

	class listener implements RecognitionListener {
		public void onReadyForSpeech(Bundle params) {
			System.out.println("001 onReadyForSpeech");
		}

		public void onBeginningOfSpeech() {
			System.out.println("02 onBeginningOfSpeech");
		}

		public void onRmsChanged(float rmsdB) {
			System.out.println("03 onRmsChanged");
		}

		public void onBufferReceived(byte[] buffer) {
			System.out.println("04 nonBufferReceived");
		}

		public void onEndOfSpeech() {
			System.out.println("05 onEndofSpeech");
		}

		public void onError(int error) {
			System.out.println("06 errorNum " + error);
			mText.setText("error " + error);
		}

		public void onResults(Bundle results) {
			String str = new String();
			System.out.println("07 onResults " + results);
			ArrayList data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			for (int i = 0; i < data.size(); i++) {
				System.out.println("result " + data.get(i));
				str += data.get(i);
			}
			mText.setText("results: " + String.valueOf(data.size()));
		}

		public void onPartialResults(Bundle partialResults) {
			System.out.println("08 onPartialResults");
		}

		public void onEvent(int eventType, Bundle params) {
			System.out.println("09 onEvent " + eventType);
		}
	}

	public void onClick(View v) {
		if (v.getId() == R.id.readMail) {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
					"voice.recognition.test");

			intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
			sr.startListening(intent);
			Log.i("111111", "11111111");
		}
	}

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
/*
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
	            */
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
	public void onInit(int status) {
		// TODO Auto-generated method stub
		ttsNoMicrophone(Constants.COMMAND_READ_SUBJECT_BODY);
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
    
}
