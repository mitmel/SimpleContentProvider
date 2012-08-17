package edu.mit.mobile.android.content.test.sample4;

import android.content.ContentValues;
import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.DBTable;
import edu.mit.mobile.android.content.ForeignKeyManager;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.UriPath;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DBForeignKeyColumn;
import edu.mit.mobile.android.content.column.TextColumn;
import edu.mit.mobile.android.content.test.SampleProvider4;

@UriPath(Person.PATH)
@DBTable("kittens")
public class Person implements ContentItem {

	@DBColumn(type = TextColumn.class)
	public static final String NAME = "name";

	@DBForeignKeyColumn(parent = Person.class, notnull = false)
	public static final String SUPERVISOR = "supervisor";

	public static final String PATH = "person";

	public static final String SUBORDINATE_PATH = "subordinates";

	public static final ForeignKeyManager SUBORDINATES = new ForeignKeyManager(Person.class,
			SUBORDINATE_PATH);

	public static final Uri CONTENT_URI = ProviderUtils.toContentUri(SampleProvider4.AUTHORITY,
			PATH);

	public static ContentValues toCv(String name) {
		final ContentValues cv = new ContentValues();

		cv.put(NAME, name);

		return cv;
	}
}
