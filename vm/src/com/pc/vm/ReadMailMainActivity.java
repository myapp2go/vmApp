package com.pc.vm;

import java.util.HashMap;

import javax.mail.Message;
import javax.mail.MessagingException;

import android.speech.tts.TextToSpeech;
import android.widget.TextView;

public class ReadMailMainActivity extends MainActivity {

	private int msgCount = 0;
	private String[] mailMessages;
	private int msgLength;
	
	protected void doReadMail() {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%9999 doReadMail Number ");
		String myEmail = ((TextView) findViewById(R.id.myEmail)).getText().toString();
		String myPassword = ((TextView) findViewById(R.id.myPassword)).getText().toString();
		new ReadMailTask(ReadMailMainActivity.this).execute(msgCount, tts, myEmail, myPassword);		
	}

	public void setMessages(Message[] messages) {
		mailMessages = new String[messages.length];
		for (int i = 0; i < messages.length; i++) {
			try {
				mailMessages[i] = messages[i].getSubject();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.print("^^^^^^^^^^^^^^^^^setMessages*********** " + mailMessages + " " + messages);
	}

	public void ReadMailDone() {
//		int msgLength = mailMessages.length;
		System.out.println("^^^^^^^^^^^^^^^^^ReadMailDone*********** ");
			readMessage(0, msgLength);

	}

	private void readMessage(int count, int msgLength) {
		System.out.println("^^^^^^^^^^^^^^^^^readMessage*********** ");
		
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
			tts.speak("mail number :" + (i + 1), TextToSpeech.QUEUE_ADD, map);
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
