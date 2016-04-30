package com.app2go.app2go;

public class Quote {

	private int quoteSize;
	private String[] symbol;
	private float[] price;
	private float[] yesterdayClose;
	private String[] arrow;
	private float[] change;
	private float[] aboveTarget;
	private float[] belowTarget;
	private float[] volume;

	public Quote(int count) {
		super();
		symbol = new String[count];
		price  = new float[count];
		yesterdayClose  = new float[count];
		arrow = new String[count];
		change  = new float[count];
		volume  = new float[count];
		aboveTarget  = new float[count];
		belowTarget  = new float[count];
	}
	
	public String[] getSymbol() {
		return symbol;
	}
	public void setSymbol(String[] symbol) {
		this.symbol = symbol;
	}
	public float[] getPrice() {
		return price;
	}
	public void setPrice(float[] price) {
		this.price = price;
	}
	public float[] getVolume() {
		return volume;
	}
	public void setVolume(float[] volume) {
		this.volume = volume;
	}

	public int getQuoteSize() {
		return quoteSize;
	}

	public void setQuoteSize(int quoteSize) {
		this.quoteSize = quoteSize;
	}

	public String[] getArrow() {
		return arrow;
	}

	public void setArrow(String[] arrow) {
		this.arrow = arrow;
	}

	public float[] getChange() {
		return change;
	}

	public void setChange(float[] change) {
		this.change = change;
	}

	public float[] getYesterdayClose() {
		return yesterdayClose;
	}

	public void setYesterdayClose(float[] yesterdayClose) {
		this.yesterdayClose = yesterdayClose;
	}

	public float[] getAboveTarget() {
		return aboveTarget;
	}

	public void setAboveTarget(float[] aboveTarget) {
		this.aboveTarget = aboveTarget;
	}

	public float[] getBelowTarget() {
		return belowTarget;
	}

	public void setBelowTarget(float[] belowTarget) {
		this.belowTarget = belowTarget;
	}
	
}
