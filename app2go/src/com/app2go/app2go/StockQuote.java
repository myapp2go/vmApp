package com.app2go.app2go;

import java.io.IOException;

import org.jsoup.Jsoup;

public class StockQuote {

	private static int timeout = 5000;
	protected Quote quote;

	public StockQuote(int count) {
		super();
		quote = new Quote(count);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StockQuote sq = new StockQuote(1);
		sq.getStockQuoteReport("LCI", 0);

	}

	public String getStockQuoteReport(String name, int ind) {
		String doc = "";
		name = name.toLowerCase();
		
		try {
			doc = Jsoup.connect("http://finance.yahoo.com/q?s=" + name).timeout(timeout).get().html();

			getStockQuote(name, doc, ind);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc;
	}

	private void getStockQuote(String name, String doc, int ind) {
		int start = 0;
		String[] sym = quote.getSymbol();
		sym[ind] = name;
		// time_rtq_ticker
		start = getValues("yfs_l84_"+name, quote.getPrice(), doc, start, ind);
		// Volume:
		start = getValues("yfs_v53_"+name, quote.getVolume(), doc, start, ind);

	}

	// unit : %, M, B default <
	private int getValues(String name, float[] valAr, String doc, int start, int ind) {
		start = doc.indexOf(name, start) + name.length() + 2; 
		int end = doc.indexOf("<", start);
		String str = doc.substring(start, end).replaceAll(",", "");
		int len = str.length();
		float mult = (float)1.0;
		String last = str.substring(len-1, len);
		switch (last) {
		case "%" :
			str = str.substring(0, len-1);
			break;
		case "M" :
			str = str.substring(0, len-1);
			break;
		case "B" :
			str = str.substring(0, len-1);
			mult = (float)1000.0;
			break;
		default :
			break;
		}
		if (str == null || str.equals("N/A") || str.length() > 20) {
			valAr[ind] = (float)0.0;
		} else {
			valAr[ind] = Float.parseFloat(str) * mult;
		}
		
		return start;
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

}
