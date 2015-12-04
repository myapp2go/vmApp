package com.pc.vm;

import java.util.ArrayList;
import java.util.HashMap;

import android.speech.tts.TextToSpeech;

public class WriteMailActivity extends ReadMailActivity {
	
    protected String mailTo ="";
    protected String mailSubject = "";
    protected String mailBody = "";
 
//    protected boolean checkYesNo = false;
    
	protected void doWriteMail(ArrayList<String> matches) {
		switch (subCommand) {
		case Constants.COMMAND_INIT :
    		subCommand = Constants.SUBCOMMAND_TO;
    		ttsAndMicrophone(Constants.COMMAND_TO_GREETING);
			break;
		case Constants.SUBCOMMAND_TO :
			String name = matches.get(0);
//			System.out.println("DUMP " + matches);
			mailTo = matchName(name);
			if (mailTo != null) {
//				checkYesNo = true;
				subCommand = Constants.SUBCOMMAND_VERIFY_TO;
				ttsAndMicrophone(Constants.COMMAND_ECHO_HEADER_GREETING + mailTo + Constants.COMMAND_ECHO_FOOTER_GREETING);
			} else {
//				checkYesNo = false;
				ttsAndMicrophone(Constants.COMMAND_TO_GREETING);
			}
			break;
		case Constants.SUBCOMMAND_VERIFY_TO :	
			String answer = matchYesNo(matches);
			switch (answer) {
			case Constants.ANSWER_YES :
				subCommand = Constants.SUBCOMMAND_SUBJECFT;
				ttsAndMicrophone(Constants.COMMAND_SUBJECT_GREETING);
				break;
			case Constants.ANSWER_NO :	
				subCommand = Constants.SUBCOMMAND_TO;
				answer = Constants.COMMAND_INIT;
				ttsAndMicrophone(Constants.COMMAND_TO_GREETING);
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
    		ttsAndMicrophone(Constants.COMMAND_BODY_GREETING);
    		break;
		case Constants.SUBCOMMAND_BODY :
			mailBody = matches.get(0);
    		subCommand = Constants.SUBCOMMAND_DONE;
    		ttsAndMicrophone(Constants.COMMAND_DONE_GREETING);
			break;
		case Constants.SUBCOMMAND_DONE :
			String mode = matchWriteMode(matches);
			switch (mode) {
			case Constants.SUBCOMMAND_SEND :
				new WriteMailTask(WriteMailActivity.this).execute(false, sharedPreferences, mailTo, mailSubject, mailBody);
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
        String ans = Constants.COMMAND_NONE;
		System.out.println("matchYesNo " );
		
        for (int i = 0; !found && (i < matches.size()); i++) {
    		System.out.println("matchYesNoanswerRECCCC " + matches.get(i));
        	switch (matches.get(i)) {
        	case Constants.ANSWER_YES :
        		found = true;
        		ans = Constants.ANSWER_YES;
        		break;
        	case Constants.ANSWER_NO :
        		found = true;
        		ans = Constants.ANSWER_NO;
        		break;	
        	}
        }

		return ans;
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

	@Override
	protected void doDebugMail(String myEmail, String myPassword,
			String mailTo, String mailSubject, String mailBody) {
		// TODO Auto-generated method stub
		new WriteMailTask(WriteMailActivity.this).execute(true, sharedPreferences, mailTo, mailSubject, mailBody);

	}
}

