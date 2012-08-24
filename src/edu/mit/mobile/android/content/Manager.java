package edu.mit.mobile.android.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

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

    public abstract String getSortOrder();

    public abstract String getPath();

}