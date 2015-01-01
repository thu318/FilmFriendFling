package com.example.questionnaireapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	// Questionnaire table columns names
	// note : (no space " " in between names because it will be used in a SQLite
	// query)
	static final String KEY_ID = "id";
	static final String QUESTION = "Question";
	static final String ANSWER_COUNT = "Answer_Count";
	static final String ANSWERS = "Answers";
	//	static final String CHARACTER_COUNT = "Character_Count";
	static final String CHARACTERS = "Characters";



	// Questionnaire database details
	static final String DATABASE_NAME = "Questionnaire_Database";
	static final String DATABASE_TABLE = "Questionnaire_Table";
	static final int DATABASE_VERSION = 1;
	static final String TAG = "DBAdapter";
	// for function execSQL
	// execSQL is used when the database command does not return anything
	static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ DATABASE_TABLE + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + QUESTION
			+ " TEXT UNIQUE, " + ANSWER_COUNT + " INTEGER, " + ANSWERS
			+ " TEXT, " + CHARACTERS + " TEXT" + ")";

	static final String[] COLUMNS = {QUESTION,ANSWER_COUNT,ANSWERS,CHARACTERS};


	//String used in text files to separate lines, questions, answers, between characters' name and corresponding points
	static final String QUESTION_SEPARATOR = "~";
	static final String LINE_SEPARATOR = "=";


	static Context context;

	DatabaseHelper DBHelper;
	SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DATABASE_CREATE);
//				addTextQuestions(db);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// ---inserting already created questions from text file---
		public void addTextQuestions(SQLiteDatabase db){
			
			AssetManager am = context.getAssets();
			InputStream is;
			try {
				is = am.open("questionsFile.txt");


				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line;
				StringBuffer content = new StringBuffer();
				while ((line = br.readLine()) != null) 
					content.append(line);

				String[] questionsArray = content.toString().split(QUESTION_SEPARATOR);

				for (String temp : questionsArray) {
					String qElements[] = temp.split(LINE_SEPARATOR);
					ContentValues values = new ContentValues();
					for (int i = 0; i < qElements.length; i++)
						values.put(COLUMNS[i], qElements[i]);
					// Inserting Row
					db.insert(DATABASE_TABLE, null, values);
					Log.d("insertion", "inserted " + qElements[0]);

				}
				br.close();
				is.close();	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	// ---opens the database---
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}


	// ---inserting a new question in another java file---
	public void insertQuestion(InputStream is) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer content = new StringBuffer();
		while ((line = br.readLine()) != null) 
			content.append(line);

		String[] questionsArray = content.toString().split(QUESTION_SEPARATOR);

		for (String temp : questionsArray) {
			Log.d("questArr", temp);
			String qElements[] = temp.split(LINE_SEPARATOR);
			ContentValues values = new ContentValues();
			for (int i = 0; i < qElements.length; i++)
				values.put(COLUMNS[i], qElements[i]);
			// Inserting Row
			db.insert(DATABASE_TABLE, null, values);
			Log.d("insertion", "inserted " + COLUMNS[0]);
		}
		br.close();
		is.close();		
	}


	// ---deleting a particular question---
	public boolean deleteQuestion(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
	}

	// ---deleting all questions---
	public void deleteQuestions() {
		db.delete(DATABASE_TABLE, null, null);
	}

	// ---deleting the table---
	public void deleteTable() {
		// String sql="DELETE FROM "+DATABASE_TABLE;
		// db.execSQL(sql);
		context.deleteDatabase(DATABASE_NAME);
	}

	// ---retrieves all the questions---
	public Cursor getAllQuestions() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ID, QUESTION,
				ANSWERS }, null, null, null, null, null);
	}

	// ---retrieves all the questions in a list---
	public List<Question> getAllQuestionsList() {
		List<Question> questionList = new ArrayList<Question>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;
		db = DBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Question question = new Question();
				question.setID(cursor.getInt(0));
				question.setQUESTION(cursor.getString(1));
				question.setANSWER_COUNT(cursor.getInt(2));
				question.setANSWERS(cursor.getString(3));
				question.setCHARACTERS(cursor.getString(4));
				questionList.add(question);
			} while (cursor.moveToNext());
		}
		// return quest list
		return questionList;
	}

	// ---retrieves a particular question---
	public Cursor getQuestion(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ID,
				QUESTION, ANSWERS }, KEY_ID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---updates a question---
	public boolean updateQuestion(long rowId, String question, String answers) {
		ContentValues args = new ContentValues();
		args.put(KEY_ID, rowId);
		args.put(QUESTION, question);
		args.put(ANSWERS, answers);
		db.insert(DATABASE_TABLE, null, args);
		return db.update(DATABASE_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
	}

	// ---retrieves total rows---
	public int getRows() throws SQLException {
		String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;
		db = DBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor.getCount();
	}

}
