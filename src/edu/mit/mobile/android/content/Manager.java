package edu.mit.mobile.android.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Interface to provide common shortcuts for interacting with {@link ContentItem}s that relate to
 * one another.
 *
 */
public interface Manager {

    /**
     * @param parent
     * @return the URI of the list of items under this parent
     */
    public abstract Uri getUri(Uri parent);

    /**
     * Adds an item to the destination
     *
     * @param cr
     * @param parent
     * @param cv
     * @return
     */
    public abstract Uri insert(ContentResolver cr, Uri parent, ContentValues cv);

    public abstract Cursor query(ContentResolver cr, Uri parent, String[] projection);

    /**
     * @return the sort order of the given {@link ContentItem}
     */
    public abstract String getSortOrder();

    /**
     * @return the path segment used in URIs for this {@link ContentItem}
     */
    public abstract String getPath();

}