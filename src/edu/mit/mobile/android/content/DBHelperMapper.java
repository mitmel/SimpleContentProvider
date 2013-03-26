package edu.mit.mobile.android.content;
/*
 * Copyright (C) 2011-2013 MIT Mobile Experience Lab
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, visit
 * http://www.gnu.org/licenses/lgpl.html
 */

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

/**
 * Handles the mapping of a numeric matcher code (usually the code that's used in conjunction with
 * {@link UriMatcher}) to a given {@link DBHelper}.
 *
 * This maintains the state of which verbs are permitted for the given path and only executes a CRUD
 * verb if it has permission. Permission can be checked using {@link #canInsert(int)} and friends.
 *
 */
public final class DBHelperMapper {
    private static final String TAG = DBHelperMapper.class.getSimpleName();
    private final SparseArray<DBHelperMapItem> mDbhMap = new SparseArray<DBHelperMapItem>();

    /**
     * Makes a mapping from the code to the given DBHelper. This helper will be used to handle any
     * queries for items that match the given code. All other items will throw an error. Check
     * {@link #canHandle(int)} and {@link #canQuery(int)}, etc. first to ensure that a query will
     * complete.
     *
     * @param code
     *            A unique ID representing the given URI; usually a {@link UriMatcher} code
     * @param helper
     *            The helper that should be used for this code.
     * @param verb
     *            The SQL verbs that should be handled by the helper. Any other requests will throw
     *            an error. Verbs can be joined together, eg. <code>VERB_INSERT | VERB_QUERY</code>
     * @param type
     *            the MIME type of the item to add.
     */
    public void addDirMapping(int code, DBHelper helper, int verb, String type) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "registered dir mapping of type " + type + " to " + helper);
        }
        mDbhMap.put(code, new DBHelperMapItem(verb, false, type, helper));
    }

    /**
     * Makes a mapping from the code to the given DBHelper. This helper will be used to handle any
     * queries for items that match the given code. All other items will throw an error. Check
     * {@link #canHandle(int)} and {@link #canQuery(int)}, etc. first to ensure that a query will
     * complete.
     *
     * @param code
     *            A unique ID representing the given URI; usually a {@link UriMatcher} code
     * @param helper
     *            The helper that should be used for this code.
     * @param verb
     *            The SQL verbs that should be handled by the helper. Any other requests will throw
     *            an error. Verbs can be joined together, eg. <code>VERB_INSERT | VERB_QUERY</code>
     * @param type
     *            the MIME type of the item to add.
     */
    public void addItemMapping(int code, DBHelper helper, int verb, String type) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "registered item mapping of type " + type + " to " + helper);
        }
        mDbhMap.put(code, new DBHelperMapItem(verb, true, type, helper));
    }

    /**
     * @param code
     * @return true if this helper has a mapping for the given code
     */
    public boolean canHandle(int code) {
        return mDbhMap.get(code) != null;
    }

    /**
     * @param code
     * @return true if this helper is allowed to insert for the given code
     */
    public boolean canInsert(int code) {
        final DBHelperMapItem item = mDbhMap.get(code);
        return item != null && item.allowVerb(VERB_INSERT);
    }

    /**
     * @param code
     * @return true if this helper is allowed to query for the given code
     */
    public boolean canQuery(int code) {
        final DBHelperMapItem item = mDbhMap.get(code);
        return item != null && item.allowVerb(VERB_QUERY);
    }

    /**
     * @param code
     * @return true if this helper is allowed to update for the given code
     */
    public boolean canUpdate(int code) {
        final DBHelperMapItem item = mDbhMap.get(code);
        return item != null && item.allowVerb(VERB_UPDATE);
    }

    /**
     * @param code
     * @return true if this helper is allowed to delete for the given code
     */
    public boolean canDelete(int code) {
        final DBHelperMapItem item = mDbhMap.get(code);
        return item != null && item.allowVerb(VERB_DELETE);
    }

    /**
     * @param code
     * @return the MIME type for the given item.
     */
    public String getType(int code) {
        final DBHelperMapItem dbhmi = mDbhMap.get(code);
        if (dbhmi == null) {
            throw new IllegalArgumentException("no mapping for code " + code);
        }
        return dbhmi.type;
    }

    private String getVerbDescription(int verb) {
        String verbString = null;
        if ((verb & VERB_INSERT) != 0) {
            verbString = "insert";

        } else if ((verb & VERB_QUERY) != 0) {
            verbString = "query";

        } else if ((verb & VERB_UPDATE) != 0) {
            verbString = "update";

        } else if ((verb & VERB_DELETE) != 0) {
            verbString = "delete";
        }
        return verbString;
    }

    private DBHelperMapItem getMap(int verb, int code) {
        final DBHelperMapItem dbhmi = mDbhMap.get(code);

        if (dbhmi == null) {
            throw new IllegalArgumentException("No mapping for code " + code);
        }
        if ((dbhmi.verb & verb) == 0) {
            throw new IllegalArgumentException("Cannot " + getVerbDescription(verb) + " for code "
                    + code);
        }
        return dbhmi;
    }

    public Uri insert(int code, ContentProvider provider, SQLiteDatabase db, Uri uri,
            ContentValues values) throws SQLException {
        final DBHelperMapItem dbhmi = getMap(VERB_INSERT, code);

        return dbhmi.dbHelper.insertDir(db, provider, uri, values);
    }

    public Cursor query(int code, ContentProvider provider, SQLiteDatabase db, Uri uri,
            String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final DBHelperMapItem dbhmi = getMap(VERB_QUERY, code);

        if (dbhmi.isItem) {
            return dbhmi.dbHelper.queryItem(db, uri, projection, selection, selectionArgs,
                    sortOrder);
        } else {
            return dbhmi.dbHelper
                    .queryDir(db, uri, projection, selection, selectionArgs, sortOrder);
        }
    }

    public int update(int code, ContentProvider provider, SQLiteDatabase db, Uri uri,
            ContentValues cv, String selection, String[] selectionArgs) {
        final DBHelperMapItem dbhmi = getMap(VERB_QUERY, code);

        if (dbhmi.isItem) {
            return dbhmi.dbHelper.updateItem(db, provider, uri, cv, selection, selectionArgs);
        } else {
            return dbhmi.dbHelper.updateDir(db, provider, uri, cv, selection, selectionArgs);
        }
    }

    public int delete(int code, ContentProvider provider, SQLiteDatabase db, Uri uri,
            String selection, String[] selectionArgs) {
        final DBHelperMapItem dbhmi = getMap(VERB_QUERY, code);

        if (dbhmi.isItem) {
            return dbhmi.dbHelper.deleteItem(db, provider, uri, selection, selectionArgs);
        } else {
            return dbhmi.dbHelper.deleteDir(db, provider, uri, selection, selectionArgs);
        }
    }

    private class DBHelperMapItem {
        public DBHelperMapItem(int verb, boolean isItem, String type, DBHelper dbHelper) {
            this.verb = verb;
            this.dbHelper = dbHelper;
            this.isItem = isItem;
            this.type = type;
        }

        public boolean allowVerb(int verb) {
            return (this.verb & verb) != 0;
        }

        final DBHelper dbHelper;
        final int verb;
        final boolean isItem;
        final String type;

    }

    public static final int VERB_INSERT = 1, VERB_QUERY = 2, VERB_UPDATE = 4, VERB_DELETE = 8,
            VERB_ALL = VERB_INSERT | VERB_QUERY | VERB_UPDATE | VERB_DELETE;
}
