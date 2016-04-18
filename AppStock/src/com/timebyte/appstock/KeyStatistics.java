package com.timebyte.appstock;

public class KeyStatistics {

	private float[] returnonEquity = new float[1];
	private float[] avgVol3M  = new float[1];
	private float[] sharesOutstanding  = new float[1];
	
	public float[] getReturnonEquity() {
		return returnonEquity;
	}
	public void setReturnonEquity(float[] returnonEquity) {
		this.returnonEquity = returnonEquity;
	}
	public float[] getAvgVol3M() {
		return avgVol3M;
	}
	public void setAvgVol3M(float[] avgVol3M) {
		this.avgVol3M = avgVol3M;
	}
	public float[] getSharesOutstanding() {
		return sharesOutstanding;
	}
	public void setSharesOutstanding(float[] sharesOutstanding) {
		this.sharesOutstanding = sharesOutstanding;
	}
	

	
}
