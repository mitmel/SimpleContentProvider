package edu.mit.mobile.android.content.test.sample3;

import java.util.Date;

import android.content.ContentValues;
import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.UriPath;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DatetimeColumn;
import edu.mit.mobile.android.content.column.TextColumn;
import edu.mit.mobile.android.content.m2m.M2MManager;
import edu.mit.mobile.android.content.test.SampleProvider3;

@UriPath(Project.PATH)
public class Project implements ContentItem {

	@DBColumn(type = TextColumn.class)
	public static final String NAME = "name";

	@DBColumn(type = DatetimeColumn.class)
	public static final String DUE_DATE = "due_date";

	public static final M2MManager PEOPLE = new M2MManager(Person.class);

	public static final String PATH = "project";

	public static final Uri CONTENT_URI = ProviderUtils.toContentUri(SampleProvider3.AUTHORITY,
			PATH);

	public static ContentValues toCv(String name, Date dueDate) {
		final ContentValues cv = new ContentValues();

		cv.put(NAME, name);
		cv.put(DUE_DATE, dueDate.getTime());

		return cv;
	}
}
