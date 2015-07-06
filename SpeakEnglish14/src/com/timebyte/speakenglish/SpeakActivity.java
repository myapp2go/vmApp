package com.timebyte.speakenglish;

import android.widget.Button;
import android.widget.TextView;

public class SpeakActivity extends MainActivity {

	@Override
	protected void initDefinitionData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initDefinitionView() {
		// TODO Auto-generated method stub
		errorWord = (TextView) findViewById(R.id.errorWord);
		errTry = (Button) findViewById(R.id.errTry);
		errNext = (Button) findViewById(R.id.errNext);
	}

	@Override
	protected void procError(int index) {
		// TODO Auto-generated method stub
		
	}

}
