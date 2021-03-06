package com.pc.vm;

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
	private int maxLen = 100;
	private int maxRetry = 5;	
	private int retry = 0;
	private int bodyReaded = 0;
//	private boolean skipLink = true;
	private static String linkSkip = "";
	
	protected void doReadMail(ArrayList<String> matches) {
		logStr.add("************** doReadMail ");
		
		switch (subCommand) {
		case Constants.COMMAND_INIT :
			switch (matches.get(0)) {
			case Constants.READ_OPTION_SUBJECT_ONLY :
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
				//				doReadOffLines();
				if ((mailSubject == null || mailSubject.length <= 0) || !isSyncMail) {
					new ReadMailTask(ReadMailActivity.this).execute(sharedPreferences);
				}
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
				readOneMessage();
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
					ttsAndMicrophone(Constants.ANSWER_NOT_VALID);
//					ttsAndPlayEarcon("beep15");
				} else {
					retry = 0;
				}
				break;	
			}
			break;
		case Constants.COMMAND_STOP :
			mailCount = 0;
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
		
		int index = 0;
		for (int i = end - 1; i >= start; i--, index++) {
			try {
				Message msg = messages[i];
				mailSubject[index] = parseFrom(msg.getFrom()[0].toString()) + " send " + msg.getSubject().toLowerCase();
				mailIndex[index] = index;
				
				Object msgContent = msg.getContent();
				if (msgContent instanceof Multipart) {
					Multipart multipart = (Multipart) msgContent;
					boolean found = false;
					
					mailBody[index] = "";
					for (int j = 0; j < multipart.getCount(); j++) {
						BodyPart bodyPart = multipart.getBodyPart(j);
						int pos = bodyPart.getContentType().toUpperCase().indexOf("PLAIN");						
						if (pos > 0) {
							found = true;
							mailBody[index] += parseMessage(bodyPart.getContent().toString());
						}
						
						pos = bodyPart.getContentType().toUpperCase().indexOf("ALTERNATIVE");
						if (pos > 0) {
							found = true;
							if (bodyPart.getContent() instanceof Multipart) {
								Multipart nestpart = (Multipart) bodyPart.getContent();
								String content = "";
								for (int k = 0; k < nestpart.getCount(); k++) {
									BodyPart nestBodyPart = nestpart.getBodyPart(j);
									int nestpos =nestBodyPart.getContentType().toUpperCase().indexOf("PLAIN");
									if ((nestpos > 0) && (nestBodyPart.getContent() != null) && !nestBodyPart.getContent().toString().equals("null")) {
										content = nestBodyPart.getContent().toString();
									}
								}
								mailBody[index] += parseMessage(content);
							}
						}
						String disposition = bodyPart.getDisposition();
						if (disposition != null && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {	
							mailBody[index] += Constants.MAIL_BODY_HAVE_ATTACHMENT;
						}
					}
									
					if (!found) {
						mailBody[index] += Constants.MAIL_BODY_NOT_SUPPORT;
					}
				} else {
					int pos = msg.getContentType().toUpperCase().indexOf("PLAIN");
					if (pos == -1) {
						mailBody[index] += Constants.MAIL_BODY_IS_HTML;
					} else {
						mailBody[index] += parseMessage(msg.getContent().toString());
					}					
				}
			} catch (IOException e) {	
				
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
			if (index == 0) {
				mailCount++;
				ttsAndMicrophone("mail number" + (index + 1) + " " + mailSubject[index]);
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
	
	public void readMailDone(String msg) {
		endDialog();
		
		if (msg != null) {
			doReadOffLines();
			if ((mailSubject == null || mailSubject.length <= 0) || !isSyncMail) {
				ttsAndMicrophone(msg);
			}
		} else {
			doSaveOffLines();
		}
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
					if (!isOffline) {
						readBodyDone = false;
					}
				} else {
					body = mailBody[count].substring(bodyReaded, len-1);
					bodyReaded = len;
					if (!isOffline) {
						readBodyDone = true;
					}
				}			
			} else {
				bodyReaded = len;
				if (!isOffline) {
					readBodyDone = true;
				}
			}

			ttsAndMicrophone(body);
		}
	}
	
	protected void readOneMessage() {
//		System.out.println("************ readOneMessage " + mailCount + " * " + mailSize);
		bodyReaded = 0;
		if (!isOffline) {
			readBodyDone = true;
		}
		
		int size = mailSize;
		if (Constants.COMMAND_SEARCH.equals(command)) {
			size = searchSize;			
		}
		
		if (mailCount < size) {
			ttsAndMicrophone("mail number" + (mailCount+1)  + " " + mailSubject[mailIndex[mailCount]]);		
			mailCount++;
		} else {
			ttsNoMicrophone("End of mail");
			mailCount++;
		}			
	}
	
	private String parseMessage(String str) {
		String paramString = str.toLowerCase();
	    int start = paramString.indexOf("http", 0);
	    StringBuffer localStringBuffer = new StringBuffer();

	    int ind = 0;
	    int end = paramString.length();
	    while (start >= 0) {
	    	if (("http:".equals(paramString.substring(start, start + 5))) || ("https:".equals(paramString.substring(start, start + 6)))) {
	    		localStringBuffer.append(paramString.substring(ind, start) + linkSkip);
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
		
		return from;
	}
	
	protected void doSearchMail(ArrayList<String> matches) {
		boolean found = false;
		
		int count = 0;
		if (mailSubject != null) {
			for (int i = 0; i < mailSubject.length - 1; i++) {
				String subject = mailSubject[i];
				found = false;
				if (subject == null) {
					found = true;
				}
				for (int j = 0; !found && (j < matches.size()); j++) {
					String str = matches.get(j).toLowerCase();
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
				subCommand = Constants.SUBCOMMAND_RETRIEVE;
			} else {
				ttsNoMicrophone("Nothing found");
			}
		}
	}

	// OffLIne
	String mailSubjectData = "mailSubjectData";
	String mailBodyData = "mailBodyData";
	
	protected void doSaveOffLines() {
		doSaveOffLine(mailSubjectData, mailSubject);
		doSaveOffLine(mailBodyData, mailBody);
	}

	protected void doSaveOffLine(String dest, String[] src) {
		File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");
		File file = new File(folder, dest);
		
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
			ttsAndMicrophone("mail number " + (mailCount+1) + " " + mailSubject[mailCount]);
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
//			e.printStackTrace();
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
	
}
