package edu.mit.mobile.android.content.test.sample5;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import edu.mit.mobile.android.content.DBHelper;
import edu.mit.mobile.android.content.m2m.IdenticalChildFinder;

public class IdenticalTagFinder implements IdenticalChildFinder {
    protected static final String[] COLS = new String[] { Tag._ID };

    @Override
    public Uri getIdenticalChild(DBHelper m2m, Uri parentChildDir, SQLiteDatabase db,
            String childTable, ContentValues values) {
        final Cursor c = db.query(childTable, COLS, Tag.NAME + "=?",
                new String[] { values.getAsString(Tag.NAME) }, null, null, null);
        try {
            if (c.moveToFirst()) {
                final long id = c.getLong(c.getColumnIndex(Tag._ID));
                return ContentUris.withAppendedId(parentChildDir, id);
            }
        } finally {
            c.close();
        }
        return null;
    }
}
