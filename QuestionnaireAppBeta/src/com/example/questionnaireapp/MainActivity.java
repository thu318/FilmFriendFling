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

	// max 16 types of answers
	private static final int max = 16;
	RadioGroup grp;
	RadioButton rd[] = new RadioButton[max];
	Button nextButton;
	int totalAnswers;

	// variables used for separating strings in database
	// to calculate score of each characters :
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
	// private boolean musicSelected;

	// sharedPreferences for saved progress
	private String progressFile = "progressPref";
	SharedPreferences progress;

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
		// Saving progress, i.e. store id of current question, score of
		// character
		if (id == R.id.save_progress) {
			Editor editor = progress.edit();
			editor.putInt("currQuestion", qid - 1);
			for (String s : charactersScore.keySet()) {
				editor.putInt(s, charactersScore.get(s));
				Log.d(s, String.valueOf(charactersScore.get(s)));
			}
			editor.commit();
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Initialize variables in the xml file and also the activities animations
	 * 
	 * @throws IOException
	 */
	private void initVariables() {
		background = findViewById(R.id.mainLayout);
		background.setBackgroundResource(R.drawable.bg1);

		db = new DBAdapter(this);

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
		// update score after pressing next button
		nextButton.setOnClickListener(this);

		// initialize sound/music options ???
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
			//
			grp.setOnCheckedChangeListener(grpListener);
		}
		//
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
	 * until reaching the last question. 6. Log the scores and transfer the
	 * character score list to the results activity class.
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
				// points in the bundle
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
	 * @return
	 */
	private static ArrayList<String> getMax(
			HashMap<String, Integer> charactersScore) {

		ArrayList<String> maxKeys = new ArrayList<String>();
		int maxValue = 0;

		for (Map.Entry<String, Integer> entry : charactersScore.entrySet()) {
			if (entry.getValue() > maxValue) {
				// New max remove all current keys since 
				// there could be more than one with the same score
				maxKeys.clear(); 
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
	private void loadProgress() {
		qid = progress.getInt("currQuestion", 0);
		for (String c : CHARACTERS)
			charactersScore.put(c, progress.getInt(c, 1));
	}

	//
	// http://stackoverflow.com/questions/456211/activity-restart-on-rotation-android
	//
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
