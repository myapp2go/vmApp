package com.app2go.app2go;

import java.util.ArrayList;

public class StockQuoteActivity extends SharedPreferencesActivity {

	@Override
	protected void doReadStockQuote(ArrayList<String> matches) {
		new StockQuoteTask(StockQuoteActivity.this).execute(sharedPreferences);		
	}

	public void readStockQuoteDone(Quote quote) {
		ttsNoMicrophone(quote.getSymbol()[0]);
		ttsNoMicrophone("price " + quote.getPrice()[0]);
		ttsNoMicrophone("volume " + quote.getVolume()[0]);
	}
}
