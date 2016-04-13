/**
 * 1. extends PCStock
 * 2. remove command on count
 */
package com.timebyte.appstock;

public class MainActivity extends BasicActivity {

//	public int count = 4;

	private void sf(float value) {
		String v = "\t" + value;
		System.out.print(v);
		if (v.length() < 9) {
			System.out.print("\t");
		}
	}
	
	public static void main(String[] args) {
		String name = "PFE";
		int count = 4;
		
		PCStock pcStock = new PCStock();
		Stock stk =pcStock.createStock();
		pcStock.getReport(name, count);
		
		MainActivity obj = new MainActivity();

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
		System.out.print("\n% EPS\t\t\t\t");
		obj.calEPS(stk);
		System.out.print("\n% ROE\t\t\t\t");
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
	
	public float[] calROE(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = stock.getNetIncomeApplicableToCommonShares()[i]/stock.getTotalStockholderEquity()[i]*100;
			sf(vals[i]);
		}
		return vals;
	}
}
