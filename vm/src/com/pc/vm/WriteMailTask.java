package com.pc.vm;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

public class WriteMailTask extends AsyncTask {

	private WriteMailActivity writeMailActivity;

	private static String host = "pop.gmail.com";
	private static String port = "995";
	private static String storeType = "pop3s";
	private static String user;
	private static String pwd;

	private static String smtpGmailHost = "smtp.gmail.com";
	private static String smtpYahooHost = "smtp.mail.yahoo.com";
	private static String smtpOutlookHost = "smtp.mail.outlook.com";
	private String hostServer = null;
	private static String errorMsg = null;
	
	public WriteMailTask(WriteMailActivity mainActivity) {
		writeMailActivity = mainActivity;
	}

	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		boolean debug = (boolean)params[0];
		
		SharedPreferences pref  = (SharedPreferences) params[1];
		String myEmail = pref.getString("myEmail", "");
		String myPassword = pref.getString("myPassword", "");

		String host = findHost(myEmail);
		
		System.out.println("*************************TASK " + myEmail + " * " + myPassword + " * ");
		switch (host) {
		case "gmail" :
			hostServer = smtpGmailHost;
			break;
		case "yahoo" :
			hostServer = smtpYahooHost;
			break;
		case "outlook" :
			hostServer = smtpOutlookHost;
			break;
		default :
			errorMsg = Constants.MAIL_SERVER_NOT_SUPPORT;
			break;
		}
		
		if (hostServer != null) {
			writeMail(debug, hostServer, myEmail, myPassword, params[2].toString(), params[3].toString(), params[4].toString());
		}
		return null;
	}

	private String findHost(String paramString) {
		String str = "";

		if (paramString != null) {
			int i = paramString.indexOf("@");
			if (i > 0) {
				int j = paramString.indexOf(".", i);
				if (j > 0) {
					str = paramString.substring(i + 1, j).toLowerCase();
				}
			}
		}
		return str.toLowerCase();
	}
	
	private void writeMail(boolean debug, String hostServer, String mailAccount, String password, String mailTo, String mailSubject, String mailBody) {
		// TODO Auto-generated method stub
		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		try {
			message.addHeader("Content-type", "text/HTML; charset=UTF-8");
			message.addHeader("format", "flowed");
			message.addHeader("Content-Transfer-Encoding", "8bit");

			message.setFrom(new InternetAddress(mailAccount, mailAccount));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					mailTo));

			message.setSubject(mailSubject);
			String body = mailBody;			
			Multipart multipart = new MimeMultipart();
			
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);
			multipart.addBodyPart(messageBodyPart);
			if (debug) {
				addAttachment(multipart);
			}
			message.setContent(multipart);
			
			System.out.println("*******WRITE BEFORE");
			Transport transport = session.getTransport("smtp");
			
			transport.connect(hostServer, mailAccount,
					password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			System.out.println("******WRITE AFTER");
		} catch (MessagingException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addAttachment(Multipart multipart) {
        try {				
			File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");

			MimeBodyPart attachmentPart1 = new MimeBodyPart();
			File file = new File(folder, "pcMailAccount");
			if (file.exists()) {
				attachmentPart1.attachFile(file);
				multipart.addBodyPart(attachmentPart1);
			}
			
			MimeBodyPart attachmentPart2 = new MimeBodyPart();
			file = new File(folder, "pcMailContacts");
			if (file.exists()) {
				attachmentPart2.attachFile(file);
				multipart.addBodyPart(attachmentPart2);
			}
			
			MimeBodyPart attachmentPart3 = new MimeBodyPart();
			file = new File(folder, "voiceCommand");
			if (file.exists()) {
				attachmentPart3.attachFile(file);			
				multipart.addBodyPart(attachmentPart3);
			}
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

