/**
 * 1. extends PCStock
 * 2. remove command on count
 * 3. uncommand df
 */
package com.timebyte.appstock;

public class MainActivity extends BasicActivity {

//	public int count = 4;

	private void sf(float value) {
		/*
		String v = "\t" + value;
		posts += v;
		System.out.print(v);
		if (v.length() < 9) {
			System.out.print("\t");
		}
		*/
	}
	
	public static void main(String[] args) {
		String name = "CONN";
		int count = 4;
		
		PCStock pcStock = new PCStock();
		Stock stk =pcStock.createStock();
		pcStock.getReport(name, count);
		
		MainActivity obj = new MainActivity();
		System.out.print("\n% of Cost of Revenue\t\t");	
		obj.calCostofRevenueRatio(stk);
		System.out.print("\nGross Profit Ratio\t\t");
		obj.calGrossProfitRatio(stk);
		System.out.print("\n% of Cost of Revenue\t\t");
		obj.calCostofRevenueRatio(stk);
		System.out.print("\nOperating Income or Loss Ratio\t");
		obj.calOperatingIncomeorLossRatio(stk);
		
//		System.out.println("&&&& " + posts);
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
}
