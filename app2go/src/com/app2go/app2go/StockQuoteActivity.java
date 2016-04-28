package com.app2go.app2go;

import java.util.ArrayList;

public class StockQuoteActivity extends SharedPreferencesActivity {

	@Override
	protected void doReadStockQuote(ArrayList<String> matches) {
		new StockQuoteTask(StockQuoteActivity.this).execute(map);		
	}

	public void readStockQuoteDone(Quote quote) {
		readTwStockQuoteDone(quote);
	}

	public void readTwStockQuoteDone(Quote quote) {
		for (int i = 0; i < quote.getQuoteSize(); i++) {
			ttsNoMicrophone(quote.getSymbol()[i]);
			ttsNoMicrophone("price " + quote.getPrice()[i]);
			float diff = quote.getPrice()[i] - quote.getYesterdayClose()[i];
			if (diff > 0) {
				ttsNoMicrophone(" " + "Up");
				ttsNoMicrophone(" " + diff);
			} else if (diff < 0) {
				ttsNoMicrophone(" " + "Down");
				ttsNoMicrophone(" " + diff);				
			} else {
				ttsNoMicrophone(" " + "No Change");
				ttsNoMicrophone(" " + diff);
			}
			ttsNoMicrophone("volume " + quote.getVolume()[i]/1000);
		}
	}
	
	public void readUsStockQuote(Quote quote) {
		for (int i = 0; i < quote.getQuoteSize(); i++) {
			ttsNoMicrophone(quote.getSymbol()[i]);
			ttsNoMicrophone("price " + quote.getPrice()[i]);
			ttsNoMicrophone(" " + quote.getArrow()[i]);
			ttsNoMicrophone(" " + quote.getChange()[i]);
			ttsNoMicrophone("volume " + quote.getVolume()[i]/1000);
		}
	}
}
