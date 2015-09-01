package com.timebyte.vm1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import android.speech.tts.TextToSpeech;

public abstract class ReadMailActivity extends SharedPreferencesActivity {

	private String[] mailSubject;
	private String[] mailBody;
	private int maxLen = 200;
	private int maxRetry = 5;	
	private int retry = 0;
	private int bodyReaded = 0;
	private boolean skipLink = true;
	
	protected void doReadMail(ArrayList<String> matches) {
		
		switch (subCommand) {
		case Constants.COMMAND_INIT :
			switch (matches.get(0)) {
			case Constants.READ_OPTION_SUBJECT_ONLY :
				readMode = Constants.READ_OPTION_SUBJECT_ONLY;
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences);
				isSyncMail = true;
				break;
			case Constants.READ_OPTION_SUBJECT_BODY :
				readMode = Constants.READ_OPTION_SUBJECT_BODY;
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences);
				isSyncMail = true;
				break;
			case Constants.COMMAND_NONE :
				break;	
			}
			break;
		case Constants.SUBCOMMAND_RETRIEVE :
			String answer = matchReadCommand(matches);
			System.out.println("************ doReadMail9999 " + subCommand + " & " + answer);
			switch (answer) {
			case Constants.ANSWER_CONTINUE :
				if (Constants.READ_OPTION_SUBJECT_BODY.equals(readMode)) {
					// PC522 Handle readBodyDone
//					waitBodyCommand = false;
					readMessageBody();
				} else {
					readMessage();
				}
				break;
			case Constants.ANSWER_STOP :
				mailCount = 0;
				readBodyDone = true;
				bodyReaded = 0;
				break;
			case Constants.ANSWER_SKIP :
				bodyReaded = 0;
//				mailCount = 0;
//				readBodyDone = true;
				if (Constants.READ_OPTION_SUBJECT_BODY.equals(readMode)) {
					readBodyDone = true;
					mailCount++;
					readMessageBody();
				} else {
					if (retry < maxRetry) {	
						retry++;
						ttsAndPlayEarcon("jetsons");
					} else {
						retry = 0;
					}
				}
				break;	
			case Constants.COMMAND_NONE :
				if (retry < maxRetry) {	
					retry++;
					ttsAndPlayEarcon("money");
				} else {
					retry = 0;
				}
				break;	
			}
			break;	
/*			
		case Constants.SUBCOMMAND_MORE_SKIP :
			String cmd = matchReadMode(matches);
			switch (cmd) {
			case Constants.SUBCOMMAND_MORE :
				microphoneOn = false;
				waitBodyCommand = false;
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				readMessageBody();
				break;
			case Constants.SUBCOMMAND_SKIP :
				microphoneOn = false;
				waitBodyCommand = false;
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				mailCount++;
				readMessageBody();
				break;
				
			case Constants.COMMAND_NONE :
				if (retry < maxRetry) {	
					retry++;
//					tts.speak(Constants.COMMAND_READ_BODY_MORE_SKIP, TextToSpeech.QUEUE_ADD, map);
					ttsAndPlayEarcon("money");
				} else {
					retry = 0;
				}
				break;
			}
			break;
			*/
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
		int len = messages.length;
		int start = 0;
		int end = messages.length;
		if (len > maxReadCount) {
			len = maxReadCount;
			start = messages.length - maxReadCount;			
		}
		mailSubject = new String[len + 1];
		mailBody = new String[len + 1];
		mailSubject[0] = Constants.COMMAND_ADVERTISE_SUBJECT;
		mailBody[0] = Constants.COMMAND_ADVERTISE_BODY;	

		int index = len;
		for (int i = start; i < end; i++, index--) {
			try {
				Message msg = messages[i];
				mailSubject[index] = msg.getSubject();
//				System.out.println("************MMM " + (index) + " " + mailSubject[index]);

				Object msgContent = msg.getContent();
				if (msgContent instanceof Multipart) {
					Multipart multipart = (Multipart) msgContent;
					boolean found = false;
					
					mailBody[index] = "";
					for (int j = 0; j < multipart.getCount(); j++) {
						BodyPart bodyPart = multipart.getBodyPart(j);
						int pos = bodyPart.getContentType().indexOf("PLAIN");						
						if (pos > 0) {
							found = true;
							mailBody[index] += parseMessage(bodyPart.getContent().toString());
						}
						
						pos = bodyPart.getContentType().indexOf("ALTERNATIVE");
						if (pos > 0) {
							found = true;
							if (bodyPart.getContent() instanceof Multipart) {
								Multipart nestpart = (Multipart) bodyPart.getContent();
								String content = "";
								for (int k = 0; k < nestpart.getCount(); k++) {
									BodyPart nestBodyPart = nestpart.getBodyPart(j);
									int nestpos =nestBodyPart.getContentType().indexOf("PLAIN");
									if ((nestpos > 0) && (nestBodyPart.getContent() != null) && !nestBodyPart.getContent().toString().equals("null")) {
										content = nestBodyPart.getContent().toString();
									}
								}
								mailBody[index] += content;
							}
						}
						String disposition = bodyPart.getDisposition();
						if (disposition != null && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {	
							mailBody[i] += Constants.MAIL_BODY_HAVE_ATTACHMENT;
						}
					}
									
					if (!found) {
						mailBody[index] += Constants.MAIL_BODY_NOT_SUPPORT;
					}
				} else {
					int pos = msg.getContentType().indexOf("PLAIN");
					if (pos == -1) {
						mailBody[index] += Constants.MAIL_BODY_IS_HTML;
					} else {
						mailBody[index] += parseMessage(msg.getContent().toString());
					}					
				}
			} catch (IOException e) {	
				
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		dump();
	}

	private void dump() {
		System.out.println("****************************************START***********************");
		for (int i = 0; i < mailBody.length; i++) {
			System.out.println("****************************************ind***********************" + i);
			System.out.println(mailBody[i]);
		}
		System.out.println("***************************************************************");

	}
	
	private String matchReadCommand(ArrayList<String> matches) {
		boolean found = false;
		String sub = Constants.COMMAND_NONE;
		System.out.println("*****MATCH " + matches);
		
		for (int i = 0; !found && (i < matches.size()); i++) {
			String ret = commandMap.get(matches.get(i));
			if (ret != null) {
				found = true;
				sub = ret;
			}
		}
		
		return sub;
	}
	
	private String matchReadCommandOld(ArrayList<String> matches) {
		// TODO Auto-generated method stub
        boolean found = false;
        String ans = Constants.COMMAND_NONE;
		
        for (int i = 0; !found && (i < matches.size()); i++) {
        	switch (matches.get(i)) {
        	case Constants.ANSWER_CONTINUE :
        		found = true;
        		ans = Constants.ANSWER_CONTINUE;
        		break;
        	case Constants.ANSWER_STOP :
        		found = true;
        		ans = Constants.ANSWER_STOP;
        		break;	
        	case Constants.ANSWER_SKIP :
        		found = true;
        		ans = Constants.ANSWER_SKIP;
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
		int len = body.length();

		if (len > maxLen) {
			if ((len - bodyReaded) >= maxLen) {
				int ind = body.indexOf(" ", (bodyReaded+maxLen));
				if (ind <= 0) {
					ind = len;
				}
				body = mailBody[count].substring(bodyReaded, ind);
				bodyReaded = ind;
				readBodyDone = false;
			} else {
				body = mailBody[count].substring(bodyReaded, len-1);
				readBodyDone = true;
			}			
		} else {
			mailCount++;
			bodyReaded = 0;
			readBodyDone = true;
		}
		
		ttsNoMicrophone("mail number" + (count + 1)  + " " + mailSubject[count] + body);
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
			
			ttsNoMicrophone("mail number" + (i + 1)  + " " + mailSubject[i]);
		}
	}
	
	private String matchReadMode(ArrayList<String> matches) {
		boolean found = false;
		String sub = Constants.COMMAND_NONE;
		
		for (int i = 0; !found && (i < matches.size()); i++) {
			switch (matches.get(i)) {
			case Constants.ANSWER_CONTINUE:
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
			case Constants.ANSWER_STOP:
				found = true;
				sub = Constants.COMMAND_STOP;
				break;
			case Constants.ANSWER_SKIP:
				found = true;
				sub = Constants.SUBCOMMAND_SKIP;
				break;
			}
		}
		
		return sub;
	}
	
	private String parseMessage(String paramString) {
	    int start = paramString.indexOf("http", 0);
	    StringBuffer localStringBuffer = new StringBuffer();

	//    System.out.println("&&& SOURCE " + paramString);
	    int ind = 0;
	    int end = paramString.length();
	    while (start >= 0) {
	    	if (("http:".equals(paramString.substring(start, start + 5))) || ("https:".equals(paramString.substring(start, start + 6)))) {
	    		localStringBuffer.append(paramString.substring(ind, start) + "  link skip");
	    		ind = paramString.indexOf(" ", start);
	    		if (ind == -1) {
	    			ind = start;
	    			start = -1;
	    		} else {
	    			start = paramString.indexOf("http", ind);
//	    			System.out.println("&&& IND " + start + " * " + ind);
	    		}
	        } else {
//	    	    System.out.println("&&& ELSE " + start + " * " + ind);
	        	start = paramString.indexOf("http", start+5);
	        }
	    }
//	    System.out.println("&&& END " + start + " * " + ind);
	    localStringBuffer.append(paramString.substring(ind, end));
	    
	    return localStringBuffer.toString();
	}
}
