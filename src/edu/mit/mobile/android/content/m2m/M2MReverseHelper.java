package edu.mit.mobile.android.content.m2m;

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
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import edu.mit.mobile.android.content.DBHelper;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.SQLGenerationException;

public class M2MReverseHelper extends DBHelper {
    private static final String WILDCARD_PATH_SEGMENT = "*";

    private final String mJoinTable, mFromTable;

    public M2MReverseHelper(M2MDBHelper m2mDBHelper) {
        mJoinTable = m2mDBHelper.getJoinTableName();
        mFromTable = m2mDBHelper.getFromTable();
    }

    @Override
    public Uri insertDir(SQLiteDatabase db, ContentProvider provider, Uri uri, ContentValues values)
            throws SQLException {
        throw new IllegalArgumentException("Not implemented");

    }

    @Override
    public int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {

        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public int deleteItem(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public int deleteDir(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public String getDirType(String authority, String path) {
        return ProviderUtils.toDirType(authority, mFromTable);
    }

    @Override
    public String getItemType(String authority, String path) {
        return ProviderUtils.toItemType(authority, mFromTable);
    }

    @Override
    public void createTables(SQLiteDatabase db) throws SQLGenerationException {
        // the wrapped helper will handle this.
    }

    @Override
    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion)
            throws SQLGenerationException {
        // the wrapped helper will handle this.
    }

    @Override
    public String getTargetTable() {
        return mFromTable;
    }

    /**
     * Selects rows from the FROM table that have a relation from any of the items in the TO table.
     * The ID of the item in the TO table that matches can be selected using
     * {@link #getJoinTableName()}.{@link M2MColumns#TO_ID}
     *
     * @param db
     *            DB that contains all the tables
     * @param fromProjection
     *            projection for the TO table
     * @param selection
     *            any extra selection query or null
     * @param selectionArgs
     *            any extra selection arguments or null
     * @param sortOrder
     *            the desired sort order or null
     * @return a cursor whose content represents the to table
     */
    public Cursor queryFrom(SQLiteDatabase db, String[] fromProjection, String selection,
            String[] selectionArgs, String sortOrder) {
        // XXX hack to get around ambiguous column names. Is there a better way to write this query?
        if (selection != null) {
            // matches "foo=bar" but not "foo.baz=bar"; only qualifies unqualified column names
            selection = selection.replaceAll("((?<!\\.)\\b\\w+=\\?)", mFromTable + ".$1");
        }

        return db.query(mFromTable + " INNER JOIN " + mJoinTable + " ON " + mJoinTable + "."
                + M2MColumns.FROM_ID + "=" + mFromTable + "." + BaseColumns._ID,
                ProviderUtils.addPrefixToProjection(mFromTable, fromProjection), selection,
                selectionArgs, null, null, sortOrder);
    }

    /**
     * Selects rows from the FROM table that have a relation from the given item in the TO table.
     *
     * @param toId
     *            _ID of the item on the FROM side of the relationship
     * @param db
     *            DB that contains all the tables
     * @param fromProjection
     *            projection for the TO table
     * @param selection
     *            any extra selection query or null
     * @param selectionArgs
     *            any extra selection arguments or null
     * @param sortOrder
     *            the desired sort order or null
     * @return a cursor whose content represents the to table
     */
    public Cursor queryFrom(long toId, SQLiteDatabase db, String[] fromProjection,
            String selection, String[] selectionArgs, String sortOrder) {
        return queryFrom(db, fromProjection,
                ProviderUtils.addExtraWhere(selection, mJoinTable + "." + M2MColumns.TO_ID + "=?"),
                ProviderUtils.addExtraWhereArgs(selectionArgs, Long.toString(toId)), sortOrder);
    }

    @Override
    public Cursor queryDir(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        final Uri parent = ProviderUtils.removeLastPathSegment(uri);

        if (WILDCARD_PATH_SEGMENT.equals(parent.getLastPathSegment())) {
            return queryFrom(db, projection, selection, selectionArgs, sortOrder);
        } else {
            final long parentId = ContentUris.parseId(parent);
            return queryFrom(parentId, db, projection, selection, selectionArgs, sortOrder);
        }
    }

    @Override
    public Cursor queryItem(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        final Uri parent = ProviderUtils.removeLastPathSegments(uri, 2);

        final long parentId = ContentUris.parseId(parent);

        final String childId = uri.getLastPathSegment();

        return queryFrom(parentId, db, projection,
                ProviderUtils.addExtraWhere(selection, BaseColumns._ID + "=?"),
                ProviderUtils.addExtraWhereArgs(selectionArgs, childId), sortOrder);
    }

}
