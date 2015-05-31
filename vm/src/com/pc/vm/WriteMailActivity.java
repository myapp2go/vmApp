package com.pc.vm;

import java.util.ArrayList;

import android.speech.tts.TextToSpeech;

public abstract class WriteMailActivity extends ReadMailActivity {

    protected boolean checkWriteMode = false;
    private String mode = Constants.COMMAND_INIT;
    
	protected void doWriteMail(ArrayList<String> matches) {
	      System.out.println("*** doWriteMail " + command + " * " + subCommand + " * " + answer + " * " + checkWriteMode + " * " + checkYesNo);

		String myEmail = "tapaulchen@gmail.com";
		String myPassword = "Tsnsn1559";

		if (checkYesNo) {
        	matchYesNo(matches);
        }
		if (checkWriteMode) {
        	matchWriteMode(matches);
        }

		 System.out.println("ANSWER " + answer + " SUB " + subCommand);
		 
		switch (subCommand) {
		case Constants.COMMAND_INIT :
    		subCommand = Constants.SUBCOMMAND_TO;
    		tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
    		startRecognizer();
			break;
		case Constants.SUBCOMMAND_TO :
			switch (answer) {
			case Constants.COMMAND_INIT :	
				String name = matches.get(0);
				System.out.println("GGGGGGGGGGGGGGGGG " + name);
				mailTo = matchName(name);
				System.out.println("FFFFFFFFFFFFFFFFFFFF " + mailTo);
				mailTo = "paultchan@yahoo.com";
				if (mailTo != null) {
					checkYesNo = true;
					tts.speak(Constants.COMMAND_ECHO_HEADER_GREETING + mailTo + Constants.COMMAND_ECHO_FOOTER_GREETING, TextToSpeech.QUEUE_ADD, map);
					startRecognizer();
				} else {
					checkYesNo = false;
		    		tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
		    		startRecognizer();
				}	
				break;
			case Constants.COMMAND_YES :
				System.out.println("YESGGGGGGGGGGGGGGGGG " + mailTo);
				checkYesNo = false;
				subCommand = Constants.SUBCOMMAND_SUBJECFT;
				tts.speak(Constants.COMMAND_SUBJECT_GREETING, TextToSpeech.QUEUE_ADD, map);
				startRecognizer();				
				break;
			case Constants.COMMAND_NO :	
				System.out.println("NOGGGGGGGGGGGGGGGGG " + mailTo);
				subCommand = Constants.SUBCOMMAND_TO;
				checkYesNo = false;
				answer = Constants.COMMAND_INIT;
	    		tts.speak(Constants.COMMAND_TO_GREETING, TextToSpeech.QUEUE_ADD, map);
	    		startRecognizer();
				break;
			case Constants.COMMAND_NONE :	
				System.out.println("NONEGGGGGGGGGGGGGGGGG " + mailTo);
				tts.speak(Constants.COMMAND_ECHO_HEADER_GREETING + mailTo + Constants.COMMAND_ECHO_FOOTER_GREETING, TextToSpeech.QUEUE_ADD, map);
				checkYesNo = true;
				startRecognizer();
				break;				
			}
			break;
		case Constants.SUBCOMMAND_SUBJECFT :
			mailSubject = matches.get(0);
			System.out.println("*************SUBJECT " + mailSubject);
    		subCommand = Constants.SUBCOMMAND_BODY;
    		tts.speak(Constants.COMMAND_BODY_GREETING, TextToSpeech.QUEUE_ADD, map);
    		startRecognizer();			
    		break;
		case Constants.SUBCOMMAND_BODY :
			mailBody = matches.get(0);
    		subCommand = Constants.SUBCOMMAND_DONE;
    		checkWriteMode = true;
    		tts.speak(Constants.COMMAND_DONE_GREETING, TextToSpeech.QUEUE_ADD, map);
    		startRecognizer();	
			break;
		case Constants.SUBCOMMAND_DONE :
			System.out.println("*************PCMODE " + mode);
			switch (mode) {
			case Constants.SUBCOMMAND_SEND :
				System.out.println("*************PCMODESEND " + mode);
				new WriteMailTask(WriteMailActivity.this).execute(myEmail, myPassword, mailTo, mailSubject, mailBody);
				break;
			}	
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

	private void matchWriteMode(ArrayList<String> matches) {
        boolean found = false;

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
// PC 522        
        mode = Constants.SUBCOMMAND_SEND;
	}
}
