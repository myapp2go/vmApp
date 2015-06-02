package com.timebyte.vm1;

import java.util.ArrayList;
import java.util.HashMap;

import android.speech.tts.TextToSpeech;

public abstract class WriteMailActivity extends ReadMailActivity {
	
	protected HashMap<String, String> contacts = new HashMap<String, String>();

    protected String mailTo ="";
    protected String mailSubject = "";
    protected String mailBody = "";
 
    protected boolean checkYesNo = false;
    
	protected void doWriteMail(ArrayList<String> matches) {
		switch (subCommand) {
		case Constants.COMMAND_INIT :
    		subCommand = Constants.SUBCOMMAND_TO;
    		tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
 //   		startRecognizer();
			break;
		case Constants.SUBCOMMAND_TO :
			String name = matches.get(0);
			System.out.println("GGGGGGGGGGGGGGGGG " + name);
			mailTo = matchName(name);
			System.out.println("FFFFFFFFFFFFFFFFFFFF " + mailTo);
			if (mailTo != null) {
				checkYesNo = true;
				subCommand = Constants.SUBCOMMAND_VERIFY_TO;
				speanOn = true;
				tts.speak(Constants.COMMAND_ECHO_HEADER_GREETING + mailTo + Constants.COMMAND_ECHO_FOOTER_GREETING, TextToSpeech.QUEUE_ADD, map);
//				startRecognizer();
			} else {
				checkYesNo = false;
	    		tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
//	    		startRecognizer();
			}
			break;
		case Constants.SUBCOMMAND_VERIFY_TO :	
			String answer = matchYesNo(matches);
			switch (answer) {
			case Constants.ANSWER_YES :
//				System.out.println("YESGGGGGGGGGGGGGGGGG " + mailTo);
//				checkYesNo = false;
				subCommand = Constants.SUBCOMMAND_SUBJECFT;
				speanOn = true;
				tts.speak(Constants.COMMAND_SUBJECT_GREETING, TextToSpeech.QUEUE_ADD, map);
//				startRecognizer();				
				break;
			case Constants.ANSWER_NO :	
//				System.out.println("NOGGGGGGGGGGGGGGGGG " + mailTo);
				subCommand = Constants.SUBCOMMAND_TO;
//				checkYesNo = false;
				answer = Constants.COMMAND_INIT;
	    		tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
//	    		startRecognizer();
				break;
			case Constants.COMMAND_NONE :	
//				System.out.println("NONEGGGGGGGGGGGGGGGGG " + mailTo);
//				tts.speak(Constants.COMMAND_ECHO_HEADER_GREETING + mailTo + Constants.COMMAND_ECHO_FOOTER_GREETING, TextToSpeech.QUEUE_ADD, map);
//				checkYesNo = true;
//				startRecognizer();
				break;				
			}
			break;
		case Constants.SUBCOMMAND_SUBJECFT :
			mailSubject = matches.get(0);
			System.out.println("*************SUBJECT " + mailSubject);
    		subCommand = Constants.SUBCOMMAND_BODY;
    		tts.speak(Constants.COMMAND_BODY_GREETING, TextToSpeech.QUEUE_ADD, map);
    		break;
		case Constants.SUBCOMMAND_BODY :
			mailBody = matches.get(0);
    		subCommand = Constants.SUBCOMMAND_DONE;
    		tts.speak(Constants.COMMAND_DONE_GREETING, TextToSpeech.QUEUE_ADD, map);
			break;
		case Constants.SUBCOMMAND_DONE :
			System.out.println("*************PCMODE ");
			String mode = matchWriteMode(matches);
			switch (mode) {
			case Constants.SUBCOMMAND_SEND :
				System.out.println("*************PCMODESEND " + mode);
				new WriteMailTask(WriteMailActivity.this).execute(sharedPreferences, mailTo, mailSubject, mailBody);
				break;
			}	
			
			break;
		}
	}

	private String matchName(String mailTo) {
		// TODO Auto-generated method stub
		String toLow = mailTo.toLowerCase();
//		System.out.println("*************matchName " + toLow + " SIZE " + contacts);
		String ret = contacts.get(toLow);
		System.out.println("*************matchName1 " + ret);
		return ret;
//		return contacts.get(mailTo);
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

		System.out.println("matchYesNoanswer " + answer);
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

