package edu.mit.mobile.android.content;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public interface OnSaveListener {

    /**
     * This hook runs right before insert or update, allowing the data to be
     * modified before being saved to the database.
     *
     * @param db 
     *
     * @param uri the uri of the item being updated or null if the item is
     * being inserted. 
     *
     * @param cv the requested data to be updated or inserted. There is no
     * guarantee that any of the values will be present.
     *
     * @return the data that will be used for the save. This will usually be
     * the same as the passed-in cv.
	 */
    public ContentValues onPreSave(SQLiteDatabase db, Uri uri, ContentValues cv); }
