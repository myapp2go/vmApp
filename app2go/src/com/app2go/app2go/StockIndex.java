package com.app2go.app2go;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

public class StockIndex extends TwStockQuote {

//	static Logger log = ALogger.getLogger(StockIndex.class);

//	private static int timeout = 5000;
//	protected Quote quote;

	public StockIndex(int count) {
		super(count);
//		quote = new Quote(count);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StockQuote sq = new StockQuote(1);
		sq.getStockQuoteReport("LCI", 0);

	}

	public String getStockIndexReport(String name, int ind) {
		String doc = "";
		name = name.toLowerCase();
//		log.debug("BEF : ");
		
		try {
			doc = Jsoup.connect("http://finance.yahoo.com/q?s=" + name).timeout(timeout).get().html();

			int retval = getStockIndex(name, doc, ind);
//			log.debug("retval : " + retval);
			if (retval < 0) {
				getMobileStockIndex(name, doc, ind);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc;
	}

	private void getMobileStockIndex(String name, String doc, int ind) {
		int start = 0;
		String[] sym = quote.getSymbol();
		sym[ind] = name;
		// time_rtq_ticker
		start = getValues(name.toUpperCase()+":value", ">", "<", 2, null, quote.getPrice(), doc, start, ind);
		// Up or Down
		start = getValues(name.toUpperCase()+":chg", ">", "<", 2, quote.getArrow(), null, doc, start, ind);
		// change
		getChange(ind);
//		start = getValues(">", null, "<", 0, null, quote.getChange(), doc, start, ind);

		// Volume:
		start = getValues(name.toUpperCase()+":longVolume", ">", "<", 2, null, quote.getVolume(), doc, start, ind);
	}

	private int getStockIndex(String name, String doc, int ind) {
		int start = 0;
		String[] sym = quote.getSymbol();
		sym[ind] = name;
		// time_rtq_ticker
		start = getValues("yfs_l10_"+name, null, "<", 2, null, quote.getPrice(), doc, start, ind);
		if (start > 0) {
			// Up or Down
			start = getValues("_arrow", "alt=\"", "\"", 2, quote.getArrow(), null, doc, start, ind);
			// change
			start = getValues(">", null, "<", 0, null, quote.getChange(), doc, start, ind);
			// Volume:
//			start = getValues("yfs_v53_"+name, null, "<", 2, null, quote.getVolume(), doc, start, ind);
		
		}
		return start;
	}

	protected void getChange(int ind) {
		if (quote.getArrow()[ind].charAt(0) == '+') {
			quote.getChange()[ind] = Float.parseFloat(quote.getArrow()[ind].substring(1));
			quote.getArrow()[ind] = "Up";
		} else if (quote.getArrow()[ind].charAt(0) == '-') {
			quote.getChange()[ind] = Float.parseFloat(quote.getArrow()[ind].substring(1));
			quote.getArrow()[ind] = "Down";			
		}
		
	}

	// unit : %, M, B default <
	protected int getValues(String name, String stMark, String endMark, int delta, String[] strAr, float[] valAr, String doc, int start, int ind) {
		start = doc.indexOf(name, start);
		if (start > 0) {
			if (stMark != null) {
				start = doc.indexOf(stMark, start) + stMark.length();
			} else {
				start += name.length() + delta;
			}
			int end = doc.indexOf(endMark, start);
			String str = doc.substring(start, end).replaceAll(",", "");
//			log.debug("valddPPP : " + str);
			if (str == null || str.length() < 2) {
				if (valAr != null) {
					valAr[ind] = (float) 0.0;
				}
			} else {
//				log.debug("valddPPPSSS : " + str.length());
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
		}
		
		return start;
	}

}
