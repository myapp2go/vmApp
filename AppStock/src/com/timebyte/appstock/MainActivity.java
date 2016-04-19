/**
 * 1. extends PCStock
 * 2. remove command on count
 * 3. remove command on main
 */
package com.timebyte.appstock;

public class MainActivity extends BasicActivity {

	public static int count = 3;
	public static float[] stockPrice = {(float)58.65, (float)49.96, (float)12.26, (float)18.1};

	private void sf(float value) {
		String v = "\t" + value;
		System.out.print(v);
		if (v.length() < 9) {
			System.out.print("\t");
		}
	}
	
	public static void main(String[] args) {
		String name = "LCI";
		
		PCStock pcStock = new PCStock();
		pcStock.createStk();
		Stock stk = pcStock.getStk();
		
		StockKeyStatistics stockKeyStatistics = new StockKeyStatistics();
		KeyStatistics ks = stockKeyStatistics.getKs();

		MainActivity obj = new MainActivity();
		
		System.out.print(name + " Annual Report");
		stockKeyStatistics.getKeyStatisticsReport(name);
		
		pcStock.getReport(name, 3);
		obj.checkSharesOutstanding(stk, ks);
		pcReport(obj, stk);
		
		count = 4;
		System.out.print("\n\nQuarterly Report");
		pcStock.getReport(name, 4);
		obj.checkSharesOutstanding(stk, ks);
		pcReport(obj, stk);
	}
	
	public void checkSharesOutstanding(Stock stk, KeyStatistics ks) {
		float ksShare = ks.getSharesOutstanding()[0];
		float[] stkShare = stk.getCommonStock();
		
		float diff = stkShare[0] / ksShare;
		if ((diff > 1.05) || (diff < 0.95)) {
			for (int i = 0; i < 4; i++) {
				stkShare[i] = ksShare;
			}
		}
	}

	private static void pcReport(MainActivity obj, Stock stk) {
		System.out.print("\nTotal Revenue\t\t\t");	
		obj.printStock(stk.getTotalRevenue());
		System.out.print("\nCost of Revenue\t\t\t");	
		obj.printStock(stk.getCostofRevenue());
		
		System.out.print("\n% of Cost of Revenue\t\t");	
		obj.calCostofRevenueRatio(stk);
		System.out.print("\n% of Gross Profit\t\t");
		obj.calGrossProfitRatio(stk);
		System.out.print("\n% of Operating Expenses\t\t");
		obj.calOperatingExpensesRatio(stk);
		System.out.print("\n% Operating Income or Loss Ratio");
		obj.calOperatingIncomeorLossRatio(stk);
		System.out.print("\n% Net Income Appl To Common Sh Ratio");
		obj.calNetIncomeApplToCommonSharesRatio(stk);
		System.out.print("\n% Interest Ratio\t\t");
		obj.calnterestRatio(stk);
		System.out.print("\nEPS\t\t\t\t");
		obj.calEPS(stk);
		System.out.print("\nPE\t\t\t\t");
		obj.calPE(stk);
		System.out.print("\nPrice To Book\t\t\t");
		obj.calPriceToBook(stk);
		System.out.print("\nStockholderEquity Per Share\t");
		obj.calStockholderEquityPerShare(stk);
		System.out.print("\nROE\t\t\t\t");
		obj.calROE(stk);
		
		System.out.print("\nNet Income App To Common Shares\t");	
		obj.printStock(stk.getNetIncomeApplicableToCommonShares());
	}

	private void printStock(float[] vals) {
		for (int i = 0; i < count; i++) {
			sf(vals[i]);
		}
	}

	public float[] calGrossProfitRatio(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = (stock.getTotalRevenue()[i] - stock.getCostofRevenue()[i])/stock.getTotalRevenue()[i];
			sf(vals[i]);
		}
		return vals;
	}
	
	public float[] calCostofRevenueRatio(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = (stock.getCostofRevenue()[i] / stock.getTotalRevenue()[i]);
			sf(vals[i]);
		}
		return vals;
	}

	public float[] calOperatingExpensesRatio(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = ((stock.getResearchDevelopment()[i]+stock.getSellingGeneralandAdministrative()[i]+stock.getNonRecurring()[i]) / stock.getTotalRevenue()[i]);
			sf(vals[i]);
		}
		return vals;
	}

	public float[] calOperatingIncomeorLossRatio(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = ((stock.getTotalRevenue()[i] - stock.getCostofRevenue()[i]) - (stock.getResearchDevelopment()[i]+stock.getSellingGeneralandAdministrative()[i]+stock.getNonRecurring()[i]))/stock.getTotalRevenue()[i];
			sf(vals[i]);
		}
		return vals;
	}
	
	public float[] calNetIncomeApplToCommonSharesRatio(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = (stock.getNetIncomeApplicableToCommonShares()[i])/stock.getTotalRevenue()[i];
			sf(vals[i]);
		}
		return vals;
	}
	
	public float[] calnterestRatio(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = (stock.getEarningsBeforeInterestAndTaxes()[i]/stock.getInterestExpense()[i]);
			sf(vals[i]);
		}
		return vals;
	}

	public float[] calEPS(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = stock.getNetIncomeApplicableToCommonShares()[i]/(stock.getCommonStock()[i]*1000);
			sf(vals[i]);
		}
		return vals;
	}

	public float[] calPE(Stock stock) {
		float[] vals = new float[4];
		float ratio = (float)1.0;
		if (count == 4) {
			ratio = (float)0.25;
		}
		for (int i = 0; i < count; i++) {
			vals[i] = stock.getStockPrice()[i] / (stock.getNetIncomeApplicableToCommonShares()[i]/(stock.getCommonStock()[i]*1000)) * ratio;
			sf(vals[i]);
		}
		return vals;
	}

	public float[] calPriceToBook(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = stock.getStockPrice()[i]/(stock.getTotalStockholderEquity()[i]/(stock.getCommonStock()[i]*1000));
			sf(vals[i]);
		}
		return vals;
	}
	
	public float[] calStockholderEquityPerShare(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = stock.getTotalStockholderEquity()[i]/(stock.getCommonStock()[i]*1000);
			sf(vals[i]);
		}
		return vals;
	}
	
	public float[] calROE(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = stock.getNetIncomeApplicableToCommonShares()[i]/stock.getTotalStockholderEquity()[i]*100;
			sf(vals[i]);
		}
		return vals;
	}
}
