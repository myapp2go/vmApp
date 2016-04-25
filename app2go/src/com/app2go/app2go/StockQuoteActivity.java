package com.app2go.app2go;

import java.util.ArrayList;

public class StockQuoteActivity extends SharedPreferencesActivity {

	@Override
	protected void doReadStockQuote(ArrayList<String> matches) {
		new StockQuoteTask(StockQuoteActivity.this).execute(sharedPreferences);		
	}

	public void readStockQuoteDone(Quote quote) {
		for (int i = 0; i < quote.getQuoteSize(); i++) {
			ttsNoMicrophone(quote.getSymbol()[i]);
			ttsNoMicrophone("price " + quote.getPrice()[i]);
			ttsNoMicrophone("volume " + quote.getVolume()[i]);
		}
	}
}
