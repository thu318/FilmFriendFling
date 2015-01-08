package com.example.questionnaireapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.example.questionnaireapp.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
		android.view.View.OnClickListener {

	/**
	 * key values in the character scores hash table names used are of the
	 * future ideal characters for the user in the questionnaire
	 */
	private static String[] CHARACTERS = { "Plywood", "Jar_Jar_Binks", "God",
			"Blair_Witch", "Crazy_Eyes", "James_Bond", "Batman", "Leatherface",
			"Lassie", "Xena", "Generic_Damsel", "Bernadette" };

	/**
	 * database connection
	 */
	private DBAdapter db;
	/**
	 * all the questions in list form
	 */
	private List<Question> questionList;
	/**
	 * question id
	 */
	int qid = 0;

	// ----------------------------------
	// QUESTION VARIABLES
	// ---------------------------------
	/**
	 * Question object
	 */
	private Question currentQuestion;
	/**
	 * TextView to show the question text string
	 */
	private TextView textQuestion;
	/**
	 * Translate animation from right to left
	 */
	private Animation animation;
	/**
	 * Translate animation from right to left with .5 seconds delay
	 */
	private Animation animation2;
	// ---------------------------------
	// ANSWERS VARIABLES
	// ----------------------------------
	/**
	 * max 16 types of answers
	 */
	private static final int max = 16;
	/**
	 * the radio group for the answers
	 */
	RadioGroup grp;
	/**
	 * separating each radio buttons
	 */
	RadioButton rd[] = new RadioButton[max];
	/**
	 * the button to press to progress through the questionnaire
	 */
	Button nextButton;
	/**
	 * number of answers in the current question
	 */
	int totalAnswers;
	// -----------------------------------

	// -------------------------------------------------------------------------------
	// variables used for separating strings in database
	// to calculate and store the score of each characters
	// -------------------------------------------------------------------------------
	private String line;
	private String[] lineArray;
	private String line3;
	private String[] lineArray3;
	private static final String ANSWER_SEPARATOR = ";";
	private static final String CHAR_PTS_SEPARATOR = "#";
	/**
	 * the current location of the radio button selected in the question by the
	 * user
	 */
	private int index;
	/**
	 * score given for selecting the answer of the current question for the
	 * current character
	 */
	private int currentCharacterScore;
	/**
	 * list of characters which has the highest score
	 */
	ArrayList<String> maxCharacters;
	/**
	 * list of characters with their score
	 */
	static HashMap<String, Integer> charactersScore = new HashMap<String, Integer>();
	// --------------------------------------------------------------------------------

	/**
	 * click sound for nextButton and radio buttons
	 */
	private MediaPlayer clickSound;
	/**
	 * background, will be randomly changed
	 */
	private View background;
	/**
	 * scroll view for a lot of possible answers control
	 */
	private ScrollView scrollView;

	/**
	 * sharedPreferences for options and sound options selected by user
	 */
	SharedPreferences optionPreferences;
	/**
	 * see if the user enabled/disabled sound
	 */
	private boolean soundOptSelected;
	/**
	 * see if the user enabled/disabled music
	 */
	// private boolean musicSelected;

	/**
	 * sharedPreferences name for saved progress
	 */
	private String progressFile = "progressPref";
	/**
	 * to save important data of for the progress of the user
	 */
	SharedPreferences progress;

	/**
	 * Set all variables and sounds show the user the current question. Refer to
	 * each method for a more descriptive information for each functions.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setup the layout of the activity
		setContentView(R.layout.activity_main);
		// initialize variables
		initVariables();
		// set the current question
		setQuestionView();
		// set sounds
		setSounds();
	}

	/**
	 * Show or hide options menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Actions for option items in the options menu
	 * 
	 * 1. Save - Saves user progress in the questionnaire
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		// Saving user progress if the save progress option is pressed
		if (id == R.id.save_progress) {
			// opens editor for progress sharedPreferences
			Editor editor = progress.edit();
			// store id of current question
			editor.putInt("currQuestion", qid - 1);
			// store the score of each characters
			for (String s : charactersScore.keySet()) {
				editor.putInt(s, charactersScore.get(s));
				Log.d(s, String.valueOf(charactersScore.get(s)));
			}
			// commit this changes to the progress sharedPreferences
			editor.commit();
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Do nothing when the user changed the orientation of the device. Fixes the
	 * error of the questionnaire resetting when the device changes orientation
	 * after answering the first question.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Set and connect layout/variables in the xml file and also the activities
	 * animations. Also, check if the current activity is a resumed activity or
	 * not.
	 * 
	 * @throws IOException
	 */
	private void initVariables() {
		background = findViewById(R.id.mainLayout);
		background.setBackgroundResource(R.drawable.bg1);

		db = new DBAdapter(this);

		// get the bundled activity from the start screen if the resume button
		// is pressed or not " intent.putExtra("ResumePressed", true) "
		Bundle b = getIntent().getExtras();
		boolean resumePressed = b.getBoolean("ResumePressed");

		// progress preferences
		progress = getSharedPreferences(progressFile, Context.MODE_PRIVATE);
		Log.d("resPressed", String.valueOf(resumePressed));

		// if user chose resume from start screen, load saved progress, else
		// call setHashTable(set character scores to 1)
		if (resumePressed)
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
		// set scrollView variable as the scroll view found in the layout folder
		scrollView = (ScrollView) findViewById(R.id.scrollView1);
		// set the radiogroup variable "grp" as the radio button group found in
		// the layout folder
		grp = (RadioGroup) findViewById(R.id.radioGroup1);

		// set all radio button variables to the radio buttons found in the
		// layout folder
		// using get getResources to write the resId (resource id)
		for (int i = 0; i < max; i++) {
			String viewId = "radio" + i;
			int resId = getResources().getIdentifier(viewId, "id",
					getPackageName());
			// resId number found and used to set the current radio button
			rd[i] = (RadioButton) findViewById(resId);
		}

		// set the button nextButton to the next button found in the layout
		// folder
		nextButton = (Button) findViewById(R.id.button1);
		// update score after pressing next button
		nextButton.setOnClickListener(this);

		// initialize sound and music options
		optionPreferences = getSharedPreferences(OptionScreen.optionFile,
				Context.MODE_PRIVATE);
		soundOptSelected = optionPreferences.getBoolean("soundOptValue", false);
		// musicSelected = optionPreferences.getBoolean("musicOptValue", false);

	}

	/**
	 * Creates actionListeners for the radio buttons, so that it plays a sound
	 * when clicked. Noting that it only does this when the sound preference of
	 * the user is on.
	 * 
	 * @see ActionListener
	 * @see soundOptSelected - option variable for sounds
	 */
	private void setSounds() {
		// Sound on in options
		if (soundOptSelected) {
			OnCheckedChangeListener grpListener = new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					Random rand = new Random();
					int x = rand.nextInt(5);
					switch (x) {
					case 0:
						// play sound 1..
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
			grp.setOnCheckedChangeListener(grpListener);
		}
		grp.setOnClickListener(this);
	}

	/**
	 * Changes the texts in the current activity into the current question and
	 * its answers to the user current progress in the questionnaire.
	 */
	private void setQuestionView() {
		// show the current question
		textQuestion.setText(currentQuestion.getQUESTION());
		textQuestion.startAnimation(animation);

		// get the answers of the current question from the question object
		line = currentQuestion.getANSWERS();
		// split the answers in the current question (";")
		lineArray = line.trim().split(ANSWER_SEPARATOR);

		// get the number of answers in the current question
		totalAnswers = currentQuestion.getANSWER_COUNT();
		// show the number of answers in the logs
		Log.d("total answers", String.valueOf(totalAnswers));

		// make at least the first one true to avoid errors if the user doesn't
		// select an option
		rd[0].setChecked(true);
		// show all answers by changing the text for the radio buttons
		for (int i = 0; i < totalAnswers; i++) {
			// set the text of the answer to the i string in the array and so
			// on..
			// ie. when i is 0 set it to the first string the string array.
			rd[i].setText(lineArray[i]);
			// start the animation of the answer text
			rd[i].startAnimation(animation2);
		}

		for (int i = totalAnswers; i < max; i++) {
			// hide unused radio buttons, so that the user can't select these
			// answers
			rd[i].setVisibility(View.INVISIBLE);
		}

		// increase question id to move on to a new question next time this
		// method is called
		qid++;
	}

	/**
	 * Launches actions after the user clicks the next button after selecting an
	 * answer to a question in the questionnaire.
	 * 
	 * 1. Play sound if the sound is on in the user option preferences. 2. Reset
	 * the place where the radio group is being viewed to its first position. 3.
	 * Increase the scores of the characters in the answer selected. 4. Change
	 * the background randomly and show the next question. 5. Repeat steps 1-4
	 * until reaching the last question. 6. Log the scores and transfer the list
	 * of characters with the highest score to the results activity class.
	 */
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

				// it's the last question
			} else {

				Log.d("Character Scores", " " + charactersScore);
				// calculate who has the highest score
				maxCharacters = getMax(charactersScore);
				Log.d("Top Scorers", " " + maxCharacters);

				// Get results page
				Intent intent = new Intent(MainActivity.this,
						ResultActivity.class);
				// create a new bundle
				Bundle b = new Bundle();
				// put the character list arranged from the highest to lowest
				// ranking in the bundle
				b.putStringArrayList("maxCharacters", maxCharacters);
				// List of characters ready
				intent.putExtras(b);
				startActivity(intent);
				finish();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Changes the questionnaire background randomly.
	 */
	private void changeBgRandomly() {
		Random rand = new Random();
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
	 * Play a sound
	 * 
	 * Used when the next button and radio button are pressed in this program
	 * 
	 * @param context
	 * @param resid
	 *            - id of chosen sound to play
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

	/**
	 * Resets all the characters score hash table to 1.
	 */
	private static void setHashTable(HashMap<String, Integer> charScore) {
		for (int i = 0; i < CHARACTERS.length; i++) {
			charScore.put(CHARACTERS[i], 1);
		}
	}

	/**
	 * Returns a list of the characters with the highest score
	 * 
	 * @param charactersScore
	 *            - character name, character current score
	 * @return list of characters with the highest score
	 */
	private static ArrayList<String> getMax(
			HashMap<String, Integer> charactersScore) {

		// List of characters with the highest score
		ArrayList<String> maxKeys = new ArrayList<String>();
		// Highest score
		int maxValue = 0;

		for (Map.Entry<String, Integer> entry : charactersScore.entrySet()) {
			if (entry.getValue() > maxValue) {
				// New max found, remove all current keys since
				// there could be more than one with the same score
				maxKeys.clear();
				// Add this key (the character name) to the list of characters
				// with the highest score
				maxKeys.add(entry.getKey());
				// Set the new highest score
				maxValue = entry.getValue();
			} else if (entry.getValue() == maxValue) {
				// Add another to the list
				maxKeys.add(entry.getKey());
			}
		}
		// Return the list of characters with the highest score
		return maxKeys;
	}

	/**
	 * Loads user progress from progress sharedPreferences
	 */
	private void loadProgress() {
		// get the number of the current question or return 0 as default
		qid = progress.getInt("currQuestion", 0);
		// replace the characters score with the score from the progress or set
		// it to 1
		for (String c : CHARACTERS)
			charactersScore.put(c, progress.getInt(c, 1));
	}

}
