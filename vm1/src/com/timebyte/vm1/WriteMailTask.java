package com.timebyte.vm1;

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
		
		writeMail(debug, myEmail, myPassword, params[2].toString(), params[3].toString(), params[4].toString());
		return null;
	}

	private void writeMail(boolean debug, String mailAccount, String password, String mailTo, String mailSubject, String mailBody) {
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

			message.setFrom(new InternetAddress("tapaulchen@gmail.com", "NoReply-JD"));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					mailTo));

			message.setSubject(mailSubject);
			String body = mailBody;
			message.setContent(body, "text/html");
			message.setText(body, "UTF-8"); 
			if (debug) {
				message.setContent(addAttachment());
			}
			
			Transport transport = session.getTransport("smtp");

			transport.connect("smtp.gmail.com", mailAccount,
					password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Multipart addAttachment() {
		Multipart messageBodyPart = null;
		
        try {			
			messageBodyPart = new MimeMultipart();			
			
			File folder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + "/VoiceMail");

			MimeBodyPart attachmentPart1 = new MimeBodyPart();
			File file = new File(folder, "pcMailAccount");
			attachmentPart1.attachFile(file);
			messageBodyPart.addBodyPart(attachmentPart1);
			
			MimeBodyPart attachmentPart2 = new MimeBodyPart();
			file = new File(folder, "pcVoiceMail");
			attachmentPart2.attachFile(file);
			messageBodyPart.addBodyPart(attachmentPart2);
			
			MimeBodyPart attachmentPart3 = new MimeBodyPart();
			file = new File(folder, "voiceCommand");
			attachmentPart3.attachFile(file);			
			messageBodyPart.addBodyPart(attachmentPart3);
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return messageBodyPart;
	}
}

