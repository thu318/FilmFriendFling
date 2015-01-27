package com.example.questionnaireapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreen extends Activity {
	/**
	 * Background music for splash screen
	 */
	MediaPlayer splash_sound;

	/**
	 * TextView in splash_screen.xml file - Name of application
	 */
	TextView splashTextView;

	/**
	 * Translate animation from right to left
	 */
	private Animation animation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash_screen);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		splash_sound = MediaPlayer.create(this, R.raw.waking_up);
		splash_sound.start();

		// locating splashTV TextView in xml file
		splashTextView = (TextView) findViewById(R.id.splashTV);
		

		// set animations variables found in the anim folder xml files
		// start text animation - displaying application's name
		animation = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.translate);
		splashTextView.startAnimation(animation);

		// Start timer Thread to make splash screen last for 5 secs,
		// during that time change the bg to the one with app name text
		// (splash_bg1) after first 2 secs
		// & display start screen after

		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent intent = new Intent(SplashScreen.this,
							StartScreen.class);
					startActivity(intent);
				}
			}
		};
		timer.start();

	}

	@Override
	protected void onPause() {
		super.onPause();
		// release background music when SplashScreen activity pauses & be
		// changed to StartScreen
		splash_sound.release();
		finish();
	}

}
