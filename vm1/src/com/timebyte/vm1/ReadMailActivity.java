package com.timebyte.vm1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import android.speech.tts.TextToSpeech;

public abstract class ReadMailActivity extends SharedPreferencesActivity {

	private String[] mailSubject;
	private String[] mailBody;
	private boolean subjectOnly = true;
	protected int mailCount = 0;
	
	protected void doReadMail(ArrayList<String> matches) {
//		increment = sharedPreferences.getInt("increment", 0);
		String cmd = matchReadMode(matches);
		
		switch (subCommand) {
		case Constants.COMMAND_INIT :
			matchReadCommand(matches);
			switch (answer) {
			case Constants.ANSWER_1 :
				subjectOnly = true;
				new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences);
				break;
			case Constants.ANSWER_2 :
				subjectOnly = false;
				new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences);
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
			speanOn = false;
			readMessage();
			break;
		case Constants.COMMAND_STOP :
//			commandReset();
    		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
//    		startRecognizer();
			break;
		}
	}
	
	public void setMessages(Message[] messages) {
		mailSubject = new String[messages.length + 1];
		mailBody = new String[messages.length + 1];
		mailSubject[0] = Constants.COMMAND_ADVERTISE_GREETING;

		int len = messages.length;
		for (int i = 1; i <= len; i++) {
			try {
				Message msg = messages[len - i];
				mailSubject[i] = msg.getSubject();

				Object msgContent = msg.getContent();
				if (msgContent instanceof Multipart) {
					Multipart multipart = (Multipart) msgContent;
					boolean found = false;
					
					for (int j = 0; j < multipart.getCount(); j++) {
						BodyPart bodyPart = multipart.getBodyPart(j);
						int pos = bodyPart.getContentType().indexOf("PLAIN");
						if (pos > 0) {
							found = true;
							mailBody[i] = bodyPart.getContent().toString();
						}
					}
					
					if (!found) {
						mailBody[i] = Constants.MAIL_BODY_NOT_SUPPORT;
					}
				} else {
					int pos = msg.getContentType().indexOf("PLAIN");
					if (pos == -1) {
						mailBody[i] = Constants.MAIL_BODY_IS_HTML;
					} else {
						mailBody[i] = msg.getContent().toString();
					}					
				}
			} catch (IOException e) {	
				
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	public void readMailDone() {
		speanOn = false;
		readMessage();
	}

	private void readMessage() {
		int start = mailCount;
		int count = mailCount + Constants.MAIL_PER_PAGE;
		if (count > mailSubject.length) {
			count = mailSubject.length;
		}
		for (int i = start; i < count; i++) {
			mailCount++;
//			Message message = mailSubject[i];
			/*
			System.out.println("----------------------------------");
			System.out.println("Email Number " + (i + 1));
			System.out.println("Subject: " + message.getSubject());
			System.out.println("From: " + message.getFrom()[0]);
			System.out.println("Text: " + message.getContent().toString());
			 */

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
			    
//			tts.speak("mail number :" + (i + 1) + message.getSubject(), TextToSpeech.QUEUE_ADD, null);
			if (subjectOnly) {
				System.out.println("111111 Email Number ");
				tts.speak("mail number" + (i + 1)  + " " + mailSubject[i], TextToSpeech.QUEUE_ADD, map);
			} else {
				System.out.println("222222111111 Email Number ");
				tts.speak("mail number" + (i + 1)  + " " + mailSubject[i] + mailBody[i], TextToSpeech.QUEUE_ADD, map);				
			}
		}
	}
	
	private String matchReadMode(ArrayList<String> matches) {
		boolean found = false;
		
		for (int i = 0; !found && (i < matches.size()); i++) {
			switch (matches.get(i)) {
			case Constants.SBCOMMAND_NEXT:
				found = true;
				subCommand = Constants.SBCOMMAND_NEXT;
				break;
			case Constants.SBCOMMAND_UP:
				found = true;
				subCommand = Constants.SBCOMMAND_UP;
				break;
			case Constants.SUBCOMMAND_ADD:
				found = true;
				subCommand = Constants.SUBCOMMAND_ADD;
				break;
			case Constants.COMMAND_STOP:
				found = true;
				subCommand = Constants.COMMAND_STOP;
				break;
			}
		}
		
		return subCommand;
	}
}
