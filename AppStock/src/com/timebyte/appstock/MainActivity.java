package com.timebyte.appstock;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String[] symbol = {
			"MMM", "T", "ABT", "ACN", "AXP", "AAPL", "AJG", "BBT", "BCE", "BDX", "CVX", "CSCO", "KO", "CL", "DE", "DEO", "EMR", "ES", "XOM", "GE", "GPC", "IBM", "ITW", "INTC", "JNJ", "JPM", "LEG", "LMT", "LOW", "MSFT", "NVS", "OXY", "OMC", "ORCL", "PH", "PFE", "PM", "PPG", "PG", "RTN", "TROW", "TXN", "UTX", "VZ", "VFC", "WM", "WFC", "AMLP"
			};
	
	TableLayout stockTable;
	TextView titleText, postText;
	String title, posts = "";
	private int count = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stockTable = (TableLayout) findViewById(R.id.stockTable);
//		titleText = (TextView)findViewById(R.id.titleText);
		postText = (TextView)findViewById(R.id.postsText);
		
        Boolean hasSD = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        String sdLoc = "";
        if (hasSD) {
        	sdLoc = Environment.getExternalStorageDirectory().toString() + "/DCIM/SQLite/";
        }
		DatabaseHandler db = new DatabaseHandler(this, sdLoc);

		for (int i = 0; i < symbol.length; i++) {

//		for (int i = 0; i < 1; i++) {
			Stock stock = new Stock();
			stock.setSymbol(symbol[i]);
			new AndroidStock(MainActivity.this).execute(stock, db, postText);		
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void createtableRow(Stock stock) {
		// TODO Auto-generated method stub
        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView symbol = new TextView(this);
        symbol.setText(stock.getSymbol());
        row.addView(symbol);
        stockTable.addView(row);
        
        createRow("Total Revenue", stock.getTotalRevenue());
        createRow("Cost of Revenue", stock.getCostofRevenue());
        createRow("Net Income Applicable To Common Shares", stock.getNetIncomeApplicableToCommonShares());
	}

	private void createRow(String name, float[] vals) {
		// TODO Auto-generated method stub
        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

    	TextView label = new TextView(this);
    	label.setText(name);
    	row.addView(label);
    	
        for (int i = 0; i < count; i++) {
        	TextView val = new TextView(this);
        	val.setText(""+vals[i]);
        	row.addView(val);
        }
        
        stockTable.addView(row);
	}
	
	public void procStock(Stock stock) {
		createtableRow(stock);
//		createtableRow();
	}
}
