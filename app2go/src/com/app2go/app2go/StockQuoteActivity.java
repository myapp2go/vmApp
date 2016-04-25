package com.app2go.app2go;

import java.util.ArrayList;

public class StockQuoteActivity extends SharedPreferencesActivity {

	@Override
	protected void doReadStockQuote(ArrayList<String> matches) {
		// TODO Auto-generated method stub
		new StockQuoteTask(StockQuoteActivity.this).execute(sharedPreferences);
		
	}

	public void readStockQuoteDone(Quote quote) {
		ttsAndPlayEarcon("symbol " + quote.getSymbol()[0]);
		ttsAndPlayEarcon("price " + quote.getPrice()[0]);
	}
}
