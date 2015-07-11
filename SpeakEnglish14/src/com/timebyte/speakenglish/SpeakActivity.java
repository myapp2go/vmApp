package com.timebyte.speakenglish;

import android.view.View;
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

	protected void hidePronunciationAll() {
		errorWord.setVisibility(View.GONE);
		errTry.setVisibility(View.GONE);
		errNext.setVisibility(View.GONE);
		mouth1.setVisibility(View.GONE);
		mouth1Type.setVisibility(View.GONE);
		mouth1Def.setVisibility(View.GONE);
		mouth2.setVisibility(View.GONE);
		mouth2Type.setVisibility(View.GONE);
		mouth2Def.setVisibility(View.GONE);
		mouth3.setVisibility(View.GONE);
		mouth3Type.setVisibility(View.GONE);
		mouth3Def.setVisibility(View.GONE);
		mouth4.setVisibility(View.GONE);
		mouth4Type.setVisibility(View.GONE);
		mouth4Def.setVisibility(View.GONE);
		mouth5.setVisibility(View.GONE);
		mouth5Type.setVisibility(View.GONE);
		mouth5Def.setVisibility(View.GONE);
	}

	protected void showPronunciationBasic() {
		errorWord.setVisibility(View.VISIBLE);
		errTry.setVisibility(View.VISIBLE);
		errNext.setVisibility(View.VISIBLE);
		mouth1.setVisibility(View.VISIBLE);
		mouth1Type.setVisibility(View.VISIBLE);
		mouth2.setVisibility(View.VISIBLE);
		mouth2Type.setVisibility(View.VISIBLE);
		mouth3.setVisibility(View.VISIBLE);
		mouth3Type.setVisibility(View.VISIBLE);
		mouth4.setVisibility(View.VISIBLE);
		mouth4Type.setVisibility(View.VISIBLE);
		mouth5.setVisibility(View.VISIBLE);
		mouth5Type.setVisibility(View.VISIBLE);
	}
}
