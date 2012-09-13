package edu.mit.mobile.android.content.test.sample2;
/*
 * Copyright (C) 2011  MIT Mobile Experience Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.DBSortOrder;
import edu.mit.mobile.android.content.DBTable;
import edu.mit.mobile.android.content.ForeignKeyManager;
import edu.mit.mobile.android.content.OnSaveListener;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.UriPath;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DatetimeColumn;
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
@UriPath(BlogPost.PATH)
@DBSortOrder(BlogPost.SORT_ORDER_DEFAULT)
public class BlogPost implements ContentItem {

    // Defining the table name as a static string will let you use it in your
    // content provider if you ever need to do custom DB queries.
    public static final String TABLE = "posts";

    // Column definitions below. ContentItem contains one column definition
    // for the BaseColumns._ID which defines the primary key.
    @DBColumn(type = DatetimeColumn.class, defaultValue = DatetimeColumn.NOW_IN_MILLISECONDS)
    public static final String CREATED_DATE = "created";

    @DBColumn(type = DatetimeColumn.class, defaultValue = DatetimeColumn.NOW_IN_MILLISECONDS)
    public static final String MODIFIED_DATE = "modified";

    @DBColumn(type = TextColumn.class, notnull = true)
    public static final String TITLE = "title";

    @DBColumn(type = TextColumn.class, notnull = true)
    public static final String BODY = "body";

    @DBColumn(type = TextColumn.class, unique = true, notnull = true)
    public static final String SLUG = "slug";

    // The path component of the content URI.
    public static final String PATH = "posts";

    // the DBSortOrder annotation on this class denotes the default sort order.
    public static final String SORT_ORDER_DEFAULT = CREATED_DATE + " DESC";

    // This is a helpful tool connecting back to the "child" of this object. This is similar
    // to Django's relation manager, although we need to define it ourselves.
    public static final ForeignKeyManager COMMENTS = new ForeignKeyManager(Comment.class);

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
