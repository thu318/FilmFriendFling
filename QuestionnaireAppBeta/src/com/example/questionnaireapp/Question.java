package com.example.questionnaireapp;

/**
 * Simple class to easily create questions for the questionnaire. 
 * Each variables have their own column from the database created,
 * where the data is read from a text file. 
 */
public class Question {

	private int ID;					
	private String QUESTION;	
	private int ANSWER_COUNT;
	private String ANSWERS;
	private String CHARACTERS;

	public Question()
	{	
		/**
		 * primary key auto incremented 
		 */
		ID=0;
		/**
		 * string example :  "What is your favorite color?
		 */
		QUESTION="";		 
		/**
		 * int example :  4
		 */
		ANSWER_COUNT=0;	
		/**
		 * string example : blue;red;green;yellow 
		 */
		ANSWERS="";		
		/**
		 * string example : character1;character2|character3;character4;character5|character6|character7
		 * - Each answers to the question will have some characters score affected by it and will be separated by "|".
		 * - Affected characters from selecting this option set is separated by ";" 
		 */
		CHARACTERS="";		 
	}						 

	public Question(String question,
			int answer_count, 
			String answers,
			String characters) {

		QUESTION=question;		
		ANSWER_COUNT=answer_count;		
		ANSWERS=answers;				
		CHARACTERS=characters;		
	}

	//get functions

	public int getID(){
		return ID;
	}

	public String getQUESTION(){
		return QUESTION;
	}

	public int getANSWER_COUNT(){
		return ANSWER_COUNT;
	}

	public String getANSWERS(){
		return ANSWERS;
	}

	public String getCHARACTERS(){
		return CHARACTERS;
	}

	//set functions

	public void setID(int id){
		ID=id;
	}

	public void setQUESTION(String question){
		QUESTION=question;
	}

	public void setANSWER_COUNT(int answer_count){
		ANSWER_COUNT=answer_count;
	}

	public void setANSWERS(String answers){
		ANSWERS=answers;
	}

	public void setCHARACTERS(String characters){
		CHARACTERS=characters;
	}
	
	public String toString(){
		return QUESTION;
	}
}
