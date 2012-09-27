package edu.mit.mobile.android.content.test;

import edu.mit.mobile.android.content.DBHelper;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.test.sample1.Message;

public class SampleProvider1 extends SimpleContentProvider {
    public static final String AUTHORITY = "edu.mit.mobile.android.content.test.sampleprovider1";

    public SampleProvider1() {
        // authority DB ver
        super(AUTHORITY, 1);

        // This helper creates the table and can do basic CRUD for items that
        // use the dir/item scheme with the BaseColumns._ID integer primary key.
        final DBHelper messageHelper = new GenericDBHelper(Message.class);

        // Adds a mapping between the given content:// URI path and the
        // helper.
        //
        // There's an optional fourth parameter which lets you have different
        // helpers handle different SQL verbs (eg. use a GenericDBHelper for
        // basic insert, delete, update, but have a custom helper for querying).
        addDirUri(messageHelper, Message.PATH, getDirType(Message.PATH));
        addItemUri(messageHelper, Message.PATH + "/#", getItemType(Message.PATH));

    }
}
