package com.timebyte.appstock;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;

import android.os.AsyncTask;

public class PCStock extends AsyncTask {

	protected String[] symbol = {
			"MMM", "T", "ABT", "ACN", "AXP", "AAPL", "AJG", "BBT", "BCE", "BDX", "CVX", "CSCO", "KO", "CL", "DE", "DEO", "EMR", "ES", "XOM", "GE", "GPC", "IBM", "ITW", "INTC", "JNJ", "JPM", "LEG", "LMT", "LOW", "MSFT", "NVS", "OXY", "OMC", "ORCL", "PH", "PFE", "PM", "PPG", "PG", "RTN", "TROW", "TXN", "UTX", "VZ", "VFC", "WM", "WFC", "AMLP"
			};
	
	protected static String posts = "";
	
	private float[] totalRevenue = new float[4];
	private float[] costofRevenue = new float[4];
	private float[] researchDevelopment = new float[4];
	private float[] sellingGeneralandAdministrative = new float[4];
	private float[] nonRecurring = new float[4];

	private float[] totalOtherIncomeExpensesNet = new float[4];
	private float[] earningsBeforeInterestAndTaxes = new float[4];

	private float[] interestExpense = new float[4];
	private float[] incomeBeforeTax = new float[4];

	private float[] incomeTaxExpense = new float[4];
	private float[] minorityInterest = new float[4];

	private float[] netIncomeApplicableToCommonShares = new float[4];

	private float[] cashAndCashEquivalents = new float[4];
	private float[] shortTermInvestments = new float[4];
	
	private float[] commonStock = new float[4];
	private float[] preferredStock = new float[4];
	private float[] retainedEarnings = new float[4];
	private float[] capitalSurplus = new float[4];
	private float[] otherStockholderEquity = new float[4];
	private float[] totalStockholderEquity = new float[4];

	private float[] depreciation = new float[4];
	
	private String v = "";
	
	public static void main(String[] args) {
		String stock = "CONN";
		int count = 4;
		
		PCStock stk = new PCStock();
		stk.getReport(stock, count);
		
		System.out.println("&&&& " + posts);
	}

	/**
	 * @param args
	 */
	public void getStock(String stock) {
	//	String stock = "BCE";
		int count = 4;
		
		getReport(stock, count);
	}
	
	private void getReport(String stock, int count) {
		if (count == 3) {
			getAnnualReport(stock, count);
		} else {
			getQualterReport(stock, count);
		}
				
		// EPS
		System.out.print("\nEPS\t\t\t\t");
		for (int i = 0; i < count; i++) {
			System.out.print("\t" + netIncomeApplicableToCommonShares[i]/(commonStock[i]*1000));
		}
	}

	private void getQualterReport(String stock, int count) {
		String doc = "";
		
		try {
			doc = Jsoup.connect("http://finance.yahoo.com/q/is?s="+stock).get().html();
			getIncomeStatement(doc, count);

			doc = Jsoup.connect("http://finance.yahoo.com/q/bs?s="+stock).get().html();
			getBalanceSheet(doc, count);

			doc = Jsoup.connect("http://finance.yahoo.com/q/cf?s="+stock).get().html();
			getCashFlow(doc, count);

			// EPS
			for (int i = 0; i < count; i++) {
//				System.out.println(netIncomeApplicableToCommonShares[i]/commonStock[i]);
			}
			
			for (int i = 0; i < count; i++) {
//				System.out.println(depreciation[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getAnnualReport(String stock, int count) {
		String doc = "";
		
		try {
			doc = Jsoup.connect("http://finance.yahoo.com/q/is?s="+stock+"+Income+Statement&annual").get().html();
			getIncomeStatement(doc, count);

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

	private void getIncomeStatement(String doc, int count) {
		getValues("Total Revenue", totalRevenue, doc, count);
		getValues("Cost of Revenue", costofRevenue, doc, count);

		getValues("Research Development", researchDevelopment, doc, count);
		getValues("Selling General and Administrative", sellingGeneralandAdministrative, doc, count);
		getValues("Non Recurring", nonRecurring, doc, count);

		getValues("Total Other Income/Expenses Net", totalOtherIncomeExpensesNet, doc, count);
		getValues("Earnings Before Interest And Taxes", earningsBeforeInterestAndTaxes, doc, count);

		getValues("Interest Expense", interestExpense, doc, count);
// CAL		getValues("Income Before Tax", incomeBeforeTax, doc, count);

		getValues("Income Tax Expense", incomeTaxExpense, doc, count);
		getValues("Minority Interest", minorityInterest, doc, count);
		
		getValues("Net Income Applicable To Common Shares", netIncomeApplicableToCommonShares, doc, count);

		// Gross Profit
		posts += "\nGross Profit\t\t\t";		
		for (int i = 0; i < count; i++) {
			sf((totalRevenue[i] - costofRevenue[i]));
//			System.out.print("\t\t" + (totalRevenue[i] - costofRevenue[i]));
		}

		// Gross Profit Ratio
		System.out.print("\nGross Profit Ratio\t\t");		
		for (int i = 0; i < count; i++) {
			System.out.print("\t" + (totalRevenue[i] - costofRevenue[i])/totalRevenue[i]);
		}
		
		// % of Cost of Revenue
		System.out.print("\n% of Cost of Revenue\t\t");		
		for (int i = 0; i < count; i++) {
			System.out.print("\t" + (costofRevenue[i] / totalRevenue[i]));
		}

		// % of Operating Expenses
		posts += "\n% of Operating Expenses\t\t";		
		for (int i = 0; i < count; i++) {
			sf(((researchDevelopment[i]+sellingGeneralandAdministrative[i]+nonRecurring[i]) / totalRevenue[i]));
		}

		// Operating Income or Loss
		posts += "\nOperating Income or Loss\t";		
		for (int i = 0; i < count; i++) {
			sf(((totalRevenue[i] - costofRevenue[i]) - (researchDevelopment[i]+sellingGeneralandAdministrative[i]+nonRecurring[i])));
//			System.out.print("\t\t" + ((totalRevenue[i] - costofRevenue[i]) - (researchDevelopment[i]+sellingGeneralandAdministrative[i]+nonRecurring[i])));
		}

		// Operating Income or Loss Ratio
		System.out.print("\nOperating Income or Loss Ratio\t");		
		for (int i = 0; i < count; i++) {
			System.out.print("\t" + ((totalRevenue[i] - costofRevenue[i]) - (researchDevelopment[i]+sellingGeneralandAdministrative[i]+nonRecurring[i]))/totalRevenue[i]);
		}
		
		// Operating Income or Loss
		posts += "\nEarnings Before Interest And Taxes";		
		for (int i = 0; i < count; i++) {
			sf(((totalRevenue[i] - costofRevenue[i]) - (researchDevelopment[i]+sellingGeneralandAdministrative[i]+nonRecurring[i]) + totalOtherIncomeExpensesNet[i]));
//			System.out.print("\t\t" + ((totalRevenue[i] - costofRevenue[i]) - (researchDevelopment[i]+sellingGeneralandAdministrative[i]+nonRecurring[i]) + totalOtherIncomeExpensesNet[i]));
		}

		// NetIncome Applicable To CommonSh
		System.out.print("\nNet Income Appl To Common Shares Ratio");		
		for (int i = 0; i < count; i++) {
			System.out.print("\t" + (netIncomeApplicableToCommonShares[i])/totalRevenue[i]);
		}

		// P49 
		posts += "\nInterest Ratio\t\t\t";
		for (int i = 0; i < count; i++) {
			sf(earningsBeforeInterestAndTaxes[i]/interestExpense[i]);
//			System.out.print("\t" + earningsBeforeInterestAndTaxes[i]/interestExpense[i]);
		}		
	}
	
	private void sf(float value) {
		v = "\t" + value;
		posts += v;
		System.out.print(v);
		if (v.length() < 9) {
			System.out.print("\t");
		}
	}
	
	private void getBalanceSheet(String doc, int count) {
		getValues("Cash And Cash Equivalents", cashAndCashEquivalents, doc, count);
		getValues("Short Term Investments", shortTermInvestments, doc, count);

		// extra Common Stock, so add < to filter out
		getValues("Common Stock<", commonStock, doc, count);
		getValues("Preferred Stock", preferredStock, doc, count);
		getValues("Retained Earnings", retainedEarnings, doc, count);
		getValues("Capital Surplus", capitalSurplus, doc, count);
		getValues("Other Stockholder Equity", otherStockholderEquity, doc, count);
		getValues("Total Stockholder Equity", totalStockholderEquity, doc, count);
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
		getValues("Depreciation", depreciation, doc, count);

		// 049
		/*
		for (int i = 0; i < 4; i++) {
			System.out.println(earningsBeforeInterestAndTaxes[i]/interestExpense[i]);
		}
		*/
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
//	//			System.out.println("FFFFFFFFFFFFFF " + beginIndex + " KKK " + endIndex);
				if (beginIndex < 0 || endIndex < 0 || (endIndex-beginIndex>20)) {
					val = 0;
				} else {
					try {
						val = Integer.parseInt(doc.substring(beginIndex, endIndex).replace(",", ""));
					} catch (Exception e) {
						val = 0;
					}
				}
				break;
			}
			
			valueAr[i] = val;
//			System.out.println(val);
		}
	}

	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		return null;
	}


}

