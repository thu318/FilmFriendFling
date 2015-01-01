package com.example.questionnaireapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.ToggleButton;

public class OptionScreen extends Activity {

	// private CheckBox soundOpt1;
	// private CheckBox soundOpt2;
	private ToggleButton soundOption;
	private ToggleButton musicOption;
	protected static String optionFile = "OptionsPref";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_option);

		// soundOpt1 = (CheckBox) findViewById(R.id.checkBox1);
		soundOption = (ToggleButton) findViewById(R.id.soundOption);
		musicOption = (ToggleButton) findViewById(R.id.musicOption);

		loadSavedPreferences();
	}

	private void loadSavedPreferences() {
		SharedPreferences sharedPreferences = getSharedPreferences(optionFile,
				Context.MODE_PRIVATE);

		// boolean checkBoxValue1 =
		// sharedPreferences.getBoolean("soudOpt1_Value", false);

		// if (checkBoxValue1) {
		// soundOpt1.setChecked(true);
		// } else {
		// soundOpt1.setChecked(false);
		// }

		/*
		 * load preferences and set checked-state for togglebuttons
		 */
		boolean soundOptValue = sharedPreferences.getBoolean("soundOptValue",
				false);
		if (soundOptValue)
			soundOption.setChecked(true);
		else
			soundOption.setChecked(false);

		boolean musicOptValue = sharedPreferences.getBoolean("musicOptValue",
				false);
		if (musicOptValue)
			musicOption.setChecked(true);
		else
			musicOption.setChecked(false);

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		// don't show
		return false;
	}

	public void save(View view) {

		Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
		SharedPreferences sharedPreferences = getSharedPreferences(optionFile,
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();

		editor.putBoolean("soundOptValue", soundOption.isChecked());
		editor.putBoolean("musicOptValue", musicOption.isChecked());
		editor.commit();

		// Intent intent = new Intent(OptionScreen.this, StartScreen.class);
		// startActivity(intent);
	}
}
