package com.pc.vm;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class DebuggingActivity extends ListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        	
        ArrayList<String> recognizerResult = (ArrayList<String>) getIntent().getStringArrayListExtra("command_list");
        String[] cmdArray = new String[recognizerResult.size()];
        for (int i = 0; i < recognizerResult.size(); i++) {
        	cmdArray[i] = recognizerResult.get(i);
        }
        /*
        debugListView = (ListView) findViewById(R.id.debugging);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cmdArray);
        debugListView.setAdapter(arrayAdapter);
        */
        
        setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, cmdArray));
    }
}
