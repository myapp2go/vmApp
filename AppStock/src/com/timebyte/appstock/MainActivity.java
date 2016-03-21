package com.timebyte.appstock;

public class MainActivity extends BasicActivity implements StockInterface {

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
		obj.calCostofRevenueRatio(stk);
		
//		System.out.println("&&&& " + posts);
	}
	
	public float[] calCostofRevenueRatio(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = (stock.getCostofRevenue()[i] / stock.getTotalRevenue()[i]);
			sf(vals[i]);
		}
		return vals;
	}

	public float[] calGrossProfitRatio(Stock stock) {
		float[] vals = new float[4];		
		for (int i = 0; i < count; i++) {
			vals[i] = (stock.getTotalRevenue()[i] - stock.getCostofRevenue()[i])/stock.getTotalRevenue()[i];
		}
		return vals;
	}
	
}
