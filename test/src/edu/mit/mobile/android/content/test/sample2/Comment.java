package edu.mit.mobile.android.content.test.sample2;

import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.UriPath;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DBForeignKeyColumn;
import edu.mit.mobile.android.content.column.DatetimeColumn;
import edu.mit.mobile.android.content.column.TextColumn;

@UriPath(Comment.PATH)
public class Comment implements ContentItem {

	@DBColumn(type = DatetimeColumn.class, defaultValue = DatetimeColumn.NOW_IN_MILLISECONDS)
	public static final String CREATED_DATE = "created";

	@DBColumn(type=TextColumn.class)
	public static final String BODY = "body";

	// this creates a foreign key relationship to the blog post. In effect, this
	// is the child storing the ID of its parent. The ForeignKeyManager will help
	// access this relationship.
	@DBForeignKeyColumn(BlogPost.class)
	public static final String POST = "post";

	public static final String PATH = "comment";

}
