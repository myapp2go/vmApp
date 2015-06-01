package com.timebyte.vm1;

import java.util.ArrayList;

import android.speech.tts.TextToSpeech;
import android.widget.TextView;
public abstract class ReadMailActivity extends MainActivity {

	protected void doReadMail(ArrayList<String> matches) {
		switch (subCommand) {
		case Constants.COMMAND_INIT :
			matchReadCommand(matches);
			String myEmail = "tapaulchen@gmail.com";
			String myPassword = "Tanan1559";
			switch (answer) {
			case Constants.ANSWER_1 :
				new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences, myEmail, myPassword);
				break;
			case Constants.ANSWER_2 :
				new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences, myEmail, myPassword);
				break;
			case Constants.COMMAND_NONE :
				break;	
			}
			break;
		case Constants.SUBCOMMAND_RETRIEVE :
			System.out.println("%%%%%%%%%%%%%%%%%%%%%66666%9999 doReadMail Number ");
//			String myEmail = ((TextView) findViewById(R.id.myEmail)).getText().toString();
//			String myPassword = ((TextView) findViewById(R.id.myPassword)).getText().toString();
// PC522			
//			subCommand = Constants.COMMAND_STOP;
//			new ReadMailTask(ReadMailActivity.this).execute(myEmail, myPassword);	
			break;
		case Constants.COMMAND_NEXT :
//			readMessage(ttsCount+10, 100);
			break;
		case Constants.COMMAND_STOP :
//			commandReset();
    		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
//    		startRecognizer();
			break;
		}
	}

	private void matchReadCommand(ArrayList<String> matches) {
		// TODO Auto-generated method stub
        boolean found = false;
        answer = Constants.COMMAND_NONE;
		
        for (int i = 0; !found && (i < matches.size()); i++) {
        	switch (matches.get(i)) {
        	case Constants.ANSWER_1 :
        		found = true;
        		answer = Constants.ANSWER_1;
        		break;
        	case Constants.ANSWER_2 :
        		found = true;
        		answer = Constants.ANSWER_2;
        		break;	
        	}
        }
	}
}
