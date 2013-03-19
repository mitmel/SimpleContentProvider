package edu.mit.mobile.android.content.test.sample5;

import java.util.HashSet;
import java.util.Set;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.UriPath;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.TextColumn;
import edu.mit.mobile.android.content.m2m.M2MManager;

@UriPath(Bookmark.PATH)
public class Bookmark implements ContentItem {

    @DBColumn(type = TextColumn.class, notnull = true)
    public static final String TITLE = "title";

    @DBColumn(type = TextColumn.class, notnull = true)
    public static final String URL = "url";

    public static final String PATH = "bookmark";

    public static M2MManager TAGS = new M2MManager(Tag.class);

    public static Uri CONTENT_URI = ProviderUtils.toContentUri(SampleProvider5.AUTHORITY, PATH);

    public static final String[] TAG_PROJECTION = new String[] { Tag.NAME };

    public static Set<String> getTags(ContentResolver cr, Uri tagDir) {
        final Set<String> tags = new HashSet<String>();

        final Cursor c = cr.query(tagDir, TAG_PROJECTION, null, null, null);

        try {
            final int tagNameCol = c.getColumnIndexOrThrow(Tag.NAME);

            while (c.moveToNext()) {
                tags.add(c.getString(tagNameCol));
            }
        } finally {
            c.close();
        }

        return tags;
    }
}
