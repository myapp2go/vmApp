package com.pc.vm;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;

public class ReadMailTask extends AsyncTask {

	private ProgressDialog statusDialog;
	private ReadMailMainActivity readMailActivity;

	private static String host = "pop.gmail.com";
	private static String port = "995";
	private static String storeType = "pop3s";
	private static String user;
	private static String pwd;
	
	private static String imapHost = "imap.gmail.com";
	private static String imapStoreType = "imaps";
	
	public ReadMailTask(ReadMailMainActivity mainActivity) {
		readMailActivity = mainActivity;
	}

	protected void onPreExecute() {
		statusDialog = new ProgressDialog(readMailActivity);
		statusDialog.setMessage("Reading mail...");
		statusDialog.setIndeterminate(false);
		statusDialog.setCancelable(false);
		statusDialog.show();
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
//				readEmailByIMAP(params[2].toString(), params[3].toString());
		return null;
	}

	public void readEmailByIMAP(String mailAccount, String password) {	
		user = mailAccount;
		pwd = password;
		
		Store emailStore = null;
		Folder emailFolder = null;
	
		Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "imaps");
		
		try {
			Session emailSession = Session.getDefaultInstance(properties);	
			emailStore = (Store) emailSession.getStore(imapStoreType);	
			emailStore.connect(imapHost, user, password);			
			emailFolder = emailStore.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			Message[] messages = emailFolder.getMessages();
			readMailActivity.setMessages(messages);
//			readMessage(0);
			
//			readMessage();
			
			emailFolder.close(false);
			emailFolder = null;
			emailStore.close();
			emailStore = null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	    finally {
			if (emailFolder != null)
				try {
					emailFolder.close(false);
				} catch (MessagingException e1) {
					e1.printStackTrace();
				}
			if (emailStore != null)
				try {
					emailStore.close();
				} catch (MessagingException e1) {
					e1.printStackTrace();
				}
	      }
		
		// TODO Auto-generated method stub
		System.out.println("************************** PWD " + mailAccount + " " + password);
	}

	@Override
	public void onProgressUpdate(Object... values) {
		statusDialog.setMessage(values[0].toString());

	}

	@Override
	public void onPostExecute(Object result) {
		statusDialog.dismiss();
		
		readMailActivity.ReadMailDone();
	}
}
