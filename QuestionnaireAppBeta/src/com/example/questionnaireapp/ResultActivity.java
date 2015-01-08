package com.example.questionnaireapp;

import java.util.ArrayList;

import com.example.questinnaireapp.TouchImage.TouchImageView;
//going through image gallery
import com.example.questionnaireapp.R;
//links
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import ie.nuim.dateapp.questionnaire.util.SystemUiHider;

public class ResultActivity extends Activity {
	/**
	 * Outcomes in the switch statement for
	 * displaying the results of the users questionnaire.
	 * Names used are of the future ideal characters for the user in the
	 * questionnaire
	*/
	private enum Characters {
		Plywood, Jar_Jar_Binks, God, Blair_Witch, Crazy_Eyes, James_Bond, Batman, Leatherface, Lassie, Xena, Generic_Damsel, Bernadette;
	}

	/**
	 * variable for the first one in the list of characters with the highest score
	*/
	private String value;
	/**
	 *  link of the character used in options menu
	 */
	private String imdblink = "";
	/**
	 *  dynamic image of the results
	 */
	private TouchImageView image;
	/**
	 * the set of images the character has
	 */
	private static int[] images;
	/**
	 *  the current place of the image gallery of the character selected
	 */
	int index;
	
	

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = false;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		initVariables();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		// show options
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//
		if (id == R.id.new_questionnaire) {
			Intent intent = new Intent(this, StartScreen.class);
			startActivity(intent);
			finish();
		}
		//
		if (id == R.id.share) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, "My ideal date is " + value
					+ " what's yours?  http://linkofapphere.com ");
			startActivity(Intent.createChooser(shareIntent,
					"Share your thoughts"));
		}
		//
		if (id == R.id.imdb) {
			final String link = imdblink;
			Toast.makeText(this, "Opening stalk page.. : " + link,
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Intent.ACTION_VIEW,
			// since you have to use a final variable here
					Uri.parse(link));
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	//
	//
	//
	private void initVariables() {
		
		// text for what happened on the date
		// TextView t = (TextView)findViewById(R.id.textResult);
		// get ideal date intent from main activity next button
		Bundle b = getIntent().getExtras();
		// list of characters with high scores
		ArrayList<String> idealDate = b.getStringArrayList("maxCharacters");
		// imdb button for the character link
		// Button imdbButton;
		// link for sharing the results on social networking sites
		// ImageView shareImage = (ImageView) findViewById(R.id.imageView2);
		// Zoom image using TouchImageView.java
		image = (TouchImageView) findViewById(R.id.imageView1);
		// set the index of the gallery of the character to the first on
		index = 0;
		// get the first one in the list of characters with the highest score
		value = idealDate.get(0);
		// switch statement
		Characters character = Characters.valueOf(value);
		// dynamic Imdb link
		String charImdbLink = "";

		switch (character) {
		case Plywood:
			// set comic strip image
			images = new int[3];
			images[0] = R.drawable.character1;
			images[1] = R.drawable.character1_2;
			images[2] = R.drawable.character1_3;
			setCurrentImage();
			// image.setImageResource(R.drawable.character1);
			charImdbLink = "http://en.wikipedia.org/wiki/Plan_9_from_Outer_Space";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you had fun sword figthing with " + value);
			break;
		case Jar_Jar_Binks:
			images = new int[3];
			images[0] = R.drawable.character2;
			images[1] = R.drawable.character2_2;
			images[2] = R.drawable.character2_3;
			setCurrentImage();
			// image.setImageResource(R.drawable.character2);
			charImdbLink = "http://en.wikipedia.org/wiki/Jar_Jar_Binks";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you kissed " + value + " on the cheek");
			break;
		case God:
			images = new int[3];
			images[0] = R.drawable.character3;
			images[1] = R.drawable.character3_2;
			images[2] = R.drawable.character3_3;
			setCurrentImage();
			// image.setImageResource(R.drawable.character3);
			charImdbLink = "http://en.wikipedia.org/wiki/Morgan_Freeman";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you had a crazy night with " + value);
			break;
		case Blair_Witch:
			images = new int[3];
			images[0] = R.drawable.character4;
			images[1] = R.drawable.character4_2;
			images[2] = R.drawable.character4_3;
			setCurrentImage();
			// image.setImageResource(R.drawable.character4);
			charImdbLink = "http://en.wikipedia.org/wiki/The_Blair_Witch_Project";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText(value +
			// " spiked your drink and you couldn't remember what you did");
			break;
		case Crazy_Eyes:
			images = new int[2];
			images[0] = R.drawable.character5;
			images[1] = R.drawable.character7_2;
			setCurrentImage();
			charImdbLink = "http://en.wikipedia.org/wiki/Crazy_Eyes_%28character%29";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you had an argument with " + value + " about toads");
			break;
		case James_Bond:
			images = new int[2];
			images[0] = R.drawable.character6;
			images[1] = R.drawable.character7_2;
			setCurrentImage();
			charImdbLink = "http://en.wikipedia.org/wiki/James_Bond";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText(value + "carried you home because you passed out" );
			break;
		case Batman:
			images = new int[2];
			images[0] = R.drawable.character7;
			images[1] = R.drawable.character7_2;
			setCurrentImage();
			// image.setImageResource(R.drawable.character7);
			charImdbLink = "http://en.wikipedia.org/wiki/Batman";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you had ice cream with " + value +
			// " and shared funny childhood stories");
			break;
		case Leatherface:
			images = new int[2];
			images[0] = R.drawable.character7;
			images[1] = R.drawable.character7_2;
			setCurrentImage();
			// image.setImageResource(R.drawable.character7);
			charImdbLink = "http://en.wikipedia.org/wiki/Leatherface";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you had ice cream with " + value +
			// " and shared funny childhood stories");
			break;
		case Lassie:
			images = new int[2];
			images[0] = R.drawable.character7;
			images[1] = R.drawable.character7_2;
			setCurrentImage();
			// image.setImageResource(R.drawable.character7);
			charImdbLink = "http://en.wikipedia.org/wiki/Lassie";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you had ice cream with " + value +
			// " and shared funny childhood stories");
			break;
		case Xena:
			images = new int[2];
			images[0] = R.drawable.character7;
			images[1] = R.drawable.character7_2;
			setCurrentImage();
			// image.setImageResource(R.drawable.character7);
			charImdbLink = "http://en.wikipedia.org/wiki/Xena:_Warrior_Princess";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you had ice cream with " + value +
			// " and shared funny childhood stories");
			break;
		case Generic_Damsel:
			images = new int[2];
			images[0] = R.drawable.character7;
			images[1] = R.drawable.character7_2;
			setCurrentImage();
			// image.setImageResource(R.drawable.character7);
			charImdbLink = "http://www.imdb.com/name/nm0813812/";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you had ice cream with " + value +
			// " and shared funny childhood stories");
			break;
		case Bernadette:
			images = new int[2];
			images[0] = R.drawable.character7;
			images[1] = R.drawable.character7_2;
			setCurrentImage();
			// image.setImageResource(R.drawable.character7);
			charImdbLink = "http://www.imdb.com/name/nm0813812/";
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			// t.setText("you had ice cream with " + value +
			// " and shared funny childhood stories");
			break;
		default:
			break;
		}

		// to have a different action for each button dynamically
		final String link = charImdbLink;
		imdblink = link;

		// Set next image with each button click on the image
		// image.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// setCurrentImage();
		// // String video_id="cxLG2wtE7TM";
		// // Intent intent = new Intent(Intent.ACTION_VIEW,
		// Uri.parse("vnd.youtube:" + video_id ));
		// // startActivity(intent);
		//
		// }
		// });

		// imdbButton=(Button)findViewById(R.id.button1);
		// imdbButton.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		// Intent intent = new Intent(Intent.ACTION_VIEW,
		// //since you have to use a final variable here
		// Uri.parse(link));
		// startActivity(intent);
		// }
		// });

		// if the orientation is landscape hide the action bar otherwise show
		if (getResources().getConfiguration().orientation == 2) {
			getActionBar().hide();
		} else {
			getActionBar().show();
			// setup description
			setDescription();
		}

		// --------------------------------------------------------------------
		// full screen code below
		// --------------------------------------------------------------------

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View button1 = findViewById(R.id.imageButton1);
		final View button2 = findViewById(R.id.imageButton2);
		final View button3 = findViewById(R.id.imageButton3);
		View contentView = image;

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
							// hide button only on portrait mode
							if (getResources().getConfiguration().orientation == 1) {
								button1.animate()
										.translationX(
												visible ? 0 : -mControlsHeight)
										.setDuration(mShortAnimTime);
								button2.animate()
										.translationY(
												visible ? 0 : mControlsHeight)
										.setDuration(mShortAnimTime);
								button3.animate()
										.translationX(
												visible ? 0 : mControlsHeight)
										.setDuration(mShortAnimTime);
							}
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
							if (getResources().getConfiguration().orientation == 1) {
								button1.setVisibility(visible ? View.VISIBLE
										: View.GONE);
								button2.setVisibility(visible ? View.VISIBLE
										: View.GONE);
								button3.setVisibility(visible ? View.VISIBLE
										: View.GONE);
							}
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		findViewById(R.id.next_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						setCurrentImage();
						// String video_id="cxLG2wtE7TM";
						// Intent intent = new Intent(Intent.ACTION_VIEW,
						// Uri.parse("vnd.youtube:" + video_id ));
						// startActivity(intent);

					}
				});

	}

	private void setDescription() {
		// create a textview variable in this view
		// and set this text view as the text view set in XML file
		// TextView d = (TextView)findViewById(R.id.textView1);
		// String description = "Your ideal date is " + value + "\n\n" +
		// "Rotate the screen to make the comic strip of how your date went larger"
		// + "\n\n" +
		// "Share/find out more/try again by pressing the buttons above :)" +
		// "\n\n" +
		// "Thanks for playing !!";
		// d.setText(description);
		// d.set
	}

	//
	//
	//
	private void setCurrentImage() {
		image.setImageResource(images[index]);
		index = (++index % images.length);
	}

	// re-launch start screen
	public void retry(View view) {
		Intent intent = new Intent(this, StartScreen.class);
		startActivity(intent);
		finish();
	}

	// opening the web page of the character chosen
	public void imdb(View view) {
		final String link = imdblink;
		Toast.makeText(this, "Opening stalk page.. : " + link,
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Intent.ACTION_VIEW,
		// since you have to use a final variable here
				Uri.parse(link));
		startActivity(intent);
	}

	// launched by xml file image for sharing
	public void share(View view) {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, "My ideal date is " + value
				+ " tell me yours :) http://linkofapphere.com ");
		startActivity(Intent.createChooser(shareIntent, "Share your thoughts"));

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_results);

		initVariables();
	}

}
