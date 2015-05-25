package com.pc.vm;

import android.speech.tts.TextToSpeech;

public abstract class WriteMailActivity extends ReadMailActivity {

	protected void doWriteMail() {
		String myEmail = "tapaulchen@gmail.com";
		String myPassword = "Tsnsn1559";
		
		switch (subCommand) {
		case Constants.COMMAND_NONE :
    		subCommand = Constants.SUBCOMMAND_SUBJECFT;
			new WriteMailTask(WriteMailActivity.this).execute(myEmail, myPassword);
//    		tts.speak(Constants.COMMAND_SUBJECT_GREETING, TextToSpeech.QUEUE_ADD, map);
//    		startRecognizer();
			break;
		case Constants.SUBCOMMAND_SUBJECFT :
    		subCommand = Constants.SUBCOMMAND_BODY;
    		tts.speak(Constants.COMMAND_BODY_GREETING, TextToSpeech.QUEUE_ADD, map);
    		startRecognizer();			
    		break;
		case Constants.SUBCOMMAND_BODY :
    		subCommand = Constants.SUBCOMMAND_DONE;
    		tts.speak(Constants.COMMAND_DONE_GREETING, TextToSpeech.QUEUE_ADD, map);
    		startRecognizer();	
			break;
		case Constants.SUBCOMMAND_DONE :

			new WriteMailTask(WriteMailActivity.this).execute(myEmail, myPassword);
			break;
		}
	}
}
