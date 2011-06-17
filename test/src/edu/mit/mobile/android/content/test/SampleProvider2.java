package edu.mit.mobile.android.content.test;

import edu.mit.mobile.android.content.DBHelper;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.test.sample2.BlogPost;

public class SampleProvider2 extends SimpleContentProvider {
	public static final String AUTHORITY = "edu.mit.mobile.android.content.test.sampleprovider2";

	public SampleProvider2() {
		//    authority   DB ver
		super(AUTHORITY, 1);

		final DBHelper blogPostHelper = new GenericDBHelper(BlogPost.class, BlogPost.CONTENT_URI);
		addDBHelper(blogPostHelper);

		blogPostHelper.setOnSaveListener(BlogPost.ON_SAVE_LISTENER);

		addDirAndItemUri(blogPostHelper, BlogPost.PATH);
	}
}
