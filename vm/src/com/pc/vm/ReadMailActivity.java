package com.pc.vm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Store;

import android.speech.tts.TextToSpeech;
import android.widget.TextView;

public abstract class ReadMailActivity extends MainActivity {

	private int msgCount = 0;
	private String[] mailMessages;
	private int msgLength;
	
	private Store emailStore = null;
	private Folder emailFolder = null;
	  
	protected void doReadMail(ArrayList<String> matches) {
        System.out.println("*** doReadMail " + command + " * " + subCommand + " * " + answer + " * " + checkReadMode);
        
		if (checkReadMode) {
			matchReadMode(matches);
			checkReadMode = false;
        }
        System.out.println("*** doReadMail AFTER " + command + " * " + subCommand + " * " + answer + " * " + checkReadMode);
		
		switch (subCommand) {
		case Constants.COMMAND_INIT :
			System.out.println("%%%%%%%%%%%%%%%%%%%%%66666%9999 doReadMail Number ");
			String myEmail = ((TextView) findViewById(R.id.myEmail)).getText().toString();
			String myPassword = ((TextView) findViewById(R.id.myPassword)).getText().toString();
// PC522			
//			subCommand = Constants.COMMAND_STOP;
			new ReadMailTask(ReadMailActivity.this).execute(myEmail, myPassword);	
			break;
		case Constants.COMMAND_NEXT :
			readMessage(ttsCount+10, 100);
			break;
		case Constants.COMMAND_STOP :
			commandReset();
    		tts.speak(Constants.COMMAND_GREETING, TextToSpeech.QUEUE_ADD, map);
    		startRecognizer();
			break;
		}
	
	}

	public void setMessages(Message[] messages) {
		mailMessages = new String[messages.length+1];
		mailMessages[0] = Constants.COMMAND_ADVERTISE_GREETING;
		for (int i = 1; i < messages.length; i++) {
			try {
				mailMessages[i] = messages[i].getSubject();
				String str = mailMessages[i];
				String type = "TEXT/HTML";
//				 if (!(messages[i].getFlags() == null))
//				        System.out.println("FLAG " + messages[i].getSubject());
				try {
					if (!(messages[i].getContent() instanceof Multipart)) {
//					System.out.println("%%%%%%%%%%%%%%%FLAG " + messages[i].getContent());
					
//						System.out.println("%%%%%%%%%%%%%%%FLAG " + messages[i].getContentType());
//						System.out.println("%%%%%%%%%%%%%%%FLAGINDEX " + messages[i].getContentType().indexOf("HTML"));
						int pos = messages[i].getContentType().indexOf("HTML");
						if (pos == -1) {
//							System.out.println("%%%%%%%%%%%%%%%CONTENT " + messages[i].getContent());
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("^^^^^^^^^^^^^^^^^setMessages*********** " + mailMessages + " " + messages);
	}

	public void ReadMailDone() {
		int msgLength = mailMessages.length;

		System.out.println("^^^^^^^^^^^^^^^^^ReadMailDone*********** ");
		readMessage(0, msgLength);

	}

	private void readMessage(int count, int msgLength) {
		System.out.println("^^^^^^^^^^^^^^^^^readMessage*********** ");
		
//		for (int i = count; i < msgLength; i++) {
		for (int i = count; (i < count+increment); i++) {
	
//			Message message = mailMessages[i];
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

				tts.speak("mail number" + (i + 1)  + " " + mailMessages[i], TextToSpeech.QUEUE_ADD, map);
	
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
	
	private void matchReadMode(ArrayList<String> matches) {
		boolean found = false;
		
		for (int i = 0; !found && (i < matches.size()); i++) {
			System.out.println("77777777777777777777777777777CHECK " +matches.get(i) );
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
		System.out.println("77777777777777777777777777777CHECsubCommandK " + subCommand);
	}
}
