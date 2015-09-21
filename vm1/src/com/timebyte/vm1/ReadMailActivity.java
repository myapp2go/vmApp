package com.timebyte.vm1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import android.os.Environment;

public abstract class ReadMailActivity extends SharedPreferencesActivity {

	private String[] mailSubject;
	private String[] mailBody;
	private int[] mailIndex;
	private int maxLen = 200;
	private int maxRetry = 5;	
	private int retry = 0;
	private int bodyReaded = 0;
//	private boolean skipLink = true;
	
	protected void doReadMail(ArrayList<String> matches) {

		switch (subCommand) {
		case Constants.COMMAND_INIT :
			switch (matches.get(0)) {
			case Constants.READ_OPTION_SUBJECT_ONLY :
				readMode = Constants.READ_OPTION_SUBJECT_ONLY;
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				
				//				doReadOffLines();
				if (mailSubject == null || mailSubject.length <= 0) {
					new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences);
				}
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
			switch (answer) {
			case Constants.ANSWER_CONTINUE :
				bodyReaded = 0;
				if (Constants.READ_OPTION_SUBJECT_BODY.equals(readMode)) {
					// PC522 Handle readBodyDone
//					waitBodyCommand = false;
					readMessageBody();
				} else {
					readOneMessage();
				}
				break;
			case Constants.ANSWER_DETAIL :
				readMessageBody();
				break;
			case Constants.ANSWER_STOP :
				bodyReaded = 0;
				mailCount = 0;
				readBodyDone = true;
				endDialog();
				break;
			case Constants.ANSWER_SKIP :
				bodyReaded = 0;
				readBodyDone = true;
				readOneMessage();
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
		case Constants.COMMAND_NEXT :
			microphoneOn = false;
			mailCount--;
			readOneMessage();
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
		mailSize = len;
		mailSubject = new String[len + 1];
		mailBody = new String[len + 1];
		mailIndex = new int[len + 1];
		mailSubject[0] = Constants.COMMAND_ADVERTISE_SUBJECT;
		mailBody[0] = Constants.COMMAND_ADVERTISE_BODY;	
		microphoneOn = false;
		
		int index = 0;
		for (int i = end - 1; i >= start; i--, index++) {
			try {
				Message msg = messages[i];
				mailSubject[index] = parseFrom(msg.getFrom()[0].toString()) + " send " + msg.getSubject();
				mailIndex[index] = index;
				
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
								mailBody[index] += parseMessage(content);
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
			
			if (index == 0) {				
				if (Constants.READ_OPTION_SUBJECT_BODY.equals(readMode)) {
					microphoneOn = false;
					readMessageBody();
				} else {
					mailCount++;
					ttsNoMicrophone("mail number" + (index+1) + " " + mailSubject[index]);
				}
			}
		}
		
//		dump();
	}

	private void dump() {
		System.out.println("****************************************START***********************");
		for (int i = 0; i < mailBody.length; i++) {
			System.out.println("****************************************ind99***********************" + i);
			System.out.println(mailBody[i]);
		}
		System.out.println("***************************************************************");

	}
	
	private String matchReadCommand(ArrayList<String> matches) {
		boolean found = false;
		String sub = Constants.COMMAND_NONE;
		
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
//		microphoneOn = false;
//		if (Constants.READ_OPTION_SUBJECT_BODY.equals(readMode)) {
//			readMessageBody();
//		} else {
//			readMessage();
//		}
		endDialog();
	}

	protected void readMessageBody() {
		int count = mailIndex[mailCount - 1];
		String body = mailBody[count];
		int len = body.length();
		
		if (len == bodyReaded) {
			readOneMessage();
		} else {
			if (len > maxLen) {
				if ((len - bodyReaded) >= maxLen) {
					int ind = body.indexOf(". ", (bodyReaded+maxLen));
					if (ind <= 0) {
						ind = len;
					}
					body = mailBody[count].substring(bodyReaded, ind);
					bodyReaded = ind;
					readBodyDone = false;
				} else {
					body = mailBody[count].substring(bodyReaded, len-1);
					bodyReaded = len;
					readBodyDone = true;
				}			
			} else {
				bodyReaded = len;
				readBodyDone = true;
			}

			ttsNoMicrophone(body);
//			ttsNoMicrophone("mail number" + (count + 1)  + " " + body);
		}
	}
	
	protected void readOneMessage() {
		bodyReaded = 0;
		readBodyDone = true;
		if (mailCount <= mailSize) {
			ttsNoMicrophone("mail number" + (mailCount+1)  + " " + mailSubject[mailIndex[mailCount]]);		
			mailCount++;
		} else {
			ttsNoMicrophone("End of mail");
		}
			
	}
	
	private void readMessageOld() {
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
				sub = Constants.ANSWER_CONTINUE;
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
			case Constants.SUBCOMMAND_DETAIL:
				found = true;
				sub = Constants.SUBCOMMAND_DETAIL;
				break;
			case Constants.SUBCOMMAND_REPEAT:
				found = true;
				sub = Constants.SUBCOMMAND_REPEAT;
				break;	
			}
		}
		
		return sub;
	}
	
	private String parseMessage(String paramString) {
	    int start = paramString.indexOf("http", 0);
	    StringBuffer localStringBuffer = new StringBuffer();

	    int ind = 0;
	    int end = paramString.length();
	    while (start >= 0) {
	    	if (("http:".equals(paramString.substring(start, start + 5))) || ("https:".equals(paramString.substring(start, start + 6)))) {
	    		localStringBuffer.append(paramString.substring(ind, start) + "  link skip");
	    		ind = paramString.indexOf(" ", start);
	    		if (ind == -1) {
	    			ind = end;
	    			start = -1;
	    		} else {
	    			start = paramString.indexOf("http", ind);
	    		}
	        } else {
	        	start = paramString.indexOf("http", start+5);
	        }
	    }
	    
	    localStringBuffer.append(paramString.substring(ind, end));
	    
	    return localStringBuffer.toString();
	}
	
	private String parseFrom(String src) {
		String from = "";
		
		if (src.charAt(0) == '=') {
			int ind = src.indexOf("<");
			if (ind > 0) {
				from = src.substring(ind+1, src.length()-1);
			}
		} else {
			int ind = src.indexOf("<");
			if (ind > 0) {
				from = src.substring(0, ind-1);
			} else {
				from = src;
			}
		}
		
//		System.out.println("********************FFF " + from);
		return from;
	}
	
	protected void doSearchMail(ArrayList<String> matches) {
		boolean found = false;
		
		int count = 0;
		for (int i = 0; i < mailSubject.length-1; i++) {
			String subject = mailSubject[i];
			found = false;
			if (subject == null) {
				found = true;
			}
			for (int j = 0; !found && (j < matches.size()); j++) {
				String str = matches.get(j);
				if (subject.indexOf(str) >= 0) {
					mailIndex[count++] = i;
					found = true;
				}
			}
		}
		
		if (count > 0) {
			mailCount = 0;
			searchSize = count;
			readOneMessage();
		}
	}
	
	String mailSubjectData = "mailSubjectData";
	String mailBodyData = "mailBodyData";
	
	protected void doSaveOffLines() {
		doSaveOffLine(mailSubjectData, mailSubject);
		doSaveOffLine(mailBodyData, mailBody);
	}

	protected void doSaveOffLine(String dest, String[] src) {
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");
		File file = new File(folder, dest);
		System.out.println("***OUT " + dest);
		
		ObjectOutputStream outputString = null;
		try {
			outputString = new ObjectOutputStream(new FileOutputStream(file));
			outputString.writeObject(src);	
			outputString.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doReadOffLines() {
		doReadOffLine(mailSubjectData, mailSubject);
		doReadOffLine(mailBodyData, mailBody);
		
		if (mailSubject == null || mailSubject.length <= 0) {
			System.out.println("No File found");
		} else {
			ttsNoMicrophone("mail number " + (mailCount+1) + " " + mailSubject[mailCount]);
			mailCount++;
		}
	}
	
	protected void doReadOffLine(String src, String[] dest) {
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");
		File file = new File(folder, src);
		
		ObjectInputStream inString = null;
		try {
			inString = new ObjectInputStream(new FileInputStream(file));

			if (mailSubjectData.equals(src)) {
				mailSubject = (String[])inString.readObject();
			}
			if (mailBodyData.equals(src)) {
				mailBody = (String[])inString.readObject();
			}
			if (mailSubject != null) {
				mailSize = mailSubject.length;
				mailIndex = new int[mailSize];
				
				for (int i = 0; i < mailSize; i++) {
					mailIndex[i] = i;
				}
			}
			inString.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
}
