package com.app2go.app2go;

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
import android.os.AsyncTask;

public class StockQuoteTask extends AsyncTask {

	private static String imapYahooHost = "imap.mail.yahoo.com";
	private static String imapStoreType = "imaps";

	private static String quoteMsg = null;
	StockQuote sq = new StockQuote();
	private Quote quote;
	
	private StockQuoteActivity stockQuoteActivity;
	private ProgressDialog statusDialog;
	
	public StockQuoteTask(StockQuoteActivity mainActivity) {
		stockQuoteActivity = mainActivity;
	}

	protected void onPreExecute() {
		statusDialog = new ProgressDialog(stockQuoteActivity);
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

//		StockQuote sq = new StockQuote();
		sq.getStockQuoteReport("LCI");
		
		return null;
	}
	
	@Override
	public void onProgressUpdate(Object... values) {
		statusDialog.setMessage(values[0].toString());

	}

	@Override
	public void onPostExecute(Object result) {
		statusDialog.dismiss();
		
		stockQuoteActivity.readStockQuoteDone(sq.getQuote());
	}

}
