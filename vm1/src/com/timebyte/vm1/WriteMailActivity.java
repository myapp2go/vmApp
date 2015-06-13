package com.timebyte.vm1;

import java.util.ArrayList;
import java.util.HashMap;

import android.speech.tts.TextToSpeech;

public class WriteMailActivity extends ReadMailActivity {
	
    protected String mailTo ="";
    protected String mailSubject = "";
    protected String mailBody = "";
 
    protected boolean checkYesNo = false;
    
	protected void doWriteMail(ArrayList<String> matches) {
		switch (subCommand) {
		case Constants.COMMAND_INIT :
    		subCommand = Constants.SUBCOMMAND_TO;
    		tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
			break;
		case Constants.SUBCOMMAND_TO :
			String name = matches.get(0);
			System.out.println("DUMP " + matches);
			mailTo = matchName(name);
			if (mailTo != null) {
				checkYesNo = true;
				subCommand = Constants.SUBCOMMAND_VERIFY_TO;
				speakOn = true;
				tts.speak(Constants.COMMAND_ECHO_HEADER_GREETING + mailTo + Constants.COMMAND_ECHO_FOOTER_GREETING, TextToSpeech.QUEUE_ADD, map);
			} else {
				checkYesNo = false;
	    		tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
			}
			break;
		case Constants.SUBCOMMAND_VERIFY_TO :	
			String answer = matchYesNo(matches);
			switch (answer) {
			case Constants.ANSWER_YES :
				subCommand = Constants.SUBCOMMAND_SUBJECFT;
				speakOn = true;
				tts.speak(Constants.COMMAND_SUBJECT_GREETING, TextToSpeech.QUEUE_ADD, map);				
				break;
			case Constants.ANSWER_NO :	
				subCommand = Constants.SUBCOMMAND_TO;
				answer = Constants.COMMAND_INIT;
	    		tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
				break;
			case Constants.COMMAND_NONE :	
//				System.out.println("NONEGGGGGGGGGGGGGGGGG " + mailTo);
//				tts.speak(Constants.COMMAND_ECHO_HEADER_GREETING + mailTo + Constants.COMMAND_ECHO_FOOTER_GREETING, TextToSpeech.QUEUE_ADD, map);
//				checkYesNo = true;
				break;				
			}
			break;
		case Constants.SUBCOMMAND_SUBJECFT :
			mailSubject = matches.get(0);
    		subCommand = Constants.SUBCOMMAND_BODY;
    		tts.speak(Constants.COMMAND_BODY_GREETING, TextToSpeech.QUEUE_ADD, map);
    		break;
		case Constants.SUBCOMMAND_BODY :
			mailBody = matches.get(0);
    		subCommand = Constants.SUBCOMMAND_DONE;
    		tts.speak(Constants.COMMAND_DONE_GREETING, TextToSpeech.QUEUE_ADD, map);
			break;
		case Constants.SUBCOMMAND_DONE :
			String mode = matchWriteMode(matches);
			switch (mode) {
			case Constants.SUBCOMMAND_SEND :
				new WriteMailTask(WriteMailActivity.this).execute(sharedPreferences, mailTo, mailSubject, mailBody);
				break;
			}	
			
			break;
		}
	}

	private String matchName(String name) {
		// TODO Auto-generated method stub
		String toLow = name.toLowerCase();
		String ret = contacts.get(toLow);
		return ret;
	}
	
    protected String matchYesNo(ArrayList<String> matches) {
        boolean found = false;
        answer = Constants.COMMAND_NONE;
		System.out.println("matchYesNo " );
		
        for (int i = 0; !found && (i < matches.size()); i++) {
    		System.out.println("matchYesNoanswerRECCCC " + matches.get(i));
        	switch (matches.get(i)) {
        	case Constants.ANSWER_YES :
        		found = true;
        		answer = Constants.ANSWER_YES;
        		break;
        	case Constants.ANSWER_NO :
        		found = true;
        		answer = Constants.ANSWER_NO;
        		break;	
        	}
        }

		return answer;
	}
    
	private String matchWriteMode(ArrayList<String> matches) {
        boolean found = false;
        String mode = Constants.COMMAND_NONE;

        for (int i = 0; !found && (i < matches.size()); i++) {
        	switch (matches.get(i)) {
        	case Constants.SUBCOMMAND_SEND :
        		mode = Constants.SUBCOMMAND_SEND;
        		found = true;
        		break;
        	case Constants.SUBCOMMAND_ADD :
        		mode = Constants.SUBCOMMAND_ADD;
        		found = true;
        		break;
        	}
		}

        return mode;
	}
}

