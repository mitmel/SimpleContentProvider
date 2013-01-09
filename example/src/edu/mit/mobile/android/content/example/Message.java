package edu.mit.mobile.android.content.example;

import android.content.ContentProvider;
import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.DBTable;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DatetimeColumn;
import edu.mit.mobile.android.content.column.TextColumn;

/**
 * <p>
 * This represents a message. This class is never instantiated, it just contains static definitions
 * that can be used to access messages using the {@link ContentProvider} framework.
 * </p>
 *
 * <p>
 * The database table name is a sanitized, lower-cased version of this classname. In this case it is
 * "message". You can override this by using the {@link DBTable} annotation on this class.
 * </p>
 *
 */
public class Message implements ContentItem {

    // Column definitions ///////////////////////////////////

    // ContentItem contains one column definition for the BaseColumns._ID which
    // defines the primary key.

    // An example column that is automatically set to the current date/time.
    // The value of the string is the column name.
    @DBColumn(type = DatetimeColumn.class, defaultValue = DatetimeColumn.NOW_IN_MILLISECONDS)
    public static final String CREATED_DATE = "created";

    // An example text column, representing
    @DBColumn(type = TextColumn.class)
    public static final String TITLE = "title";

    @DBColumn(type = TextColumn.class)
    public static final String BODY = "body";

    // //////////////////////////////////////////////////////

    // This defines the path component of the content URI.
    // For most instances, it's best to just use the classname here:
    public static final String PATH = "message";

    // The SimpleContentProvider constructs content URIs based on your provided
    // path and authority.
    // This constant is not necessary, but is very handy for doing queries.
    public static final Uri CONTENT_URI = ProviderUtils.toContentUri(
            SampleProvider.AUTHORITY, PATH);

    /**
     * <p>
     * The content type representing a message item.
     * </p>
     *
     * <p>
     * This content type is automatically generated based on the provider name and the table name.
     * See {@link ProviderUtils#toItemType(String, String)} for details.
     * </p>
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.item/vnd.edu.mit.mobile.android.content.example.sampleprovider.message";

}
