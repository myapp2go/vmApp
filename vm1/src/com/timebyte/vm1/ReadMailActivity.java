package com.timebyte.vm1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import android.speech.tts.TextToSpeech;
import android.widget.TextView;
public abstract class ReadMailActivity extends SharedPreferencesActivity {

	private String[] mailSubject;
	private String[] mailBody;
	private int increment;
	
	protected void doReadMail(ArrayList<String> matches) {
		increment = sharedPreferences.getInt("increment", 0);
		switch (subCommand) {
		case Constants.COMMAND_INIT :
			matchReadCommand(matches);
			switch (answer) {
			case Constants.ANSWER_1 :
				new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences);
				break;
			case Constants.ANSWER_2 :
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
//			readMessage(ttsCount+10, 100);
			break;
		case Constants.COMMAND_STOP :
//			commandReset();
    		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
//    		startRecognizer();
			break;
		}
	}
	
	public void setMessages(Message[] messages) {
		mailSubject = new String[messages.length+1];
		mailBody = new String[messages.length+1];
		mailSubject[0] = Constants.COMMAND_ADVERTISE_GREETING;
		for (int i = 1; i < messages.length; i++) {
			try {
				mailSubject[i] = messages[i].getSubject();
//				String str = mailSubject[i];
				String type = "TEXT/HTML";
//				 if (!(messages[i].getFlags() == null))
//				        System.out.println("FLAG " + messages[i].getSubject());
				try {
//					if (!(messages[i].getContent() instanceof Multipart)) {
//					System.out.println("%%%%%%%%%%%%%%%FLAG " + messages[i].getContent());
					
//						System.out.println("%%%%%%%%%%%%%%%FLAG " + messages[i].getContentType());
//						System.out.println("%%%%%%%%%%%%%%%FLAGINDEX " + messages[i].getContentType().indexOf("HTML"));
						int pos = messages[i].getContentType().indexOf("HTML");
						if (pos == -1) {
//							System.out.println("%%%%%%%%%%%%%%%CONTENT " + messages[i].getContent());
							mailBody[i] = messages[i].getContent().toString();
						} else {
							mailBody[i] = Constants.MAIL_BODY_NOT_SUPPORT;
						}
//					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("^^^^^^^^^^^^^^^^^setMessages*********** " + mailSubject + " " + messages);
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
		int msgLength = mailSubject.length;

		System.out.println("^^^^^^^^^^^^^^^^^ReadMailDone*********** ");
		readMessage(0, msgLength);
	}

	private void readMessage(int count, int msgLength) {
		System.out.println("^^^^^^^^^^^^^^^^^readMessage*********** ");
		
//		for (int i = count; i < msgLength; i++) {
		for (int i = count; (i < count+increment); i++) {
	
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

				tts.speak("mail number" + (i + 1)  + " " + mailSubject[i] + mailBody[i], TextToSpeech.QUEUE_ADD, map);
	
/*			
			Object msgContent = message.getContent();
			String content = "";
	
			if (msgContent instanceof Multipart) {
				Multipart multipart = (Multipart) msgContent;
				System.out.println("BodyPart" + "MultiPartCount: " + multipart.getCount());

				for (int j = 0; j < multipart.getCount(); j++) {
					BodyPart bodyPart = multipart.getBodyPart(j);

					String disposition = bodyPart.getDisposition();

					if (j == 0) {
					if (disposition != null
						&& (disposition.equalsIgnoreCase("ATTACHMENT"))) {
						System.out.println("Mail have some attachment");

						DataHandler handler = bodyPart.getDataHandler();
						System.out.println("file name : " + handler.getName());
					} else {
						content = bodyPart.getContent().toString();
						System.out.println("getText " + content);
					}
				}
			}
		}
		*/
		}
	}
}
