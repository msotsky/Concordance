

import java.util.*;

/**
 *  Represents a single hash table entry.
 *  
 *  @author Liam Keliher
 */

public class HashEntry
{
	private String key;
	private LinkedList<ID> list;
	//-------------------------------------------------------------------------
	/**
	 * HashEntry constructor.  Sets the key value and creates a new LinkedList<ID>
	 * to which it adds the first ID.
	 * 
	 * @param inKey    key value for this hash table entry (word from text)
	 * @param firstID  first ID (text location) where the key is found
	 */
	public HashEntry(String inKey, ID firstID) throws ConcordanceException
	{
		if (inKey == null || inKey.equals("") || firstID == null)
		{
			throw new ConcordanceException("Inside HashEntry constructor -- problem with arguments");
		} // if

		key = inKey;
		list = new LinkedList<ID>();
		list.add(firstID);
	} // constructor HashEntry(String, Reference)
	//-------------------------------------------------------------------------
	/**
	 * Standard accessor method for key value.
	 */
	public String getKey()
	{
		return key;
	} // getKey()
	//-------------------------------------------------------------------------
	/**
	 * Standard accessor method for list of IDs.
	 */
	public LinkedList<ID> getList()
	{
		return list;
	} // getList()
	//-------------------------------------------------------------------------
} // class HashEntry