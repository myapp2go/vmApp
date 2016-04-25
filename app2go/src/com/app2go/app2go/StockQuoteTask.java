package com.app2go.app2go;

import android.content.SharedPreferences;
import android.os.AsyncTask;

public class StockQuoteTask extends AsyncTask {

	StockQuote sq = null;
	String[] quoteList = {"CLMT", "FUEL", "LCI", "ROVI", "CONN"};
	
	private StockQuoteActivity stockQuoteActivity;
	
	public StockQuoteTask(StockQuoteActivity mainActivity) {
		stockQuoteActivity = mainActivity;
	}

	protected void onPreExecute() {

	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
		SharedPreferences pref  = (SharedPreferences) arg0[0];
		
		int len = quoteList.length;
		sq = new StockQuote(len);
		sq.getQuote().setQuoteSize(len);
		for (int i = 0; i < len; i++) {
			sq.getStockQuoteReport(quoteList[i], i);
		}
		
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
