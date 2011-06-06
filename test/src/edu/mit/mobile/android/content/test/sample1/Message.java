package edu.mit.mobile.android.content.test.sample1;

import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DateColumn;
import edu.mit.mobile.android.content.column.TextColumn;
import edu.mit.mobile.android.content.test.SampleProvider1;

public class Message implements ContentItem {

	@DBColumn(type=DateColumn.class, defaultValue=DateColumn.CURRENT_TIMESTAMP)
	public static final String  CREATED_DATE = "created";

	@DBColumn(type=TextColumn.class)
	public static final String BODY = "body";

	public static final String PATH = "message";

	public static final Uri
		CONTENT_URI = Uri.parse("content://"+SampleProvider1.AUTHORITY+"/"+PATH);
}
