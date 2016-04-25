package com.app2go.app2go;

import android.content.SharedPreferences;
import android.os.AsyncTask;

public class StockQuoteTask extends AsyncTask {

	StockQuote sq = new StockQuote();
	
	private StockQuoteActivity stockQuoteActivity;
	
	public StockQuoteTask(StockQuoteActivity mainActivity) {
		stockQuoteActivity = mainActivity;
	}

	protected void onPreExecute() {

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

	}

	@Override
	public void onPostExecute(Object result) {		
		stockQuoteActivity.readStockQuoteDone(sq.getQuote());
	}

}
