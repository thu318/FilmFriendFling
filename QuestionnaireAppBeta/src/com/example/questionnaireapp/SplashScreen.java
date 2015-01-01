package com.example.questionnaireapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreen extends Activity {

	MediaPlayer splash_sound;
	TextView splashTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
			
		setContentView(R.layout.splash_screen);

		splash_sound = MediaPlayer.create(this, R.raw.waking_up);
		splash_sound.start();
		
		splashTextView = (TextView) findViewById(R.id.splashTV);
		splashTextView.startAnimation(AnimationUtils.loadAnimation(SplashScreen.this, android.R.anim.slide_in_left));
		
		Thread timer = new Thread(){
			public void run(){
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally{
					Intent intent = new Intent(SplashScreen.this, StartScreen.class);
					startActivity(intent);
				}
			}
		};
		timer.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		splash_sound.release();
		finish();
	}

}
