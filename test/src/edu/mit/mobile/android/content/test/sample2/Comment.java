package edu.mit.mobile.android.content.test.sample2;

import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DatetimeColumn;
import edu.mit.mobile.android.content.column.TextColumn;

public class Comment implements ContentItem {

	@DBColumn(type = DatetimeColumn.class, defaultValue = DatetimeColumn.NOW_IN_MILLISECONDS)
	public static final String CREATED_DATE = "created";

	@DBColumn(type=TextColumn.class)
	public static final String BODY = "body";

	public static final String PATH = "comment";
}
