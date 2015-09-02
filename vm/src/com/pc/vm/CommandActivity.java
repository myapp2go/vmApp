package com.pc.vm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CommandActivity extends Activity {
	
	protected SharedPreferences sharedPreferences;
	
	private Set<String> command1 = new HashSet<String>();
	private Set<String> command2 = new HashSet<String>();
	private Set<String> command3 = new HashSet<String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.command_activity);
		
		final Button commandButton1 = (Button) this.findViewById(R.id.command1);
		commandButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.mainActivity.commandRecord(Constants.ANSWER_CONTINUE);
				onBackPressed();
			}			
		});

		final Button commandButton2 = (Button) this.findViewById(R.id.command2);
		commandButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.mainActivity.commandRecord(Constants.ANSWER_STOP);
				onBackPressed();
			}			
		});
		
		final Button commandButton3 = (Button) this.findViewById(R.id.command3);
		commandButton3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.mainActivity.commandRecord(Constants.ANSWER_SKIP);
				onBackPressed();
			}			
		});
		
		final Button saveButton = (Button) this.findViewById(R.id.save);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.mainActivity.commandRecord(Constants.ANSWER_SAVE);
				onBackPressed();
			}			
		});

		final Button cleanButton = (Button) this.findViewById(R.id.clean);
		cleanButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.mainActivity.commandRecord(Constants.ANSWER_CLEAN);
				onBackPressed();
			}			
		});
    }

}
