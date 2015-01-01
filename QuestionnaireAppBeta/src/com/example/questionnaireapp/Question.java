package com.example.questionnaireapp;

public class Question {

	private int ID;					
	private String QUESTION;	
	private int ANSWER_COUNT;
	private String ANSWERS;
//	private String CHARACTER_COUNT;
	private String CHARACTERS;

	public Question()
	{	
		//_id
		ID=0;
		//What is your favorite colour?
		QUESTION="";		 
		//4
		ANSWER_COUNT=0;	
		//blue;red;green;yellow
		ANSWERS="";		
//		//2;3;1;1 the effected characters when answering a question with options 1 , 2 , 3 or 4
//		// option 1 has 2 affected, and option 2 has 3 ...
//		CHARACTER_COUNT="";
		//character1;character2|character3;character4;character5|character6|character7
		//each options will be separated by "|"
		//each affected character set is separated by ";"
		CHARACTERS="";		 
	}						 

	public Question(String question,
			int answer_count, 
			String answers,
			String characters) {

		QUESTION=question;		
		ANSWER_COUNT=answer_count;		
		ANSWERS=answers;				
//		CHARACTER_COUNT=character_count; 
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

//	public String getCHARACTER_COUNT(){
//		return CHARACTER_COUNT;
//	}

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

//	public void setCHARACTER_COUNT(String character_count){
//		CHARACTER_COUNT=character_count;
//	}

	public void setCHARACTERS(String characters){
		CHARACTERS=characters;
	}
	
	public String toString(){
		return QUESTION;
	}
}
