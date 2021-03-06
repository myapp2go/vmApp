package com.timebyte.appstock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
 
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
//    private static final String DATABASE_NAME = "contactsManager";
    private static final String DATABASE_LOC = "/DCIM?SQLite/";
    private static final String DATABASE_NAME = "pc.db";
    
    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
 
    // Contacts Table Columns names
    private static final String COL_ID = "id";
    private static final String COL_SYMBOL = "SYM";
    private static final String COL_TotalRevenue = "I_T_R";
    private static final String COL_CostofRevenue = "I_C_R";
    private static final String COL_ResearchDevelopment = "I_R_D";
    private static final String COL_SellingGeneralandAdministrative = "I_S_G";
    private static final String COL_NonRecurring = "I_N_R";
    
    private static final String COL_NAME = "name";
    private static final String COL_PH_NO = "phoneNumber";

    public DatabaseHandler(final Context context, String sdLoc) {
    	super(context, sdLoc+DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
       
        //3rd argument to be passed is CursorFactory instance
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + COL_ID + " INTEGER PRIMARY KEY," 
                + COL_SYMBOL + " TEXT,"
                + COL_TotalRevenue + " REAL,"
                + COL_CostofRevenue + " REAL,"
                + COL_ResearchDevelopment + " REAL,"
                + COL_SellingGeneralandAdministrative + " REAL,"
                + COL_NonRecurring + " REAL,"
        		+ COL_NAME + " TEXT,"
                + COL_PH_NO + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new contact
    void addContact(Stock contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COL_NAME, contact.getName()); // Contact Name
        values.put(COL_PH_NO, contact.getPhoneNumber()); // Contact Phone
 
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
 
    // Getting single contact
    Stock getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { COL_ID,
                COL_NAME, COL_PH_NO }, COL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Stock contact = new Stock(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return contact;
    }
 
    // Getting All Contacts
    public List<Stock> getAllContacts() {
        List<Stock> contactList = new ArrayList<Stock>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	Stock contact = new Stock();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return contactList;
    }
 
    // Updating single contact
    public int updateContact(Stock contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COL_NAME, contact.getName());
        values.put(COL_PH_NO, contact.getPhoneNumber());
 
        // updating row
        return db.update(TABLE_CONTACTS, values, COL_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }
 
    // Deleting single contact
    public void deleteContact(Stock contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COL_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
 
    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }

    class DatabaseContext extends ContextWrapper {

    	private static final String DEBUG_CONTEXT = "DatabaseContext";

    	public DatabaseContext(Context base) {
    	    super(base);
    	}

    	@Override
    	public File getDatabasePath(String name) 
    	{
    	    File sdcard = Environment.getExternalStorageDirectory();    
    	    String dbfile = sdcard.getAbsolutePath() + File.separator+ "databases" + File.separator + name;

    	    if (!dbfile.endsWith(".db"))
    	    {
    	        dbfile += ".db" ;
    	    }

    	    File result = new File(dbfile);

    	    if (!result.getParentFile().exists())
    	    {
    	        result.getParentFile().mkdirs();
    	    }

    	    if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN))
    	    {
    	        Log.w(DEBUG_CONTEXT,
    	                "getDatabasePath(" + name + ") = " + result.getAbsolutePath());
    	    }

    	    return result;
    	}

    	@Override
    	public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) 
    	{
    	    SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    	    // SQLiteDatabase result = super.openOrCreateDatabase(name, mode, factory);
    	    if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN))
    	    {
    	        Log.w(DEBUG_CONTEXT,
    	                "openOrCreateDatabase(" + name + ",,) = " + result.getPath());
    	    }
    	    return result;
    	}
    }
}