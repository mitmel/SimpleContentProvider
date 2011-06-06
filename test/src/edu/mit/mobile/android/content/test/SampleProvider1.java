package edu.mit.mobile.android.content.test;

import edu.mit.mobile.android.content.DBHelper;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.test.sample1.Message;

public class SampleProvider1 extends SimpleContentProvider {
	public static final String AUTHORITY = "edu.mit.mobile.android.content.test.sampleprovider1";

	public SampleProvider1() {
		//    authority       DB name     DB ver
		super(AUTHORITY, "sampleprovider1", 1);

		// This helper creates the table and can do basic CRUD for items that
		// use the dir/item scheme with the BaseColumns._ID integer primary key.
		final DBHelper messageHelper = new GenericDBHelper(Message.class, Message.CONTENT_URI);

		// This binds the helper to the provider.
		addDBHelper(messageHelper);

		// Adds a mapping between the given content:// URI path and the
		// helper.
		//
		// There's an optional third parameter which lets you have different
		// helpers handle different requests (eg. use a GenericDBHelper for
		// basic insert, delete, update, but have a custom helper for querying).
		addDirUri(messageHelper, Message.PATH);
		addItemUri(messageHelper, Message.PATH + "/#");
	}
}
