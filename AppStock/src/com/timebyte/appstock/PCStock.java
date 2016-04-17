package com.timebyte.appstock;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;

import android.os.AsyncTask;

public class PCStock extends AsyncTask {

	protected Stock stk;
		
	public static void main(String[] args) {
		System.out.println("Start with MainActivity");
	}

	protected Stock createStock() {
		stk = new Stock();
		return stk;
	}
	
	/**
	 * @param args
	 */
	public void getStock(String stock) {
		int count = 4;
		
		getReport(stock, count);
	}
	
	protected void getReport(String stock, int count) {
		if (count == 3) {
			getAnnualReport(stock, count);
		} else {
			getQualterReport(stock, count);
		}
/*				
		// EPS
		System.out.print("\nEPS\t\t\t\t");
		for (int i = 0; i < count; i++) {
			System.out.print("\t" + netIncomeApplicableToCommonShares[i]/(commonStock[i]*1000));
		}
		*/
	}

	private void getQualterReport(String stock, int count) {
		String doc = "";
		
		try {
			doc = Jsoup.connect("http://finance.yahoo.com/q/is?s="+stock).get().html();
			getIncomeStatement(stock, doc, count);

			doc = Jsoup.connect("http://finance.yahoo.com/q/bs?s="+stock).get().html();
			getBalanceSheet(doc, count);

			doc = Jsoup.connect("http://finance.yahoo.com/q/cf?s="+stock).get().html();
			getCashFlow(doc, count);
/*
			// EPS
			for (int i = 0; i < count; i++) {
//				System.out.println(netIncomeApplicableToCommonShares[i]/commonStock[i]);
			}
			
			for (int i = 0; i < count; i++) {
//				System.out.println(depreciation[i]);
			}
			*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getAnnualReport(String stock, int count) {
		String doc = "";
		
		try {
			doc = Jsoup.connect("http://finance.yahoo.com/q/is?s="+stock+"+Income+Statement&annual").get().html();
			getIncomeStatement(stock, doc, count);

			doc = Jsoup.connect("http://finance.yahoo.com/q/bs?s="+stock+"+Balance+Sheet&annual").get().html();
			getBalanceSheet(doc, count);

			doc = Jsoup.connect("http://finance.yahoo.com/q/cf?s="+stock+"&annual").get().html();
			getCashFlow(doc, count);

/*			
			for (int i = 0; i < count; i++) {
				System.out.println(depreciation[i]);
			}
			*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getIncomeStatement(String name, String doc, int count) {
		getValues("Total Revenue", stk.getTotalRevenue(), doc, count);
		getValues("Cost of Revenue", stk.getCostofRevenue(), doc, count);

		getValues("Research Development", stk.getResearchDevelopment(), doc, count);
		getValues("Selling General and Administrative", stk.getSellingGeneralandAdministrative(), doc, count);
		getValues("Non Recurring", stk.getNonRecurring(), doc, count);

		getValues("Total Other Income/Expenses Net", stk.getTotalOtherIncomeExpensesNet(), doc, count);
		getValues("Earnings Before Interest And Taxes", stk.getEarningsBeforeInterestAndTaxes(), doc, count);

		getValues("Interest Expense", stk.getInterestExpense(), doc, count);
// CAL		getValues("Income Before Tax", stk.getIncomeBeforeTax(), doc, count);

		getValues("Income Tax Expense", stk.getIncomeTaxExpense(), doc, count);
		getValues("Minority Interest", stk.getMinorityInterest(), doc, count);
		
		getValues("Net Income Applicable To Common Shares", stk.getNetIncomeApplicableToCommonShares(), doc, count);

		getString("scope", stk.getPeriodEnding(), doc, count);
		
		getStockPrice(name, stk.getStockPrice(), count);

	}
	
	private void getStockPrice(String name, float[] stockPrice, int count) {
		Map<String, String[]> map = stk.getMonthMap();
		for (int i = 0; i < count; i++) {
			String data = stk.getPeriodEnding()[i];
			String month = data.substring(0, 3);
			String[] val = map.get(month);
			int index = data.indexOf(",");
			String year = data.substring(index+2);
			
			getPrice(name, data, val, month, year);
		}		
	}

	private void getPrice(String name, String date, String[] val, String month, String year) {
		String newYear = year;
		if (month.equals("Dec")) {
			newYear = "" + (Integer.parseInt(year) + 1);
		}
		String url = "http://finance.yahoo.com/q/hp?s=" + name + "&a=" + val[0] + "&b=1&c=" + year + "&d=" + val[1] + "&e=7&f=" + newYear + "&g=d";
		
		try {
			String hpDoc = Jsoup.connect(url).get().html();
			int start = hpDoc.indexOf(date);
			boolean found = false;
			if (start < 0) {
				for (int i = 0; !found && i < 10; i++) {
					date = getNextDate(date, val);
					start = hpDoc.indexOf(date);
					if (start > 0) {
						found = true;
					}
				}
			} else {
				found = true;
			}
			
			if (found) {
				start = hpDoc.indexOf("right", start) + 7;
				start = hpDoc.indexOf("right", start) + 7;
				start = hpDoc.indexOf("right", start) + 7;
				start = hpDoc.indexOf("right", start) + 7;
				int end = hpDoc.indexOf("<", start);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

			
	private String getNextDate(String date, String[] val) {
		int end = date.indexOf(",");
		String month = date.substring(0, 3);
		String day = date.substring(4, end);
		String year = date.substring(end + 2);
		String newDay = "1";
		if (day.equals("31")) {
			month = val[2];
			if (month.equals("Jan")) {
				year = "" + (Integer.parseInt(year) + 1);
			}
		} else {
			newDay = "" + (Integer.parseInt(day) + 1);
		}
 
		return month + " " + newDay + ", " + year;
	}

	private void getBalanceSheet(String doc, int count) {
		getValues("Cash And Cash Equivalents", stk.getCashAndCashEquivalents(), doc, count);
		getValues("Short Term Investments", stk.getShortTermInvestments(), doc, count);

		// extra Common Stock, so add < to filter out
		getValues("Common Stock<", stk.getCommonStock(), doc, count);
		getValues("Preferred Stock", stk.getPreferredStock(), doc, count);
		getValues("Retained Earnings", stk.getRetainedEarnings(), doc, count);
		getValues("Capital Surplus", stk.getCapitalSurplus(), doc, count);
		getValues("Other Stockholder Equity", stk.getOtherStockholderEquity(), doc, count);
		getValues("Total Stockholder Equity", stk.getTotalStockholderEquity(), doc, count);
/*
		for (int i = 0; i < 4; i++) {
			System.out.println(commonStock[i]+retainedEarnings[i]+capitalSurplus[i]+otherStockholderEquity[i]);
		}
		
		// Book Value Per Common Share
		for (int i = 0; i < 4; i++) {
			System.out.println((totalStockholderEquity[i]-preferredStock[i])/commonStock[i]);
		}
		*/
	}

	private void getCashFlow(String doc, int count) {
		getValues("Depreciation", stk.getDepreciation(), doc, count);

		// 049
		/*
		for (int i = 0; i < 4; i++) {
			System.out.println(earningsBeforeInterestAndTaxes[i]/interestExpense[i]);
		}
		*/
	}

	private void getString(String name, String[] valueAr, String doc, int count) {
		int nameInd = doc.indexOf(name);
		
		int endIndex = nameInd;
		int beginIndex = 0;
		for (int i = 0; i < count; i++) {
			beginIndex = doc.indexOf(name, endIndex) + 7;
			beginIndex = doc.indexOf(">", beginIndex) + 1;
			endIndex = doc.indexOf("<", beginIndex);
			valueAr[i] = doc.substring(beginIndex, endIndex);
		}
	}

	private void getValues(String name, float[] valueAr, String doc, int count) {
		int nameInd = doc.indexOf(name);
		
		int endIndex = nameInd;
		int beginIndex = 0;
		int val = 0;
		for (int i = 0; i < count; i++) {
			beginIndex = doc.indexOf("right", endIndex) + 7;
			if ("strong".equals(doc.substring(beginIndex+2, beginIndex+8))) {
				beginIndex += 10;
			}
			switch (doc.charAt(beginIndex)) {
			case '(' :
				endIndex = doc.indexOf(")", beginIndex);
				val = Integer.parseInt(doc.substring(beginIndex+1, endIndex).replace(",", "")) * (-1);
				break;
			case ' ' :
				endIndex = doc.indexOf("td", beginIndex) - 1;
				val = 0;
				break;
			default:
				endIndex = doc.indexOf("nbsp", beginIndex) - 1;
				if (beginIndex < 0 || endIndex < 0 || (endIndex-beginIndex>20)) {
					val = 0;
				} else {
					try {
						val = Integer.parseInt(doc.substring(beginIndex, endIndex).replaceAll(",", ""));
					} catch (Exception e) {
						val = 0;
					}
				}
				break;
			}
			
			valueAr[i] = val;
		}
	}

	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

}

