package edu.mit.mobile.android.content.test.sample1;

import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.TextColumn;
import edu.mit.mobile.android.content.column.TimestampColumn;
import edu.mit.mobile.android.content.test.SampleProvider1;


/**
 * A simple message with a body and creation date.
 */
public class Message implements ContentItem {

	// Column definitions below. ContentItem contains one column definition
	// for the BaseColumns._ID which defines the primary key.
	@DBColumn(type = TimestampColumn.class, defaultValue = TimestampColumn.CURRENT_TIMESTAMP)
	public static final String CREATED_DATE = "created";

	@DBColumn(type = TextColumn.class)
	public static final String BODY = "body";

	// The path component of the content URI.
	public static final String PATH = "message";

	// The SimpleContentProvider constructs content URIs based on your provided
	// path and authority.
	// This constant is not necessary, but is very handy for doing queries.
	public static final Uri CONTENT_URI = ProviderUtils.toContentUri(SampleProvider1.AUTHORITY, PATH);

}
