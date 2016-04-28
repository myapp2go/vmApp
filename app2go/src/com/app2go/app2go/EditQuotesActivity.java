package com.app2go.app2go;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EditQuotesActivity extends Activity {
	protected SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editquotes_activity);

		sharedPreferences = getApplicationContext().getSharedPreferences(
				Constants.QUOTE_SHARE_PREFERENCES, MODE_PRIVATE);

		((TextView)findViewById(R.id.sleepTime)).setText(""+MainActivity.sleepTime);
		String str = SharedPreferencesActivity.quoteList;
		if (str != null && str.length() > 1) {
			((TextView)findViewById(R.id.quoteList)).setText(SharedPreferencesActivity.quoteList);
		}
		
		final Button saveQuoteButton = (Button) this.findViewById(R.id.saveQuote);
		saveQuoteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				savePreference();
				onBackPressed();
			}
		});

		final Button cancelButton = (Button) this.findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	private void savePreference() {
		Editor editor = sharedPreferences.edit();
		editor.putString("readOPtion", Constants.READ_OPTION_SUBJECT_ONLY);
		editor.putInt("increment", 10);

		String FILENAME = "pcStockQuotes";

		File folder = new File(Environment.getExternalStorageDirectory(),
				Environment.DIRECTORY_DCIM + "/Quotes");
		folder.mkdirs();

		FileOutputStream fos;
		try {
			folder.createNewFile();
			// fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos = new FileOutputStream(new File(folder, FILENAME));

			setContact(fos, editor, R.id.sleepTime, R.id.quoteList);
/*
			setContact(fos, editor, R.id.quote2, R.id.target2);
			setContact(fos, editor, R.id.quote3, R.id.target3);
			setContact(fos, editor, R.id.quote4, R.id.target4);
			setContact(fos, editor, R.id.quote5, R.id.target5);
*/
			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		editor.commit();
	}

	private void setContact(FileOutputStream fos, Editor editor, int nameId,
			int contactId) throws IOException {
		String sleep = ((TextView) findViewById(nameId)).getText().toString();
		String contact = ((TextView) findViewById(contactId)).getText()
				.toString();
		if (sleep != null && contact != null) {
			fos.write((sleep + "#" + contact).getBytes());
//			editor.putString((Constants.CONTACT_MARKER + name), contact);
		}
	}
}
