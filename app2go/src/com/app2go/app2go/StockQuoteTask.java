package com.app2go.app2go;

import java.util.StringTokenizer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
		String quotes = pref.getString("Quotes", "");
		int len = procQuotes(quotes);
				
		sq = new StockQuote(len);
		sq.getQuote().setQuoteSize(len);
		String[] symbols = setQuotes(quotes);
		
		for (int i = 0; i < len; i++) {
			System.out.println("SSS " + symbols[i]);
			sq.getStockQuoteReport(symbols[i], i);
		}
		
		return null;
	}
	
	private String[] setQuotes(String quotes) {
		String[] symbols = sq.getQuote().getSymbol();
		float[] targets = sq.getQuote().getTarget();
		int count = 0;
		
		StringTokenizer st = new StringTokenizer(quotes, Constants.CONTACT_MARKER);
		
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			int ind = str.indexOf(":");
			if (ind > 0) {
				symbols[count] = str.substring(0, ind);
				try {
					targets[count] = Float.parseFloat(str.substring(ind+1));
				} catch (Exception e) {
					targets[count] = (float)0.0;
				}
				count++;
			}
		}
		
		return symbols;
	}

	private int procQuotes(String quotes) {
		int count = 0;
		
		StringTokenizer st = new StringTokenizer(quotes, Constants.CONTACT_MARKER);
				
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			int ind = str.indexOf(":");
			if (ind > 0) {
//				String name = str.substring(0, ind);
//				String value = str.substring(ind+1);
				count++;
			}
		}
		
		return count;
	}
	
	@Override
	public void onProgressUpdate(Object... values) {

	}

	@Override
	public void onPostExecute(Object result) {		
		stockQuoteActivity.readStockQuoteDone(sq.getQuote());
	}

}
