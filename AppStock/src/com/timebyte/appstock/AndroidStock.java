package com.timebyte.appstock;

import java.util.List;

import android.os.AsyncTask;
import android.widget.TextView;

public class AndroidStock extends AsyncTask {

	private BasicActivity mainActivity;
	private DatabaseHandler db;
	private KeyStatistics ks;
	
	private Stock stk;
	
	TextView postText;
	
	public AndroidStock(BasicActivity activity) {
		mainActivity = activity;
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
		mainActivity.procStock(stk, ks);
/*		
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
*/        
//        postText.setText(posts);
        
		System.out.println("onPostExecute: ");
	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		System.out.println("doInBackground: ");
		stk = (Stock) arg0[0];
		db = (DatabaseHandler) arg0[1];
		postText = (TextView) arg0[2];

		StockKeyStatistics stockKeyStatistics = new StockKeyStatistics();
		ks = stockKeyStatistics.getKs();
		
		stockKeyStatistics.getKeyStatisticsReport(stk.getSymbol());

//			posts += "\n" + symbol[i];
		PCStock pcStock = new PCStock();
		pcStock.setStk(stk);
		pcStock.getReport(stk.getSymbol(), 3);
		
//		getStock(stk.getSymbol());
		
		return null;
	}

}
