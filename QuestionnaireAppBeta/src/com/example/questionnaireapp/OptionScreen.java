/*
 * By : Ireneo Mercado 
 * Update By: Thu Huong Dao
 * 
 */

package com.example.questionnaireapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * An activity class where the user could set their music preference in the app
 */
public class OptionScreen extends Activity {

	private ToggleButton soundOption;
	private ToggleButton musicOption;
	protected static String optionFile = "OptionsPref";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_option);

		soundOption = (ToggleButton) findViewById(R.id.soundOption);
		musicOption = (ToggleButton) findViewById(R.id.musicOption);

		loadSavedPreferences();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		// don't show
		return false;
	}

	/**
	 * Loads users preferences and set checked-state for the toggle buttons in
	 * the activity.
	 * 
	 *  @see android.content.SharedPreferences
	 */
	private void loadSavedPreferences() {
		SharedPreferences sharedPreferences = getSharedPreferences(optionFile,
				Context.MODE_PRIVATE);

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

	/**
	 * Saves user preferences using Android SharedPreferences class.
	 * 
	 * @see android.content.SharedPreferences
	 * @param view
	 *            to show "Saved" (toast message) on the screen activity
	 */
	public void save(View view) {

		Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
		SharedPreferences sharedPreferences = getSharedPreferences(optionFile,
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();

		editor.putBoolean("soundOptValue", soundOption.isChecked());
		editor.putBoolean("musicOptValue", musicOption.isChecked());
		editor.commit();

		// exit to start screen after saving
		// Intent intent = new Intent(OptionScreen.this, StartScreen.class);
		// startActivity(intent);
	}
}
