package com.app2go.app2go;

public class Quote {

	private String[] symbol = new String[1];
	private float[] price  = new float[1];
	private float[] volume  = new float[1];
	
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
	
}
