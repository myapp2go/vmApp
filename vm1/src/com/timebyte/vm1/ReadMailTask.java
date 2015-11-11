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

	private static String imapGmailHost = "imap.gmail.com";
	private static String imapYahooHost = "imap.mail.yahoo.com";
	private static String imapOutlookHost = "imap-mail.outlook.com";
	private static String imapStoreType = "imaps";
	private static String errorMsg = null;
	
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
		String host = findHost(myEmail);
		
		System.out.println("*************************TASK " + myEmail + " * " + myPassword + " * " + readOPtion);
		switch (host) {
		case "gmail" :
			readGEmailByIMAP(imapGmailHost, myEmail, myPassword);
			break;
		case "yahoo" :
			readGEmailByIMAP(imapYahooHost, myEmail, myPassword);
			break;
		case "outlook" :
			readGEmailByIMAP(imapOutlookHost, myEmail, myPassword);
			break;
		default :
			errorMsg = Constants.SETTING_ACCOUNT_ERROR;
			break;
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
	
	public void readGEmailByIMAP(String imapHost, String mailAccount, String password) {	
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

//		    System.out.println("&&&&&&&&&&&&&&&&&&SEEEEEEEEEEEEEEEE&&&&&&& " +emailFolder.getMessageCount() );
//		    System.out.println("&&&&&&&&&&&&&&&&&&SEEEEEEEEEEEEEEEE " +messages1.length );
//			Message[] messages = emailFolder.getMessages();
//			System.out.println("&&&&&&&&&&&&&&&&&&SDDDDDDDDDDDDDDDD " +messages1.length );
			readMailActivity.setMessages(messages1);
			
			emailFolder.close(false);
			emailFolder = null;
			emailStore.close();
			emailStore = null;
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			errorMsg = Constants.SETTING_ACCOUNT_ERROR;
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
		
		readMailActivity.readMailDone(errorMsg);
	}
	
}
