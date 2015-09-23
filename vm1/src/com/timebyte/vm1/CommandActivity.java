package com.timebyte.vm1;

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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.command_activity);
		
		final Button commandContinueButton = (Button) this.findViewById(R.id.cmd_continue);
		commandContinueButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.mainActivity.commandRecord(Constants.ANSWER_CONTINUE);
				onBackPressed();
			}			
		});

		final Button commandDetailButton = (Button) this.findViewById(R.id.cmd_detail);
		commandDetailButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.mainActivity.commandRecord(Constants.ANSWER_DETAIL);
				onBackPressed();
			}			
		});
		
		final Button commandSkipButton = (Button) this.findViewById(R.id.cmd_skip);
		commandSkipButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.mainActivity.commandRecord(Constants.ANSWER_SKIP);
				onBackPressed();
			}			
		});

		final Button commandStopButton = (Button) this.findViewById(R.id.cmd_stop);
		commandStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.mainActivity.commandRecord(Constants.ANSWER_STOP);
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
		
		final Button cancelButton = (Button) this.findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}			
		});
    }

}
