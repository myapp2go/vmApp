package com.app2go.app2go;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

public class TwStockQuote {

//	static Logger log = ALogger.getLogger(TwStockQuote.class);
	
	protected static int timeout = 5000;
	protected Quote quote;

	public TwStockQuote(int count) {
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

	public String getStockIndexReport(String name, int ind) {
		return "";
	}
		
	public String getStockQuoteReport(String name, int ind) {
		String doc = "";
		name = name.toLowerCase();
		try {
			doc = Jsoup.connect("https://tw.finance.yahoo.com/q/q?s=" + name).timeout(timeout).get().html();
			
			int retval = getTwStockQuote(name, doc, ind);
			if (retval < 0) {
//				getTwMobileStockQuote(name, doc, ind);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc;
	}

	private void getTwMobileStockQuote(String name, String doc, int ind) {
		int start = 0;
		String[] sym = quote.getSymbol();
		sym[ind] = name;
		// time_rtq_ticker
		start = getValues(name.toUpperCase()+":value", ">", "<", 2, null, quote.getPrice(), doc, start, ind);
		// Up or Down
		start = getValues(name.toUpperCase()+":chg", ">", "<", 2, quote.getArrow(), null, doc, start, ind);
		// change
		getTwChange(ind);
//		start = getValues(">", null, "<", 0, null, quote.getChange(), doc, start, ind);

		// Volume:
		start = getValues(name.toUpperCase()+":longVolume", ">", "<", 2, null, quote.getVolume(), doc, start, ind);
	}

	private void getTwChange(int ind) {
		if (quote.getArrow()[ind].charAt(0) == '+') {
			quote.getChange()[ind] = Float.parseFloat(quote.getArrow()[ind].substring(1));
			quote.getArrow()[ind] = "Up";
		} else if (quote.getArrow()[ind].charAt(0) == '-') {
			quote.getChange()[ind] = Float.parseFloat(quote.getArrow()[ind].substring(1));
			quote.getArrow()[ind] = "Down";			
		}		
	}

	private int getTwStockQuote(String name, String doc, int ind) {
		int start = 0;
		String[] sym = quote.getSymbol();
		sym[ind] = name;
		// time_rtq_ticker
		start = getValues("stocklist=", "nowrap", "<", 2, quote.getArrow(), null, doc, start, ind);
		if (start > 0) {
			// price
			start = getValues("nowrap", "b>", "<", 1, null, quote.getPrice(), doc, start, ind);

			// buy
			start = getValues("nowrap", null, "<", 1, null, quote.getChange(), doc, start, ind);
			// sold
			start = getValues("nowrap", null, "<", 1, null, quote.getChange(), doc, start, ind);
			// up down
			start = getValues("nowrap", null, "<", 2, quote.getArrow(), null, doc, start, ind);
			// quantity
			start = getValues("nowrap", null, "<", 1, null, quote.getVolume(), doc, start, ind);
			// yesterday close
			start = getValues("nowrap", null, "<", 1, null, quote.getYesterdayClose(), doc, start, ind);
			// open
			start = getValues("nowrap", null, "<", 1, null, quote.getChange(), doc, start, ind);
			// high
			start = getValues("nowrap", null, "<", 1, null, quote.getChange(), doc, start, ind);
			// low
			start = getValues("nowrap", null, "<", 1, null, quote.getChange(), doc, start, ind);
		
		}
		return start;
	}

	// unit : %, M, B default <
	private int getValues(String name, String stMark, String endMark, int delta, String[] strAr, float[] valAr, String doc, int start, int ind) {
		start = doc.indexOf(name, start);
		if (start > 0) {
			if (stMark != null) {
				start = doc.indexOf(stMark, start) + stMark.length();
			} else {
				start += name.length() + delta;
			}
			int end = doc.indexOf(endMark, start);
			String str = doc.substring(start, end).replaceAll(",", "");
			if (strAr != null) {
				strAr[ind] = str;
			} else {
				int len = str.length();
				float mult = (float) 1.0;
				String last = str.substring(len - 1, len);
				switch (last) {
				case "%":
					str = str.substring(0, len - 1);
					break;
				case "M":
					str = str.substring(0, len - 1);
					break;
				case "k":
				case "B":
					str = str.substring(0, len - 1);
					mult = (float) 1000.0;
					break;
				case "m":
					str = str.substring(0, len - 1);
					mult = (float) 1000000.0;
					break;
				default:
					break;
				}
				if (str == null || str.equals("N/A") || str.length() > 20) {
					valAr[ind] = (float) 0.0;
				} else {
					valAr[ind] = Float.parseFloat(str) * mult;
				}
			}
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
