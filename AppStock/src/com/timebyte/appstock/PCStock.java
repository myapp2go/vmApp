package com.timebyte.appstock;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

public class PCStock extends AsyncTask {

	private MainActivity mainActivity;
	private DatabaseHandler db;
	TextView postText;
	private String posts = "";
	
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
	
	/**
	 * @param args
	 */
	public void getStock(String stock) {
	//	String stock = "CONN";
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
		System.out.print("Gross Profit\t\t\t");		
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
		System.out.print("\n% of Operating Expenses\t\t");		
		for (int i = 0; i < count; i++) {
			System.out.print("\t" + ((researchDevelopment[i]+sellingGeneralandAdministrative[i]+nonRecurring[i]) / totalRevenue[i]));
		}

		// Operating Income or Loss
		System.out.print("\nOperating Income or Loss\t");		
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
		System.out.print("\nEarnings Before Interest And Taxes");		
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
		System.out.print("\nInterest Ratio\t\t\t");
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
				val = Integer.parseInt(doc.substring(beginIndex, endIndex).replace(",", ""));
				break;
			}
			
			valueAr[i] = val;
//			System.out.println(val);
		}
	}

	public PCStock(MainActivity activity) {
		System.out.println("STOCK1: ");
		
		mainActivity = activity;
	}

	@Override
	protected void onPreExecute() {
		System.out.println("onPreExecute: ");
	}
	
	@Override
	public void onProgressUpdate(Object... values) {
		System.out.println("onProgressUpdate: ");
	}
	
	@Override
	public void onPostExecute(Object result) {
        db.addContact(new Contact("Paul", "9100000000"));
        db.addContact(new Contact("Srinivas", "9199999999"));
        db.addContact(new Contact("Tommy", "9522222222"));
        db.addContact(new Contact("Karthik", "9533333333"));
 
        // Reading all contacts
//        Log.d("Reading: ", "Reading all contacts..");
        List<Contact> contacts = db.getAllContacts();       
 
        for (Contact cn : contacts) {
            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
                // Writing Contacts to log
            System.out.println("Name: " + log);
        }
        
        postText.setText(posts);
        
		System.out.println("onPostExecute: ");
	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		System.out.println("doInBackground: ");
		db = (DatabaseHandler) arg0[0];
		postText = (TextView) arg0[1];
		
		getStock("CONN");
		
		return null;
	}

}

