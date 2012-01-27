package edu.mit.mobile.android.content.test.sample3;

import android.content.ContentValues;
import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.UriPath;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.TextColumn;
import edu.mit.mobile.android.content.m2m.M2MManager;
import edu.mit.mobile.android.content.test.SampleProvider3;

@UriPath(Person.PATH)
public class Person implements ContentItem {

	@DBColumn(type = TextColumn.class)
	public static final String NAME = "name";

	public static final M2MManager PROJECTS = new M2MManager(Project.class);

	public static final String PATH = "person";

	public static final Uri CONTENT_URI = ProviderUtils.toContentUri(SampleProvider3.AUTHORITY,
			PATH);

	public static ContentValues toCv(String name) {
		final ContentValues cv = new ContentValues();

		cv.put(NAME, name);

		return cv;
	}
}
