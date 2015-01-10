/*
 * By: Lee, Wei-Meng. Android Application Development Cookbook : 93
 * 	   Creating and using SQLite databases programmatically
 * Updated by: Ireneo Mercado
 * 			   Thu Huong Dao 
 */

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

/**
 * Encapsulate all the complexities of accessing data so that is is transparent
 * to the calling code. The calling code is mostly MainActivity.java, for
 * getting all the questions in list form and the number of questions in the
 * table.
 * 
 * @see Lee, Wei-Meng. Android Application Development Cookbook : 93 Creating
 *      and using SQLite databases programmatically.
 */
public class DBAdapter {
	// -------------------------------------------------------------------------
	// QUESTIONNAIRE TABLE
	// note : (no space " " in between names because it will be used in a SQLite
	// query)
	// ------------------------------------------------------------------------
	/**
	 * Primary key id
	 */
	static final String KEY_ID = "id";
	/**
	 * String column. The questions to ask the user.
	 */
	static final String QUESTION = "Question";
	/**
	 * String column. The number of answers for the question
	 */
	static final String ANSWER_COUNT = "Answer_Count";
	/**
	 * String column. The answers for the question, separated by semi-colons
	 * ";".
	 */
	static final String ANSWERS = "Answers";
	/**
	 * String column. The characters affected by an answer, separated by "|".
	 * Each option then is separated by a semi-colon ";" if there is more than
	 * one character affected by a question. The score is separated by "#" for
	 * each answer.
	 */
	static final String CHARACTERS = "Characters";

	// ----------------------------------------------------------------------
	//
	// QUESTIONNAIRE DATABASE
	//
	// ----------------------------------------------------------------------
	static final String DATABASE_NAME = "Questionnaire_Database";
	static final String DATABASE_TABLE = "Questionnaire_Table";
	static final int DATABASE_VERSION = 1;

	/**
	 * Creating the questionnaire table in the Questionnaire_Database database
	 * execSQL command. note: execSQL is used when the database command does not
	 * return anything
	 */
	static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ DATABASE_TABLE + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + QUESTION
			+ " TEXT UNIQUE, " + ANSWER_COUNT + " INTEGER, " + ANSWERS
			+ " TEXT, " + CHARACTERS + " TEXT" + ")";

	/**
	 * All columns in the questionnaire table. Used in inserting data to the
	 * table for loop when adding questions read from a text file.
	 */
	static final String[] COLUMNS = { QUESTION, ANSWER_COUNT, ANSWERS,
			CHARACTERS };

	// String used in text files to separate lines, questions, answers, between
	// characters' name and corresponding points
	/**
	 * Separate questions in the text file.
	 */
	static final String QUESTION_SEPARATOR = "~";
	/**
	 * Separate strings in the text file so that the right data goes into the
	 * right questionnaire table columns.
	 */
	static final String LINE_SEPARATOR = "=";

	/**
	 * Used context for accessing the assets folder to read the questions,
	 * answers etc. (questionsFile.txt)
	 * 
	 * Explanation of the context class Context.class :
	 * 
	 * Interface to global information about an application environment. This is
	 * an abstract class whose implementation is provided by the Android system.
	 * It allows access to application-specific resources and classes, as well
	 * as up-calls for application-level operations such as launching
	 * activities, broadcasting and receiving intents, etc.
	 */
	static Context context;

	/**
	 * Encapsulate all the complexities of accessing data so that is is
	 * transparent to the calling code. The calling code is mostly
	 * MainActivity.java, for getting all the questions in list form and the
	 * number of questions in the table.
	 * 
	 * @see Lee, Wei-Meng. Android Application Development Cookbook : 93 Recipes
	 *      for Building Winning Apps Creating and using SQLite databases
	 *      programmatically.
	 */
	DatabaseHelper DBHelper;
	/**
	 * Android built-in database system.
	 */
	SQLiteDatabase db;

	/**
	 * Adds a private class that extends the SQLiteOpenHelper class, which is a
	 * helper class in Android for database creation and version management.
	 * Overrides the onCreate() and onUpgrade() methods.
	 * 
	 * @param ctx
	 *            - context
	 * @see - Context.class
	 */
	public DBAdapter(Context ctx) {
		DBAdapter.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		/**
		 * If the database isn't created yet, these method is launched.
		 * Create the database and add its database columns values.
		 */
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DATABASE_CREATE);
				addTextQuestions(db);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		/**
		 * To insert created questions from questionsFile.txt file inside the
		 * assets folder to the questionnaire database table.
		 * 
		 * @param db
		 *            - the questionnaire database
		 */
		public void addTextQuestions(SQLiteDatabase db) {

			AssetManager am = context.getAssets();
			InputStream is;
			try {
				is = am.open("questionsFile.txt");

				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line;
				StringBuffer content = new StringBuffer();
				while ((line = br.readLine()) != null)
					content.append(line);

				String[] questionsArray = content.toString().split(
						QUESTION_SEPARATOR);

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
		/**
		 * Recreate the database
		 */
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	/**
	 * Opens the database
	 * 
	 * @return opened database
	 * @throws SQLException
	 */
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the database
	 */
	public void close() {
		DBHelper.close();
	}

	/**
	 * Retrieves all the questions in a list---
	 * 
	 * @return an arraylist of all data in the questionnaire table columns
	 *         (questions, answers etc.) in list form, to be read and separated
	 *         in the main activity.
	 */
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
		return questionList;
	}

	/**
	 * Retrieves total rows rows in the database table, so that the main
	 * activity knows when all the questions have been asked to the user.
	 * 
	 * @return number of rows, which is the number of total questions in the
	 *         questionnaire.
	 * @throws SQLException
	 */
	public int getRows() throws SQLException {
		String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;
		db = DBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor.getCount();
	}

}
