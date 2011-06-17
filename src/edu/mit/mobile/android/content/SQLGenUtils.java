package edu.mit.mobile.android.content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper functions for SQL generation.
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
public class SQLGenUtils {

	// this pattern defines what a valid name (table name, column name, etc.) is in SQLite.
	private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_]+");
	// the inverse of the above pattern.
	private static final Pattern NON_NAME_CHARS = Pattern.compile("[^A-Za-z0-9_]+");

	/**
	 * Creates a valid SQLite name from the Java classname, lowercased.
	 *
	 * @param myClass
	 * @return a valid SQL name
	 */
	public static final String toValidName(Class<? extends Object> myClass){
		return toValidName(myClass.getSimpleName().toLowerCase());
	}

	/**
	 * Removes any non-name characters from the given name.
	 * @param name
	 * @return a valid SQL name
	 */
	public static final String toValidName(String name){
		// strip out any non-name characters from the name.
		final Matcher m = NON_NAME_CHARS.matcher(name);
		name = m.replaceAll("");

		return name;
	}

	/**
	 * @param name
	 * @return true if the name is a valid SQLite name.
	 */
	public static boolean isValidName(String name){
		return VALID_NAME.matcher(name).matches();
	}
}
