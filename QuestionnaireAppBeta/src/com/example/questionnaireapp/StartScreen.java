/*
 * By : Ireneo Mercado
 * Update By: Thu Huong Dao
 * 
 * Creates a screen for the user to navigate between starting 
 * the questionnaire, resuming a questionnaire and the options 
 * screens. 
 */
package com.example.questionnaireapp;

import com.example.questionnaireapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

//import android.widget.Toast;

/**
 * An activity class where the user could navigate between starting the
 * questionnaire, resuming a questionnaire and the options screens.
 * 
 */
public class StartScreen extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_start);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);

		return false;
	}

	/**
	 * Starts the main activity of the app which is the questionnaire.
	 * 
	 * @see MainActivity.java
	 * @see XML file activity_start.xml android:onClick="start"
	 * @param view
	 *            - XML fileOnClickListener interface requires a parameter of
	 *            type View.
	 */
	public void start(View view) {

		// Toast.makeText(this, "start pressed",Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("ResumePressed", false);
		startActivity(intent);
	}

	/**
	 * Opens the options screen of the app where user could change a couple of
	 * settings.
	 * 
	 * @see OptionsScreen.java
	 * @see XML file activity_startn.xml android:onClick="option"
	 * @param view
	 *            - XML fileOnClickListener interface requires a parameter of
	 *            type View.
	 */
	public void options(View view) {

		// Toast.makeText(this, "options pressed", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, OptionScreen.class);
		startActivity(intent);
	}

	/**
	 * Resumes application progress to users previously saved location on the
	 * questionnaire.
	 * 
	 * @see MainActivity.java
	 * @see XML file activity_startn.xml android:onClick="resume_progress"
	 * @param view
	 *            - XML fileOnClickListener interface requires a parameter of
	 *            type View.
	 */
	public void resume_progress(View view) {

		// Toast.makeText(this, "resume pressed", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("ResumePressed", true);
		startActivity(intent);
	}
}
