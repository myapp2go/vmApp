package com.timebyte.appstock;

import java.util.List;
import android.widget.TextView;

public class AndroidStock extends PCStock {

	private DatabaseHandler db;
	TextView postText;
	
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
        
        postText.setText(posts);
        
		System.out.println("onPostExecute: ");
	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		System.out.println("doInBackground: ");
		db = (DatabaseHandler) arg0[0];
		postText = (TextView) arg0[1];
		
		for (int i = 0; i < symbol.length; i++) {
//			System.out.println("\n*****MMM " + symbol[i]);
			posts += "\n" + symbol[i];
			getStock(symbol[i]);
		}		
		
		return null;
	}

}
