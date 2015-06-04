package com.timebyte.vm1;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class ReadMailTask extends AsyncTask {

	private static String imapHost = "imap.gmail.com";
	private static String imapStoreType = "imaps";
	
	private ReadMailActivity readMailActivity;
	private ProgressDialog statusDialog;
	
	public ReadMailTask(ReadMailActivity mainActivity) {
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
	protected Object doInBackground(Object... arg0) {

		SharedPreferences pref  = (SharedPreferences) arg0[0];
		// TODO Auto-generated method stub
//		SharedPreferences pref = getApplicationContext().getSharedPreferences("VoiceMailPref", MODE_PRIVATE); 
		String myEmail = pref.getString("myEmail", "");
		String myPassword = pref.getString("myPassword", "");
		String readOPtion = pref.getString("readOPtion", "");

		System.out.println("*************************TASK " + myEmail + " * " + myPassword + " * " + readOPtion);
		readGEmailByIMAP(myEmail, myPassword);
		
		return null;
	}

	public void readGEmailByIMAP(String mailAccount, String password) {	
		Store emailStore = null;
		Folder emailFolder = null;
	
		Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "imaps");
		
		try {
			Session emailSession = Session.getDefaultInstance(properties);	
			emailStore = (Store) emailSession.getStore(imapStoreType);	
			emailStore.connect(imapHost, mailAccount, password);			
			emailFolder = emailStore.getFolder("INBOX");
//			emailFolder.open(Folder.READ_WRITE);
			emailFolder.open(Folder.READ_ONLY);
			
		    Flags seen = new Flags(Flags.Flag.SEEN);
		    FlagTerm unseenFlagTerm = new FlagTerm(seen, true);
		    
//		    Message messages1[] = emailFolder.search(unseenFlagTerm);
		    Message messages1[] = emailFolder.getMessages();
		    
		    System.out.println("SEEEEEEEEEEEEEEEE " +messages1.length );
			Message[] messages = emailFolder.getMessages();
			readMailActivity.setMessages(messages1);
			
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
		
		readMailActivity.readMailDone();
	}
	
}
