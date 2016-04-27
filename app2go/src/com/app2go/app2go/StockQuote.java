package com.app2go.app2go;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

public class StockQuote {

	static Logger log = ALogger.getLogger(StockQuote.class);

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
		log.debug("BEF : ");
		
		try {
			doc = Jsoup.connect("http://finance.yahoo.com/q?s=" + name).timeout(timeout).get().html();

			int retval = getStockQuote(name, doc, ind);
			log.debug("retval : " + retval);
			if (retval < 0) {
				getMobileStockQuote(name, doc, ind);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc;
	}

	private void getMobileStockQuote(String name, String doc, int ind) {
		int start = 0;
		String[] sym = quote.getSymbol();
		sym[ind] = name;
		// time_rtq_ticker
		start = getValues(name.toUpperCase()+":value", ">", "<", 2, null, quote.getPrice(), doc, start, ind);

		// Volume:
		start = getValues(name.toUpperCase()+":longVolume", ">", "<", 2, null, quote.getVolume(), doc, start, ind);
	}

	private int getStockQuote(String name, String doc, int ind) {
		int start = 0;
		String[] sym = quote.getSymbol();
		sym[ind] = name;
		// time_rtq_ticker
		start = getValues("yfs_l84_"+name, null, "<", 2, null, quote.getPrice(), doc, start, ind);
		if (start > 0) {
			// Up or Down
			start = getValues("_arrow", "alt=\"", "\"", 2, quote.getArrow(), null, doc, start, ind);
			// change
			start = getValues(">", null, "<", 0, null, quote.getChange(), doc, start, ind);
			// Volume:
			start = getValues("yfs_v53_"+name, null, "<", 2, null, quote.getVolume(), doc, start, ind);
		
		}
		return start;
	}

	// unit : %, M, B default <
	private int getValues(String name, String stMark, String endMark, int delta, String[] strAr, float[] valAr, String doc, int start, int ind) {
		log.debug(start + "val : " + name + "***" + doc.substring(start, start+100));
		start = doc.indexOf(name, start);
		log.debug(start + "NNN : " + name);
		if (start > 0) {
			if (stMark != null) {
				start = doc.indexOf(stMark, start) + stMark.length();
			} else {
				start += name.length() + delta;
			}
			log.debug("valdd : " + doc.substring(start, start+100));
			int end = doc.indexOf(endMark, start);
			String str = doc.substring(start, end).replaceAll(",", "");
			log.debug("valddPPP : " + str);
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
				log.debug("valArSS : " + str);
				if (str == null || str.equals("N/A") || str.length() > 20) {
					valAr[ind] = (float) 0.0;
				} else {
					valAr[ind] = Float.parseFloat(str) * mult;
				}
				log.debug("valAr : " + valAr[ind]);
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
