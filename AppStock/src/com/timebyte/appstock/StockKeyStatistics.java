package com.timebyte.appstock;

import java.io.IOException;

import org.jsoup.Jsoup;

public class StockKeyStatistics {

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
			doc = Jsoup.connect("http://finance.yahoo.com/q/ks?s="+name+"+Key+Statistics").get().html();
			getKeyStatistics(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void getKeyStatistics(String doc) {
		getValues("Return on Equity (ttm)", ks.getReturnonEquity(), doc, "%");
		getValues("Avg Vol (3 month)", ks.getAvgVol3M(), doc, "<");
		getValues("Shares Outstanding", ks.getSharesOutstanding(), doc, "M");
		
	}

	// unit : %, M, B default <
	private void getValues(String name, float[] valAr, String doc, String unit) {
		int start = doc.indexOf(name);
		start = doc.indexOf("yfnc_tabledata1", start) + 17;
		int end = doc.indexOf(unit, start+2);
		valAr[0] = Float.parseFloat(doc.substring(start, end).replaceAll(",", ""));
		System.out.println(valAr[0]);
	}

	public KeyStatistics getKs() {
		return ks;
	}

	public void setKs(KeyStatistics ks) {
		this.ks = ks;
	}
	
}
