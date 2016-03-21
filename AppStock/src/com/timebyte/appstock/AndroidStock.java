package com.timebyte.appstock;

import java.util.List;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AndroidStock extends PCStock {

	private MainActivity mainActivity;
	private DatabaseHandler db;
	private TableLayout stockTable;

	TextView postText;
	
	public AndroidStock(MainActivity activity) {
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
		mainActivity.procStock(stk);
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
//		mainActivity = (MainActivity) arg0[0];
		stk = (Stock) arg0[1];
		db = (DatabaseHandler) arg0[2];
		stockTable = (TableLayout) arg0[3];
		postText = (TextView) arg0[4];

//		for (int i = 0; i < symbol.length; i++) {

		for (int i = 0; i < 1; i++) {
//			System.out.println("\n*****MMM " + symbol[i]);
			posts += "\n" + symbol[i];
			getStock(stk.getSymbol());
//			createtableRow();
		}		
		
		return null;
	}

	private void createtableRow () {
		// TODO Auto-generated method stub
        TableRow row= new TableRow(mainActivity);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView symbol = new TextView(mainActivity);
        symbol.setText("CONN");
        row.addView(symbol);

        stockTable.addView(row);
	}

}
