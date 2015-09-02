package com.pc.vm;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
		SharedPreferences pref  = (SharedPreferences) params[0];
		String myEmail = pref.getString("myEmail", "");
		String myPassword = pref.getString("myPassword", "");
		
		writeMail(myEmail, myPassword, params[1].toString(), params[2].toString(), params[3].toString());
		return null;
	}

	private void writeMail(String mailAccount, String password, String mailTo, String mailSubject, String mailBody) {
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

}

