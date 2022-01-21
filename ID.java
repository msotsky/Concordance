/**
 *  Contains the identifying information for a verse.
 *  
 *  @author Liam Keliher
 */



public class ID
{
	private short book, chapter, verse;
	//-------------------------------------------------------------------------
	public ID(short inBook, short inChapter, short inVerse)
	{
		book = inBook;
		chapter = inChapter;
		verse = inVerse;
	} // constructor ID(short,short,short)
	//-------------------------------------------------------------------------
	public boolean equals(ID compare)
	{
		if (book == compare.book && chapter == compare.chapter && verse == compare.verse)
		{
			return true;
		} // if
		else
		{
			return false;
		} // else
	} // equals(Reference)
	//-------------------------------------------------------------------------
	public short getBook()
	{
		return book;
	} // getBook()
	//-------------------------------------------------------------------------
	public short getChapter()
	{
		return chapter;
	} // getChapter()
	//-------------------------------------------------------------------------
	public short getVerse()
	{
		return verse;
	} // getVerse()
	//-------------------------------------------------------------------------
} // class ID