package edu.mit.mobile.android.content.test.sample2;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.DBTable;
import edu.mit.mobile.android.content.OnSaveListener;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DateColumn;
import edu.mit.mobile.android.content.column.TextColumn;
import edu.mit.mobile.android.content.test.SampleProvider2;

/**
 * A slightly more complex example to test. Doesn't entirely make sense as a
 * data item on a phone, but serves as a nice, well-understood demonstration and
 * test.
 *
 * @author steve
 *
 */
@DBTable(BlogPost.TABLE)
public class BlogPost implements ContentItem {

	// Defining the table name as a static string will let you use it in your
	// content provider if you ever need to do custom DB queries.
	public static final String TABLE = "posts";

	// Column definitions below. ContentItem contains one column definition
	// for the BaseColumns._ID which defines the primary key.
	@DBColumn(type = DateColumn.class, defaultValue = DateColumn.CURRENT_TIMESTAMP)
	public static final String CREATED_DATE = "created";

	@DBColumn(type = DateColumn.class, defaultValue = DateColumn.CURRENT_TIMESTAMP)
	public static final String MODIFIED_DATE = "modified";

	@DBColumn(type = TextColumn.class, notnull = true)
	public static final String TITLE = "title";

	@DBColumn(type = TextColumn.class, notnull = true)
	public static final String BODY = "body";

	@DBColumn(type = TextColumn.class, unique = true, notnull = true)
	public static final String SLUG = "slug";

	// The path component of the content URI.
	public static final String PATH = "posts";

	// The SimpleContentProvider constructs content URIs based on your provided
	// path and authority.
	// This constant is not necessary, but is very handy for doing queries.
	public static final Uri CONTENT_URI = ProviderUtils.toContentUri(SampleProvider2.AUTHORITY, PATH);

	public static final OnSaveListener ON_SAVE_LISTENER = new OnSaveListener(){
		@Override
		public ContentValues onPreSave(SQLiteDatabase db, Uri uri, ContentValues cv) {
			if (! cv.containsKey(SLUG) && cv.containsKey(TITLE)){
				final String slug = cv.getAsString(TITLE).replaceAll("\\s+", "-").replaceAll("[^\\w-]+", "");
				cv.put(SLUG, slug);
			}
			return cv;
		}
	};
}
