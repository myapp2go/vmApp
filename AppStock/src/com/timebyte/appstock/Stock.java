package com.timebyte.appstock;

import java.util.HashMap;
import java.util.Map;

public class Stock {

    //private variables
    int _id;
    
    Map<String, String[]> monthMap = new HashMap<String, String[]>();
    
    private String symbol;
    private String[] periodEnding = new String[4];
	private float[] stockPrice = new float[4];
	
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
	
    String _name;
    String _phone_number;
 
    // Empty constructor
    public Stock(){
    	monthMap.put("Jan", new String[]{"00", "01", "Feb"});
    	monthMap.put("Feb", new String[]{"01", "02", "Mar"});
    	monthMap.put("Mar", new String[]{"02", "03", "Apr"});
    	monthMap.put("Apr", new String[]{"03", "04", "May"});
    	monthMap.put("May", new String[]{"04", "05", "Jun"});
    	monthMap.put("Jun", new String[]{"05", "06", "Jul"});
    	monthMap.put("Jul", new String[]{"06", "07", "Aug"});
    	monthMap.put("Aug", new String[]{"07", "08", "Sep"});
    	monthMap.put("Sep", new String[]{"08", "09", "Oct"});
    	monthMap.put("Oct", new String[]{"09", "10", "Nov"});
    	monthMap.put("Nov", new String[]{"10", "11", "Dec"});
    	monthMap.put("Dec", new String[]{"11", "00", "Jan"});
    }
    
    // constructor
    public Stock(int id, String name, String _phone_number){
        this._id = id;
        this._name = name;
        this._phone_number = _phone_number;
    }
 
    // constructor
    public Stock(String name, String _phone_number){
        this._name = name;
        this._phone_number = _phone_number;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
 
    // setting id
    public void setID(int id){
        this._id = id;
    }
 
    // getting name
    public String getName(){
        return this._name;
    }
 
    // setting name
    public void setName(String name){
        this._name = name;
    }
 
    // getting phone number
    public String getPhoneNumber(){
        return this._phone_number;
    }
 
    // setting phone number
    public void setPhoneNumber(String phone_number){
        this._phone_number = phone_number;
    }
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public float[] getTotalRevenue() {
		return totalRevenue;
	}
	public void setTotalRevenue(float[] totalRevenue) {
		this.totalRevenue = totalRevenue;
	}
	public float[] getCostofRevenue() {
		return costofRevenue;
	}
	public void setCostofRevenue(float[] costofRevenue) {
		this.costofRevenue = costofRevenue;
	}
	public float[] getResearchDevelopment() {
		return researchDevelopment;
	}
	public void setResearchDevelopment(float[] researchDevelopment) {
		this.researchDevelopment = researchDevelopment;
	}
	public float[] getSellingGeneralandAdministrative() {
		return sellingGeneralandAdministrative;
	}
	public void setSellingGeneralandAdministrative(
			float[] sellingGeneralandAdministrative) {
		this.sellingGeneralandAdministrative = sellingGeneralandAdministrative;
	}
	public float[] getNonRecurring() {
		return nonRecurring;
	}
	public void setNonRecurring(float[] nonRecurring) {
		this.nonRecurring = nonRecurring;
	}
	public float[] getTotalOtherIncomeExpensesNet() {
		return totalOtherIncomeExpensesNet;
	}
	public void setTotalOtherIncomeExpensesNet(float[] totalOtherIncomeExpensesNet) {
		this.totalOtherIncomeExpensesNet = totalOtherIncomeExpensesNet;
	}
	public float[] getEarningsBeforeInterestAndTaxes() {
		return earningsBeforeInterestAndTaxes;
	}
	public void setEarningsBeforeInterestAndTaxes(
			float[] earningsBeforeInterestAndTaxes) {
		this.earningsBeforeInterestAndTaxes = earningsBeforeInterestAndTaxes;
	}
	public float[] getInterestExpense() {
		return interestExpense;
	}
	public void setInterestExpense(float[] interestExpense) {
		this.interestExpense = interestExpense;
	}
	public float[] getIncomeBeforeTax() {
		return incomeBeforeTax;
	}
	public void setIncomeBeforeTax(float[] incomeBeforeTax) {
		this.incomeBeforeTax = incomeBeforeTax;
	}
	public float[] getIncomeTaxExpense() {
		return incomeTaxExpense;
	}
	public void setIncomeTaxExpense(float[] incomeTaxExpense) {
		this.incomeTaxExpense = incomeTaxExpense;
	}
	public float[] getMinorityInterest() {
		return minorityInterest;
	}
	public void setMinorityInterest(float[] minorityInterest) {
		this.minorityInterest = minorityInterest;
	}
	public float[] getNetIncomeApplicableToCommonShares() {
		return netIncomeApplicableToCommonShares;
	}
	public void setNetIncomeApplicableToCommonShares(
			float[] netIncomeApplicableToCommonShares) {
		this.netIncomeApplicableToCommonShares = netIncomeApplicableToCommonShares;
	}
	public float[] getCashAndCashEquivalents() {
		return cashAndCashEquivalents;
	}
	public void setCashAndCashEquivalents(float[] cashAndCashEquivalents) {
		this.cashAndCashEquivalents = cashAndCashEquivalents;
	}
	public float[] getShortTermInvestments() {
		return shortTermInvestments;
	}
	public void setShortTermInvestments(float[] shortTermInvestments) {
		this.shortTermInvestments = shortTermInvestments;
	}
	public float[] getCommonStock() {
		return commonStock;
	}
	public void setCommonStock(float[] commonStock) {
		this.commonStock = commonStock;
	}
	public float[] getPreferredStock() {
		return preferredStock;
	}
	public void setPreferredStock(float[] preferredStock) {
		this.preferredStock = preferredStock;
	}
	public float[] getRetainedEarnings() {
		return retainedEarnings;
	}
	public void setRetainedEarnings(float[] retainedEarnings) {
		this.retainedEarnings = retainedEarnings;
	}
	public float[] getCapitalSurplus() {
		return capitalSurplus;
	}
	public void setCapitalSurplus(float[] capitalSurplus) {
		this.capitalSurplus = capitalSurplus;
	}
	public float[] getOtherStockholderEquity() {
		return otherStockholderEquity;
	}
	public void setOtherStockholderEquity(float[] otherStockholderEquity) {
		this.otherStockholderEquity = otherStockholderEquity;
	}
	public float[] getTotalStockholderEquity() {
		return totalStockholderEquity;
	}
	public void setTotalStockholderEquity(float[] totalStockholderEquity) {
		this.totalStockholderEquity = totalStockholderEquity;
	}
	public float[] getDepreciation() {
		return depreciation;
	}
	public void setDepreciation(float[] depreciation) {
		this.depreciation = depreciation;
	}
	public String[] getPeriodEnding() {
		return periodEnding;
	}
	public void setPeriodEnding(String[] periodEnding) {
		this.periodEnding = periodEnding;
	}

	public float[] getStockPrice() {
		return stockPrice;
	}

	public void setStockPrice(float[] stockPrice) {
		this.stockPrice = stockPrice;
	}

	public Map<String, String[]> getMonthMap() {
		return monthMap;
	}

	public void setMonthMap(Map<String, String[]> monthMap) {
		this.monthMap = monthMap;
	}


}

