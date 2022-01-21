

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Concordance class
 *
 * A properly constructed object of this class represents a concordance
 * for a file that has the structure of kjv12.txt.
 *
 * @author Liam Keliher
 * 
 * Edited by: Erika Hill, Max Sotsky
 */

public class Concordance
{
	private BufferedReader input;
	private int tableSize;
	private HashEntry[] table;
	private ArrayList<String> allBookNames;
	private int wordCounter = 0;
	private int distinctWordCounter = 0;
	private int verseCounter = 0;
	private int chapterCounter = 0;
	private PrintStream out;
	//-------------------------------------------------------------------------
	public Concordance(File inFile, int inTableSize) throws FileNotFoundException, ConcordanceException
	{
		this(inFile, inTableSize, System.out);
	} // Concordance(File,int)
	//-------------------------------------------------------------------------
	public Concordance(File inFile, int inTableSize, PrintStream outPS) throws FileNotFoundException, ConcordanceException
	{
		input = new BufferedReader(new FileReader(inFile));
		tableSize = inTableSize;
		table = new HashEntry[tableSize];
		allBookNames = new ArrayList<String>();
		out = outPS;

		out.println("Building concordance...");
		buildConcordance();

		out.println();
		out.println("Total number of words = " + wordCounter);
		out.println("Number of distinct words = " + distinctWordCounter);
		out.println("Number of verses = " + verseCounter);
		out.println("Number of chapters = " + chapterCounter);
		out.println();

	} // Concordance(File,int,PrintStream)
	//-------------------------------------------------------------------------
	/**
	 *  Hashes the input string to produce an integer in the range 0...(N-1),
	 *  where N is the table size.
	 */
	private int hash(String input)
	{
		// Value to hold the position, and how far quadradic probing should jump, for the given string.
		BigInteger jump = new BigInteger("0");
		BigInteger toAdd;
		BigInteger tableSizeBI = new BigInteger("" + tableSize);
		
		// Goes through each character, adding up ASCII values times 10 to the power of the character's index.
		for(int index = 0; index < input.length(); index++) {
			toAdd = new BigInteger("" + ((long) Math.pow(10, index) * (int) input.charAt(index)));
			jump = jump.add(toAdd);
		}
		
		// Squares jump, mod tableSize.
		jump = jump.multiply(jump);
		jump = jump.mod(tableSizeBI);
		
		// Converts jump from a BigInteger to an int and returns it.
		return jump.intValue();

	} // hash(String)
	//-------------------------------------------------------------------------
	/**
	 *  Searches the hash table for the input word.
	 *  If the input word is not present *and* the table is full, return -1.
	 *  
	 *  If the input word *is* present, return the index of the table entry containing the word.
	 *  
	 *  If the input word is not present, and the table is *not* empty, return the index of
	 *  the empty table location where an entry containing that word would be inserted if
	 *  we were performing insertion.
	 *  
	 *  NOTE:  this is the method where you perform quadratic probing.
	 *  
	 *  @param  key  the word being searched for
	 *  @return a table index or -1 (see above description)
	 */
	public int search(String key)
	{
		// Makes the key lowercase.
		String lowerKey = customToLowerCase(key);
		// Figures out how far each jump should be.
		int jump = hash(lowerKey);
		
		// If the first index is empty, or the key it is searching for, returns first index.
		if (table[jump] == null || table[jump].getKey().equals(lowerKey)) {
			return jump;
		}
		
		// Checks the second index onward. 
		// If it finds the key or an empty place without returning back to the first index, returns found index.
		for(int index = (2 * jump) % tableSize; index != jump; index = (index + jump) % tableSize) {
			if (table[index] == null || table[index].getKey().equals(lowerKey)) {
				return index;
			}
		}
		
		// If the loop went back around to the first index, returns -1. No empty index or matching key found.
		return -1;

	} // search(String)
	//-------------------------------------------------------------------------
	/**
	 * This method takes in a table index.  If that table location is empty (null),
	 * return null.  If not, create a String array containing a String for each ID in
	 * the linked list of the table entry.  Each String should contain the information
	 * in the ID in human-friendly form (see handout). 
	 *
	 * @param  index  hash table index
	 * @return  array of IDs stored in table location, converted to human-friendly form
	 */
	public String[] formatIDs(int index)
	{
		// If the index exists and isn't empty...
        if (index != -1 && table[index] != null) {
        	
        	// Variables to hold data.
        	LinkedList<ID> IDList = table[index].getList();
            String[] IDStrings = new String[IDList.size()];
            ID currentID;
            
            // Goes down the list, putting each ID's data into a String, and that String into the String array
            for(int i = 0; i < IDList.size(); i++) {
            	currentID = IDList.get(i);
            	IDStrings[i] = allBookNames.get(currentID.getBook() - 1) + " " + currentID.getChapter() + ":" + currentID.getVerse();
            }
            
            return IDStrings;
        }
        
        // If there are no results, returns null
		return null;

	} // formatIDs(int)
	//-------------------------------------------------------------------------
	/**
	 *  Reads in each line of the input file and processes it appropriately.
	 *
	 *  DO NOT MODIFY THIS METHOD !!!
	 */
	private void buildConcordance() throws ConcordanceException
	{
		String currLine = "", restOfLine;
		int lineCounter = 0;
		short bookNum = 0, chapterNum = 0, verseNum = 0;
		String bookNumStr, chapterNumStr, verseNumStr;
		String bookName;

		try
		{
			currLine = input.readLine();
			while (currLine != null)
			{
				lineCounter++;

				//---------------------------------------
				//----- Line introducing a new book -----
				//---------------------------------------
				if ((currLine.length() > 7) && currLine.substring(0, 4).equals("Book"))
				{
					bookNumStr = currLine.substring(5, 7);
					bookName = currLine.substring(7).trim();
					bookNum++;
					try
					{
						if (bookNum != Integer.parseInt(bookNumStr) || bookName.equals(""))
						{
							out.println("Inside buildConcordance() method in class Concordance (#1).");
							out.println("Format problem on line " + lineCounter);
							out.println("Terminating...");
							throw new ConcordanceException("Inside buildConcordance -- format problem on line " + lineCounter);
						} // if
					} // try
					catch(NumberFormatException nfe)
					{
						out.println("Inside buildConcordance() method in class Concordance (#2).");
						out.println("Format problem on line " + lineCounter);
						out.println("Terminating...");
						throw new ConcordanceException("Inside buildConcordance -- format problem on line " + lineCounter);
					} // catch
					allBookNames.add(bookName);
					chapterNum = 0;

					out.println("Currently working on " + bookName);

				} // if

				//---------------------------------
				//----- First line of a verse -----
				//---------------------------------
				else if ((currLine.length() > 7) && (currLine.charAt(0) >= '0' && currLine.charAt(0) <= '9'))
				{
					chapterNumStr = currLine.substring(0, 3);
					verseNumStr = currLine.substring(4, 7);
					restOfLine = currLine.substring(7).trim();
					if (verseNumStr.equals("001"))
					{
						verseNum = 1;
						chapterNum++;
						chapterCounter++;
					} // if
					else
					{
						verseNum++;
					} // else
					try
					{
						if (chapterNum != Integer.parseInt(chapterNumStr) || verseNum != Integer.parseInt(verseNumStr) || currLine.charAt(3) != ':')
						{
							out.println("Inside buildConcordance() method in class Concordance (#3).");
							out.println("Format problem on line " + lineCounter);
							out.println("Terminating...");
							throw new ConcordanceException("Inside buildConcordance -- format problem on line " + lineCounter);
						} // if
					} // try
					catch(NumberFormatException nfe)
					{
						out.println("Inside buildConcordance() method in class Concordance (#4).");
						out.println("Format problem on line " + lineCounter);
						out.println("Terminating...");
						throw new ConcordanceException("Inside buildConcordance -- format problem on line " + lineCounter);
					} // catch

					verseCounter++;

					addToConcordance(restOfLine, bookNum, chapterNum, verseNum);
				} // else if

				//--------------------------
				//----- Any other line -----
				//--------------------------
				else
				{
					currLine = currLine.trim();
					addToConcordance(currLine, bookNum, chapterNum, verseNum);
				} // else

				currLine = input.readLine();
			} // while

			input.close();
		} // try
		catch(IOException ioe)
		{
			out.println("Inside buildConcordance() method in class Concordance (#5).");
			out.println("An IOException has occurred.  This is NOT the fault of the programmer.");	
			out.println("Terminating...");
			throw new ConcordanceException("Inside buildConcordance -- format problem on line " + lineCounter);
		} // catch(IOException)
	} // buildConcordance()
	//-------------------------------------------------------------------------
	/**
	 *  This method parses the String passed as input, and for each word that it contains,
	 *  adds an ID to the concordance (as long as this does not create a duplicate).
	 */
	private void addToConcordance(String currLine, short currBook, short currChap, short currVerse) throws ConcordanceException
	{
		String[] words;
		String trimmedLine = currLine.trim();
		String newWord;
		ID newID;

		if (!trimmedLine.equals(""))
		{
			words = trimmedLine.split(" ");
			for (int i = 0; i < words.length; i++)
			{
				newWord = removePunctuation(words[i]);
				newWord = customToLowerCase(newWord);
				if (newWord == null || newWord.equals(""))
				{
					out.println("Inside addToConcordance() method in class Concordance.");
					out.println("Call to removePunctuation() and makeLowerCase() returned null or empty String.");
					out.println("Terminating...");
					throw new ConcordanceException("removePunctuation() or makeLowerCase() returned null or empty String.");
				} // if

				wordCounter++;

				newID = new ID(currBook, currChap, currVerse);



				//----------------------------------------------------------------------
				//--- If newWord is not yet in the concordance, add an entry for it  ---
				//--- (the HashEntry constructor will automatically insert the first ---
				//---  reference (ID) into the associated list), and then increment  ---
				//---  distinctWordCounter.                                          ---
				//---                                                                ---
				//--- If newWord is already in the concordance, only add a new       ---
				//--- reference (ID) if it does not create a duplicate.              ---
				//----------------------------------------------------------------------
				
				// Finds where the key should be placed.
				int wordPlace = search(newWord);
				
				// If it found a place, and that place is empty...
				if (wordPlace != -1 && table[wordPlace] == null) {
					// Adds in the new HashEntry for that word and ID.
					table[wordPlace] = new HashEntry(newWord, newID);
					distinctWordCounter++;
				}
				
				// If it found the word already in there somewhere...
				else if (wordPlace != -1) {
					// Gets the ID list already in that place.
					LinkedList<ID> list = table[wordPlace].getList();
					
					// If this ID is not already on the list, adds it on.
					if (!list.getLast().equals(newID)) {
						list.add(newID);
					}
				}
			} // for
		} // if
	} // addToConcordance(String,short,short,short)
	//-------------------------------------------------------------------------
	private String removePunctuation(String inWord)
	{
		char[] punc = {'.', ',', ';', ':', '?', '!', '(', ')'};
		boolean isPunc;
		char c;
		int inWordLength, newWordLength;
		String newWord = null;

		if (inWord == null)
		{
			newWord = null;
		} // if
		if (inWord.equals(""))
		{
			newWord = "";
		} // else if
		else
		{
			//--------------------------------------------------------------
			//----- Remove any punctuation marks that appear in punc[] -----
			//--------------------------------------------------------------
			inWordLength = inWord.length();
			newWord = "";
			for (int i = 0; i < inWordLength; i++)
			{
				c = inWord.charAt(i);
				isPunc = false;
				for (int j = 0; j < punc.length; j++)
				{
					if (c == punc[j])
					{
						isPunc = true;
						break;
					} // if
				} // for

				if (!isPunc)
				{
					newWord += c;
				} // if
			} // for

			//--------------------------------------
			//----- Remove "--" at end of word -----
			//--------------------------------------
			newWordLength = newWord.length();
			if (newWordLength > 2 && newWord.substring(newWordLength-2).equals("--"))
			{
				newWord = newWord.substring(0, newWordLength-2);
			} // if

			//--------------------------------------
			//----- Remove "'s" at end of word -----
			//--------------------------------------
			newWordLength = newWord.length();
			if (newWordLength > 2 && newWord.substring(newWordLength-2).equals("'s"))
			{
				newWord = newWord.substring(0, newWordLength-2);
			} // if

			//--------------------------------------
			//----- Remove trailing apostrophe -----
			//--------------------------------------
			newWordLength = newWord.length();
			if (newWord.charAt(newWordLength-1) == '\'')
			{
				newWord = newWord.substring(0,newWordLength-1);
			} // if
		} // else

		return newWord;
	} // removePunctuation(String)
	//-------------------------------------------------------------------------
	/**
	 * Customized toLowerCase method.  Takes a String and processes each character.
	 * If a non-letter character is found (other than hyphens), returns null.
	 * Otherwise, converts every letter (other than hyphens) to lowercase and
	 * returns the resulting String.
	 *  
	 * @param  inWord  the String to be processed
	 */
	public String customToLowerCase(String inWord)
	{
		int inWordLength;
		String newWord = null;

		if (inWord == null)
		{
			newWord = null;
		} // if
		if (inWord.equals(""))
		{
			newWord = "";
		} // else if
		else
		{
			newWord = "";
			inWordLength = inWord.length();
			for (int i = 0; i < inWordLength; i++)
			{
				if (!Character.isLetter(inWord.charAt(i)) && (inWord.charAt(i) != '-'))
				{
					newWord = null;
					break;
				} // if
				else
				{
					newWord += Character.toLowerCase(inWord.charAt(i));
				} // else
			} // for
		} // else

		return newWord;
	} // customToLowerCase(String)
	//-------------------------------------------------------------------------
} // class Concordance
