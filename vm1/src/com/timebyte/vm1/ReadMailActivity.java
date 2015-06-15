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
	private int maxLen = 200;
	private int maxRetry = 2;	
	private int retry = 0;
	private int bodyReaded = 0;
	
	protected void doReadMail(ArrayList<String> matches) {
//		increment = sharedPreferences.getInt("increment", 0);
		System.out.println("doReadMail " + command + " * "  + subCommand + " * "  + readMode + " * " + " * " + mailCount + " * " + ttsCount + " * " + microphoneOn);
		String answer = Constants.COMMAND_NONE;
		
		switch (subCommand) {
		case Constants.COMMAND_INIT :
			answer = matchReadCommand(matches);
			switch (answer) {
			case Constants.ANSWER_1 :
				readMode = Constants.READ_OPTION_SUBJECT_ONLY;
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences);
				break;
			case Constants.ANSWER_2 :
				readMode = Constants.READ_OPTION_SUBJECT_BODY;
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences);
				break;
			case Constants.COMMAND_NONE :
				break;	
			}
			break;
		case Constants.SUBCOMMAND_RETRIEVE :
			answer = matchReadCommand(matches);
			switch (answer) {
			case Constants.ANSWER_1 :
				readMessage();
				break;
			case Constants.ANSWER_2 :
				mailCount = 0;
				break;
			case Constants.COMMAND_NONE :
				break;	
			}
			break;			
		case Constants.SUBCOMMAND_MORE_SKIP :
			System.out.println("%%%%%%%%%%%%%%%%%%%%%66666%9999 doReadMail Number ");
			String cmd = matchReadMode(matches);
			System.out.println("%%%%%%%%%%%%%%%%%%%%%66666%9999 doReadMail matchReadMode " + cmd + " * " + subCommand);
			switch (cmd) {
			case Constants.SUBCOMMAND_MORE :
				microphoneOn = false;
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				readMessageBody();
				break;
			case Constants.SUBCOMMAND_SKIP :
				microphoneOn = false;
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				mailCount++;
				readMessageBody();
				break;
			case Constants.COMMAND_NONE :
				if (retry < maxRetry) {
					microphoneOn = true;	
					retry++;
					tts.speak(Constants.COMMAND_READ_BODY_MORE_SKIP, TextToSpeech.QUEUE_ADD, map);
				} else {
					retry = 0;
				}
				break;
			}
			break;
		case Constants.COMMAND_NEXT :
			microphoneOn = false;
			mailCount--;
			readMessage();
			break;
		case Constants.COMMAND_STOP :
			mailCount = 0;
//			commandReset();
//    		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
//    		startRecognizer();
			break;
		}
	}
	
	public void setMessages(Message[] messages) {
		mailSubject = new String[messages.length + 1];
		mailBody = new String[messages.length + 1];
		mailSubject[0] = Constants.COMMAND_ADVERTISE_SUBJECT;
		mailBody[0] = Constants.COMMAND_ADVERTISE_BODY;
		
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

	private String matchReadCommand(ArrayList<String> matches) {
		// TODO Auto-generated method stub
        boolean found = false;
        String ans = Constants.COMMAND_NONE;
		
        for (int i = 0; !found && (i < matches.size()); i++) {
        	switch (matches.get(i)) {
        	case Constants.ANSWER_1 :
        		found = true;
        		ans = Constants.ANSWER_1;
        		break;
        	case Constants.ANSWER_2 :
        		found = true;
        		ans = Constants.ANSWER_2;
        		break;	
        	}
        }
        
        return ans;
	}
	
	public void readMailDone() {
		microphoneOn = false;
		if (Constants.READ_OPTION_SUBJECT_BODY.equals(readMode)) {
			readMessageBody();
		} else {
			readMessage();
		}
	}

	protected void readMessageBody() {
		int count = mailCount;
		
		String body = mailBody[count];
		if (body.length() > maxLen) {
			int ind = body.indexOf(" ", (bodyReaded+maxLen));
			body = mailBody[count].substring(bodyReaded, ind);
			bodyReaded = ind;
			readBodyDone = false;
		} else {
			mailCount++;
			readBodyDone = true;
		}
		
		tts.speak("mail number" + (count + 1)  + " " + mailSubject[count] + body, TextToSpeech.QUEUE_ADD, map);						
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

//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
			    
			tts.speak("mail number" + (i + 1)  + " " + mailSubject[i], TextToSpeech.QUEUE_ADD, map);
		}
	}
	
	private String matchReadMode(ArrayList<String> matches) {
		boolean found = false;
		String sub = Constants.COMMAND_NONE;
		
		for (int i = 0; !found && (i < matches.size()); i++) {
			switch (matches.get(i)) {
			case Constants.ANSWER_1:
				found = true;
				sub = Constants.SUBCOMMAND_MORE;
				break;
			case Constants.SBCOMMAND_UP:
				found = true;
				sub = Constants.SBCOMMAND_UP;
				break;
			case Constants.SUBCOMMAND_ADD:
				found = true;
				sub = Constants.SUBCOMMAND_ADD;
			case Constants.ANSWER_2:
				found = true;
				sub = Constants.SUBCOMMAND_SKIP;
				break;
			}
		}
		
		return sub;
	}
}
