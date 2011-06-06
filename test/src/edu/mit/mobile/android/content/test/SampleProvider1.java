package edu.mit.mobile.android.content.test;

import edu.mit.mobile.android.content.DBHelper;
import edu.mit.mobile.android.content.DBHelperMapper;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.test.sample1.Message;

public class SampleProvider1 extends SimpleContentProvider {
	public static final String AUTHORITY = "edu.mit.mobile.android.content.test.sampleprovider1";

	public SampleProvider1() {
		super(AUTHORITY, "sampleprovider1", 1);

		final DBHelper messageHelper = new GenericDBHelper(Message.class, Message.CONTENT_URI);
		addDBHelper(messageHelper);

		addDirUri(messageHelper, Message.PATH, DBHelperMapper.TYPE_ALL);
		addItemUri(messageHelper, Message.PATH + "/#", DBHelperMapper.TYPE_ALL);
	}

}
