package com.app2go.app2go;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class StockQuoteTask extends AsyncTask {

//	static Logger log = ALogger.getLogger(StockQuoteTask.class);

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
		String[] symbols = setQuotes(map, indexSize);
		
		for (int i = 0; i < mSize; i++) {
////			log.debug("Quote : " + symbols[i]);
			sq.getStockQuoteReport(symbols[i+indexSize], i+indexSize);
		}

		for (int i = 0; i < indexSize; i++) {
//			log.debug("list : " + StockQuoteActivity.indexList[i]);
			sq.getStockIndexReport(StockQuoteActivity.indexList[i], i);
		}
		
		return null;
	}
	
	private String[] setQuotes(Map map, int indexSize) {
		String[] symbols = sq.getQuote().getSymbol();
		float[] aboveTargets = sq.getQuote().getAboveTarget();
		float[] belowTargets = sq.getQuote().getBelowTarget();
		
		int count = indexSize;
	    Iterator it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
			symbols[count] = (String) pair.getKey();
			try {
				String val = (String)pair.getValue();
				int del = val.indexOf("/");
				String above = val.substring(0, del);
				String below = val.substring(del+1);
				aboveTargets[count] = Float.parseFloat(above);
				belowTargets[count] = Float.parseFloat(below);
			} catch (Exception e) {
				aboveTargets[count] = (float)0.0;
				belowTargets[count] = (float)0.0;
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
