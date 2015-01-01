package com.example.questionnaireapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.example.questionnaireapp.R;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		android.view.View.OnClickListener {

	private static String[] CHARACTERS = { "Plywood", "Jar_Jar_Binks", "God",
			"Blair_Witch", "Crazy_Eyes", "James_Bond", "Batman", "Leatherface",
			"Lassie", "Xena", "Generic_Damsel", "Bernadette" };

	// database connect
	private DBAdapter db;
	// to get all the questions in list form
	private List<Question> questionList;

	// starting question
	int qid = 0;

	// question variables:
	private Question currentQuestion;
	private TextView textQuestion;
	private Animation animation;
	private Animation animation2;
	private Animation animation3;

	// max 16 types of answers
	private static final int max = 16;
	RadioGroup grp;
	RadioButton rd[] = new RadioButton[max];
	Button nextButton;
	int totalAnswers;

	// variables used for separating strings in database
	// to calculate score of each characters 
	private String line;
	private String[] lineArray;
	private String line3;
	private String[] lineArray3;
	private static final String ANSWER_SEPARATOR = ";";
	private static final String CHAR_PTS_SEPARATOR = "#";
	private int index;
	private int currentCharacterScore;
	ArrayList<String> maxCharacters;
	static HashMap<String, Integer> charactersScore = new HashMap<String, Integer>();

	// click sound for nextButton and radio buttons
	private MediaPlayer clickSound;
	// background, will be randomly changed
	private View background;
	// scroll view for a lot of possible answers control
	private ScrollView scrollView;

	// sharedPreferences for options and sound options selected by user
	SharedPreferences optionPreferences;
	private boolean soundOptSelected;
	private boolean musicSelected;

	// sharedPreferences for saved progress
	private String progressFile = "progressPref";
	SharedPreferences progress;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// fullScreen();

		initVariables();

		// setup questions
		setQuestionView();
		// setup sounds
		setSounds();

	}// end of onCreate method

	/**
	 * Initialize variables in the xml file including animations
	 * @throws IOException 
	 */
	private void initVariables(){
		background = findViewById(R.id.mainLayout);
		background.setBackgroundResource(R.drawable.bg1);

		db = new DBAdapter(this);
		db.open();
		AssetManager am =this.getAssets();
		InputStream is;

			try {
				is = am.open("questionsFile.txt");
				db.insertQuestion(is);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		
		Bundle b = getIntent().getExtras();
		boolean resumePressed = b.getBoolean("ResumePressed");
		
		// progress preferences
		progress = getSharedPreferences(progressFile, Context.MODE_PRIVATE);
		Log.d("resPressed", String.valueOf(resumePressed));
		
		//if user chose resume from start screen, load saved progress, else call setHashTable(set character scores to 1)
		if(resumePressed)
			loadProgress();
		else
			setHashTable(charactersScore);

		// all the questions in list form
		questionList = db.getAllQuestionsList();
		Log.d("number of question", String.valueOf(questionList.size()));
		// qid = 0 starting question
		currentQuestion = questionList.get(qid);
		// set textview variable as the text found in the layout folder xml file
		textQuestion = (TextView) findViewById(R.id.textView1);
		// set animations variables found in the anim folder xml files
		animation = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.translate);
		animation2 = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.translate2);
		animation3 = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fade);
		// finds scroll view
		scrollView = (ScrollView) findViewById(R.id.scrollView1);

		grp = (RadioGroup) findViewById(R.id.radioGroup1);

		// Initialize all radio button answers layout
		// using get getResources
		for (int i = 0; i < max; i++) {
			String viewId = "radio" + i;
			int resId = getResources().getIdentifier(viewId, "id",
					getPackageName());
			rd[i] = (RadioButton) findViewById(resId);
		}

		//
		nextButton = (Button) findViewById(R.id.button1);
		//
		nextButton.startAnimation(animation3);
		// update score after pressing next button
		nextButton.setOnClickListener(this);

		// initialize sound/music options ???
		optionPreferences = getSharedPreferences(OptionScreen.optionFile,
				Context.MODE_PRIVATE);
		soundOptSelected = optionPreferences.getBoolean("soundOptValue", false);
		musicSelected = optionPreferences.getBoolean("musicOptValue", false);

	}

	/**
	 * Hides the menu and action bar
	 */
//	private void fullScreen() {
//		if (Build.VERSION.SDK_INT < 16) {
//			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//					WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		} else {
//			View decorView = getWindow().getDecorView();
//			// Hide the status bar.
//			int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//			decorView.setSystemUiVisibility(uiOptions);
//			// Remember that you should never show the action bar if the
//			// status bar is hidden, so hide that too if necessary.
//			ActionBar actionBar = getActionBar();
//			actionBar.hide();
//		}
//	}

	//
	//
	//
	private void setSounds() {

		/*
		 * play click sound when each radiobutton is clicked when user selected
		 * soundOption
		 */
		if (soundOptSelected) {
			OnCheckedChangeListener grpListener = new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					Random rand = new Random();
					int x = rand.nextInt(5);
					switch (x) {
					case 0:
						playClickSound(MainActivity.this, R.raw.rd_click);
						break;
					case 1:
						playClickSound(MainActivity.this, R.raw.rd_click1);
						break;
					case 2:
						playClickSound(MainActivity.this, R.raw.rd_click2);
						break;
					case 3:
						playClickSound(MainActivity.this, R.raw.rd_click3);
						break;
					case 4:
						playClickSound(MainActivity.this, R.raw.rd_click4);
						break;
					default:
						break;
					}
				}
			};
			//
			grp.setOnCheckedChangeListener(grpListener);
		}
		//
		grp.setOnClickListener(this);
	}

	//
	//
	//
	private void setQuestionView() {
		textQuestion.setText(currentQuestion.getQUESTION());
		textQuestion.startAnimation(animation);
		// Toast.makeText(this, "Answer Count : " +
		// currentQuestion.getANSWER_COUNT() , Toast.LENGTH_LONG).show();
		line = currentQuestion.getANSWERS();
		// Toast.makeText(this, "Answers : " + line , Toast.LENGTH_LONG).show();
		lineArray = line.trim().split(ANSWER_SEPARATOR);

		totalAnswers = currentQuestion.getANSWER_COUNT();
		// Toast.makeText(this, totalAnswers, Toast.LENGTH_LONG).show();
		Log.d("total answers", String.valueOf(totalAnswers));

		// make at least the first one true to avoid errors if the user doesn't
		//select an option
		rd[0].setChecked(true);
		for (int i = 0; i < totalAnswers; i++) {
			rd[i].setText(lineArray[i]);
			rd[i].startAnimation(animation2);
		}
		// hide unused radio buttons
		for (int i = totalAnswers; i < max; i++)
			rd[i].setVisibility(View.INVISIBLE);

		// increase question id to move on to a new question next time
		qid++;
	}

	//
	//
	//
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.button1:
			// play the clickSound if user selected soundOption
			if (soundOptSelected)
				playClickSound(MainActivity.this, R.raw.click);
			// reset scroll view
			scrollView.scrollTo(0, 0);

			// selected radio button
			index = grp
					.indexOfChild(findViewById(grp.getCheckedRadioButtonId()));

			line = currentQuestion.getCHARACTERS();
			lineArray = line.trim().split("\\|");

			line = lineArray[index];
			lineArray = line.trim().split(ANSWER_SEPARATOR);

			// line2 = currentQuestion.getCHARACTER_COUNT();
			// lineArray2 = line2.trim().split(";");

			// add the scores to the characters affected by the choice
			for (int i = 0; i < lineArray.length; i++) {
				line3 = lineArray[i];
				lineArray3 = line3.trim().split(CHAR_PTS_SEPARATOR);

				currentCharacterScore = charactersScore.get(lineArray3[0]);
				charactersScore.put(lineArray3[0], currentCharacterScore
						+ Integer.parseInt(lineArray3[1]));
			}

			// check if it's not the last question
			if (qid < db.getRows()) {
				// set the next background randomly
				changeBgRandomly();
				// update the current question
				currentQuestion = questionList.get(qid);
				// set the next questions or get results
				setQuestionView();
				nextButton.startAnimation(animation3);

				// it's the last question
			} else {

				Log.d("Character Scores", " " + charactersScore);
				maxCharacters = getMax(charactersScore);
				Log.d("Top Scorers", " " + maxCharacters);

				// Get results page
				Intent intent = new Intent(MainActivity.this,
						ResultActivity.class);
				Bundle b = new Bundle();
				// Character that has the highest points
				b.putStringArrayList("maxCharacters", maxCharacters);
				// List of characters ready
				intent.putExtras(b);
				startActivity(intent);
				finish();
			}

			/*
			 * saving
			 */

			break;
		default:
			break;
		}
	}

	/**
	 * method for changing background randomly
	 */
	private void changeBgRandomly() {
		Random rand = new Random();
		// int x = rand.nextInt(5);
		int x = rand.nextInt(3);
		switch (x) {
		case 0:
			background.setBackgroundResource(R.drawable.bg1);
			break;
		case 1:
			background.setBackgroundResource(R.drawable.light1);
			break;
		case 2:
			background.setBackgroundResource(R.drawable.light2);
			break;
		default:
			break;
		}
	}

	/**
	 * method for changing background by its id
	 * @param id - id of choosen background in drawable
	 */
	private void changeBgById(int id) {
		background.setBackgroundResource(id);
	}

	/**
	 * method for playing a sound each time a button/radio button clicked
	 * @param context
	 * @param resid - id of choosen sound to play
	 */
	private void playClickSound(Context context, int resid) {
		clickSound = MediaPlayer.create(context, resid);
		clickSound.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				mp.reset();
				mp.release();
				mp = null;
			}
		});
		clickSound.start();
	}

	//
	// Function to make sure each character resets its score if the
	// program restarts
	//
	private static void setHashTable(HashMap<String, Integer> charScore) {
		for (int i = 0; i < CHARACTERS.length; i++) {
			charScore.put(CHARACTERS[i], 1);
		}
	}

	// get a list of the characters with the highest score
	private static ArrayList<String> getMax(
			HashMap<String, Integer> charactersScore2) {
		ArrayList<String> maxKeys = new ArrayList<String>();
		int maxValue = 0;
		for (Map.Entry<String, Integer> entry : charactersScore2.entrySet()) {
			if (entry.getValue() > maxValue) {
				maxKeys.clear(); /* New max remove all current keys */
				maxKeys.add(entry.getKey());
				maxValue = entry.getValue();
			} else if (entry.getValue() == maxValue) {
				maxKeys.add(entry.getKey());
			}
		}
		return maxKeys;
	}
	
	/**
	 * load progress from progress sharedPreferences
	 */
	private void loadProgress(){
		qid = progress.getInt("currQuestion",0);
		for(String c: CHARACTERS)
			charactersScore.put(c, progress.getInt(c, 1));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		// show menu
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//Saving progress, i.e. store id of current question, score of character
		if (id == R.id.save_progress) {
			Editor editor = progress.edit();
			editor.putInt("currQuestion", qid-1);
			for (String s : charactersScore.keySet()) {
				editor.putInt(s, charactersScore.get(s));
				Log.d(s,String.valueOf(charactersScore.get(s)));
			}
			editor.commit();
		}

		return super.onOptionsItemSelected(item);
	}

	//
	// http://stackoverflow.com/questions/456211/activity-restart-on-rotation-android
	//
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// setContentView(R.layout.myLayout);
		Toast.makeText(this, "Orientation changed", Toast.LENGTH_LONG).show();
	}

	// -----------------------------------------------------------------------------
	// EXTRA DATABASE FUNCTIONS
	// source : Lee, Wei-Meng, Android Application Cookbook 2013
	// Chapter 10 Persisting Data
	// -----------------------------------------------------------------------------

	//
	// DBAdapter.java addQuestions()
	// Adds strings/number to an SQLite database created
	//
	// public void addQuestions() {
	// // ---add a question---
	// db.open();
	//
	// AssetManager am = this.getAssets();
	// InputStream is;
	// try {
	// is = am.open("questionsFile.txt");
	// db.insertQuestion(is);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// db.close();
	// }

	// public void getQuestions() {
	// // --get all questions---
	// db.open();
	// Cursor c = db.getAllQuestions();
	// if (c.moveToFirst()) {
	// do {
	// displayQuestion(c);
	// } while (c.moveToNext());
	// }
	// db.close();
	// }
	//
	// public void getQuestion(int rowId) {
	// // ---get a question---
	// db.open();
	// Cursor c = db.getQuestion(rowId);
	// if (c.moveToFirst())
	// displayQuestion(c);
	// else
	// Toast.makeText(this, "No question found", Toast.LENGTH_LONG).show();
	// db.close();
	// }
	//
	// public void updateQuestion(int rowId) {
	// // ---update a question---
	// db.open();
	// if (db.updateQuestion(rowId, "new question", "new answer"))
	// Toast.makeText(this, "Update successful.", Toast.LENGTH_LONG)
	// .show();
	// else
	// Toast.makeText(this, "Update failed.", Toast.LENGTH_LONG).show();
	// db.close();
	// }
	//
	// public void deleteQuestion(int rowId) {
	// db.open();
	// if (db.deleteQuestion(rowId))
	// Toast.makeText(this, "Delete successful.", Toast.LENGTH_LONG)
	// .show();
	// else
	// Toast.makeText(this, "Delete failed.", Toast.LENGTH_LONG).show();
	// db.close();
	// }
	//
	// public void deleteQuestions() {
	// db.open();
	// db.deleteQuestions();
	// db.close();
	// }
	//
	// public void displayQuestion(Cursor c) {
	// Toast.makeText(
	// this,
	// "id: " + c.getString(0) + "\n" + "Question: " + c.getString(1)
	// + "\n" + "Answer:  " + c.getString(2),
	// Toast.LENGTH_LONG).show();
	// }

}
