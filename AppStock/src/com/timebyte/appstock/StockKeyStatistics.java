package com.timebyte.appstock;

import java.io.IOException;

import org.jsoup.Jsoup;

public class StockKeyStatistics {

	private static int timeout = 5000;
	protected KeyStatistics ks;
	
	public StockKeyStatistics() {
		super();
		ks = new KeyStatistics();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String name = "CONN";
		
		StockKeyStatistics stockKeyStatistics = new StockKeyStatistics();
		stockKeyStatistics.createKeyStatistics();
		stockKeyStatistics.getKeyStatisticsReport(name);
		
//		System.out.println("SSS " + ks.getReturnonEquity()[0]);
	}

	protected KeyStatistics createKeyStatistics() {
		ks = new KeyStatistics();
		return ks;
	}

	public void getKeyStatisticsReport(String name) {
		String doc = "";
		
		try {
			doc = Jsoup.connect("http://finance.yahoo.com/q/ks?s="+name+"+Key+Statistics").timeout(timeout).get().html();
			getKeyStatistics(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void getKeyStatistics(String doc) {
		getValues("Return on Equity (ttm)", ks.getReturnonEquity(), doc);
		getValues("Avg Vol (3 month)", ks.getAvgVol3M(), doc);
		getValues("Shares Outstanding", ks.getSharesOutstanding(), doc);
		
	}

	// unit : %, M, B default <
	private void getValues(String name, float[] valAr, String doc) {
		int start = doc.indexOf(name);
		start = doc.indexOf("yfnc_tabledata1", start) + 17;
		int end = doc.indexOf("<", start+2);
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
			valAr[0] = (float)0.0;
		} else {
			valAr[0] = Float.parseFloat(str) * mult;
		}
	}

	public KeyStatistics getKs() {
		return ks;
	}

	public void setKs(KeyStatistics ks) {
		this.ks = ks;
	}
	
}
