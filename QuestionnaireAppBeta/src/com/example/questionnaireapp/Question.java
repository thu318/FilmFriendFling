package com.example.questionnaireapp;

/**
 * Simple class to easily create questions for the questionnaire. 
 * Each variables have their own column from the database created,
 * where the data is read from a text file. 
 */
public class Question {

	/**
	 * primary key auto incremented 
	 */
	private int ID;
	/**
	 * One of the questions of the questionnaire. Example :  "What is your favorite color?
	 */
	private String QUESTION;
	/**
	 * The number of answers the question has. Example :  4
	 */
	private int ANSWER_COUNT;
	/**
	 * The possible answers separated with a semicolon. Example : blue;red;green;yellow 
	 */
	private String ANSWERS;
	/**
	 * - Each answers to the question will have some characters score affected by it and will be separated by "|".
	 * - Affected characters from selecting this option set is separated by ";" 
	 * Example : James_Bond#2|Leatherface#4|God#4|Jar_Jar_Binks#4|Batman#2|Bernadette#4|Generic_Damsel#4|Plywood#3|Blair_Witch#4|Lassie#4|Crazy_Eyes#4|Xena#4
	 */
	private String CHARACTERS;
	
	/**
	 * Default constructor to initialize a question object. 
	 */
	public Question()
	{	
		ID=0;
		QUESTION="";		 
		ANSWER_COUNT=0;	
		ANSWERS="";		
		CHARACTERS="";		 
	}						 	
	/**
	 * Set the details of the question object.
	 *  
	 * @param question - the question.
	 * @param answer_count - the number of answers.
	 * @param answers - the answers.
	 * @param characters - the characters affected by each answers.
	 * @see QUESTION, ANSWER_COUNT,ANSWERS and CHARACTERS
	 */
	public Question(String question,
			int answer_count, 
			String answers,
			String characters) {

		QUESTION=question;		
		ANSWER_COUNT=answer_count;		
		ANSWERS=answers;				
		CHARACTERS=characters;		
	}
	//get functions used in the database

	/**
	 * 
	 * @return ID of the question
	 */
	public int getID(){
		return ID;
	}
	
	/**
	 * 
	 * @return the question.
	 */
	public String getQUESTION(){
		return QUESTION;
	}
	
	/**
	 * 
	 * @return the number of answers.
	 */
	public int getANSWER_COUNT(){
		return ANSWER_COUNT;
	}

	/**
	 * 
	 * @return  the answers.
	 */
	public String getANSWERS(){
		return ANSWERS;
	}

	/**
	 * 
	 * @return the characters affected by each answers.
	 */
	public String getCHARACTERS(){
		return CHARACTERS;
	}

	//set functions used in the database

	/**
	 * 
	 * @param ID of the question
	 */
	public void setID(int id){
		ID=id;
	}

	/**
	 * 
	 * @param the question
	 */
	public void setQUESTION(String question){
		QUESTION=question;
	}

	/**
	 * 
	 * @param the number of answers
	 */
	public void setANSWER_COUNT(int answer_count){
		ANSWER_COUNT=answer_count;
	}

	/**
	 * 
	 * @param the answers to the question
	 */
	public void setANSWERS(String answers){
		ANSWERS=answers;
	}

	/**
	 * 
	 * @param the characters affected by each answers.
	 */
	public void setCHARACTERS(String characters){
		CHARACTERS=characters;
	}
}
