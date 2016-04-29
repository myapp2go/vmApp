package com.app2go.app2go;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class StockQuoteTask extends AsyncTask {

	static Logger log = ALogger.getLogger(StockQuoteTask.class);

	TwStockQuote sq = null;
//	String[] quoteList = {"CLMT", "FUEL", "LCI", "ROVI", "CONN"};
	
	private StockQuoteActivity stockQuoteActivity;
	
	public StockQuoteTask(StockQuoteActivity mainActivity) {
		stockQuoteActivity = mainActivity;
	}

	protected void onPreExecute() {

	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
//		Map map  = (Map) arg0[0];
		Map map = SharedPreferencesActivity.map;
		int mSize = map.size();
		int indexSize = StockQuoteActivity.indexList.length;
		int len = mSize + indexSize;
				
		if (MainActivity.preferenceFile.equals(Constants.PREFERENCE_FILE)) {
			sq = new StockQuote(len);
		} else {
			sq = new TwStockQuote(mSize);
		}
		sq.getQuote().setQuoteSize(len);
		String[] symbols = setQuotes(map);
		
		for (int i = 0; i < mSize; i++) {
			log.debug("Quote : " + symbols[i]);
			sq.getStockQuoteReport(symbols[i], i);
		}

		for (int i = 0; i < indexSize; i++) {
			log.debug("list : " + StockQuoteActivity.indexList[i]);
			sq.getStockIndexReport(StockQuoteActivity.indexList[i], i+mSize);
		}
		
		return null;
	}
	
	private String[] setQuotes(Map map) {
		String[] symbols = sq.getQuote().getSymbol();
		float[] targets = sq.getQuote().getTarget();
		
		int count = 0;
	    Iterator it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
			symbols[count] = (String) pair.getKey();
			try {
				targets[count] = Float.parseFloat((String)pair.getValue());
			} catch (Exception e) {
				targets[count] = (float)0.0;
			}
			count++;
	    }
		
		return symbols;
	}
	
	@Override
	public void onProgressUpdate(Object... values) {

	}

	@Override
	public void onPostExecute(Object result) {		
		stockQuoteActivity.readStockQuoteDone(sq.getQuote());
	}

}
