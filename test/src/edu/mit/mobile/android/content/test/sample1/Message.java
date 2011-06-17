package edu.mit.mobile.android.content.test.sample1;

import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DateColumn;
import edu.mit.mobile.android.content.column.TextColumn;
import edu.mit.mobile.android.content.test.SampleProvider1;

//     The table name defaults to the classname, lowercased. If you wish to
//     override the default name, you can specify it directly:
// @DBTable(Message.TABLE)
public class Message implements ContentItem {

	//     Defining the table name as a static string will let you use it in your
	//     content provider if you ever need to do custom DB queries.
	// public static final String TABLE = "mymessage";

	// Column definitions below. ContentItem contains one column definition
	// for the BaseColumns._ID which defines the primary key.
	@DBColumn(type = DateColumn.class, defaultValue = DateColumn.CURRENT_TIMESTAMP)
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
