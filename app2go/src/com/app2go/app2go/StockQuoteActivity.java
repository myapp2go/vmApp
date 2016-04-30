package com.app2go.app2go;

import java.util.ArrayList;

public class StockQuoteActivity extends SharedPreferencesActivity {

//	public static String[] indexList = {"^dji"};
	public static String[] indexList = {"^dji", "^ixic", "^gspc"};
	public static String[] indexNameList = {"Dow Jones", "NASDAQ", "S&P 500"};
	public static int skipNum = 5;
	private int indexCount = 1;
	
	@Override
	protected void doReadStockQuote(ArrayList<String> matches) {
		new StockQuoteTask(StockQuoteActivity.this).execute(map);		
	}

	public void readStockQuoteDone(Quote quote) {
		if (MainActivity.preferenceFile.equals(Constants.PREFERENCE_FILE)) {
			readUsStockQuoteDone(quote);
		} else {
			readTwStockQuoteDone(quote);
		}
	}

	public void readUsStockQuoteDone(Quote quote) {
		indexCount--;
		int len = quote.getQuoteSize() - 1;
		for (int i = 0; i <= len; i++) {
			if (indexCount == 0 || i >= indexList.length) {
				if (i < indexList.length) {
					ttsNoMicrophone(indexNameList[i]);
					ttsNoMicrophone("index " + quote.getPrice()[i]);
				} else {
					String str = quote.getSymbol()[i];
					for (int j = 0; j < str.length(); j++) {
						ttsNoMicrophone(str.substring(j, j + 1));
					}
					ttsNoMicrophone("price " + quote.getPrice()[i]);
				}
				
				ttsNoMicrophone(quote.getArrow()[i]);

				ttsNoMicrophone(" " + quote.getChange()[i]);

				if (i == len) {
					ttsNoMicrophone("volume " + quote.getVolume()[i] / 1000,
							true);
				} else {
					if (i >= indexList.length) {
						ttsNoMicrophone("volume " + quote.getVolume()[i] / 1000, false);
					}
				}
			}
		}
		
		if (indexCount == 0) {
			indexCount = skipNum;
		}
	}

	public void readTwStockQuoteDone(Quote quote) {
		int len = quote.getQuoteSize() - 1;
		for (int i = 0; i <= len; i++) {
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
			}if (i == len) {
				ttsNoMicrophone("volume " + quote.getVolume()[i]/1000, true);
			} else {
				ttsNoMicrophone("volume " + quote.getVolume()[i]/1000, false);				
			}
		}
	}
	
}
