package com.app2go.app2go;

public class Quote {

	private int quoteSize;
	private String[] symbol;
	private float[] price;
	private String[] arrow;
	private float[] change;
	private float[] target;
	private float[] volume;

	public Quote(int count) {
		super();
		symbol = new String[count];
		price  = new float[count];
		arrow = new String[count];
		change  = new float[count];
		volume  = new float[count];
		target  = new float[count];
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

	public float[] getTarget() {
		return target;
	}

	public void setTarget(float[] target) {
		this.target = target;
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
	
}
