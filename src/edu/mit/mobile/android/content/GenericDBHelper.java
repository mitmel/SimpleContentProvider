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
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import edu.mit.mobile.android.content.annotation.SQLExtractor;
import edu.mit.mobile.android.content.dbhelper.ContentItemDBHelper;

/**
 * Provides basic CRUD database calls to handle very simple object types, eg:
 *
 * <pre>
 * content://AUTHORITY/item
 * content://AUTHORITY/item/1
 * </pre>
 *
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
public class GenericDBHelper extends ContentItemDBHelper {

    private final String mTable;
    private final SQLExtractor mExtractor;

    private boolean mCreatedTables = false;

    /**
     * @param contentItem
     *            the class that defines the content item that will be managed by this helper.
     */
    public GenericDBHelper(Class<? extends ContentItem> contentItem) {
        super(contentItem);
        mExtractor = new SQLExtractor(contentItem);
        mTable = mExtractor.getTableName();
    }

    /**
     * This default implementation drops existing tables and recreates them. If you want to preserve
     * the user's data, please override this and handle migrations more carefully.
     *
     * @see edu.mit.mobile.android.content.DBHelper#upgradeTables(android.database.sqlite.SQLiteDatabase,
     *      int, int)
     */
    @Override
    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + mTable);
        createTables(db);
    }

    public String getTable() {
        return mTable;
    }

    @Override
    public String getTargetTable() {
        return getTable();
    }

    @Override
    public String getDirType(String authority, String path) {
        return ProviderUtils.toDirType(authority, mTable);
    }

    @Override
    public String getItemType(String authority, String path) {
        return ProviderUtils.toItemType(authority, mTable);
    }

    public Class<? extends ContentItem> getContentItem() {
        return mContentItem;
    }

    @Override
    public void createTables(SQLiteDatabase db) throws SQLGenerationException {
        if (mCreatedTables) {
            return;
        }
        for (final String sqlExpression : mExtractor.getTableCreation()) {
            db.execSQL(sqlExpression);
        }
        mCreatedTables = true;
    }

    protected ContentValues callOnPreSaveListener(SQLiteDatabase db, Uri uri, ContentValues values) {
        if (mOnSaveListener != null) {
            values = mOnSaveListener.onPreSave(db, null, values);
        }
        return values;
    }

    @Override
    public Uri insertDir(SQLiteDatabase db, ContentProvider provider, Uri uri, ContentValues values)
            throws SQLException {
        values = callOnPreSaveListener(db, uri, values);

        final long id = db.insertOrThrow(mTable, null, values);
        if (id != -1) {
            return ContentUris.withAppendedId(uri, id);
        } else {
            throw new SQLException("error inserting into " + mTable);
        }
    }

    @Override
    public int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {

        values = callOnPreSaveListener(db, uri, values);

        return db.update(mTable, values,
                ProviderUtils.addExtraWhere(where, BaseColumns._ID + "=?"),
                ProviderUtils.addExtraWhereArgs(whereArgs, uri.getLastPathSegment()));
    }

    @Override
    public int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {
        values = callOnPreSaveListener(db, uri, values);

        return db.update(mTable, values, where, whereArgs);
    }

    @Override
    public int deleteItem(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        return db.delete(mTable, ProviderUtils.addExtraWhere(where, BaseColumns._ID + "=?"),
                ProviderUtils.addExtraWhereArgs(whereArgs, uri.getLastPathSegment()));
    }

    @Override
    public int deleteDir(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        return db.delete(mTable, where, whereArgs);
    }

    @Override
    public Cursor queryDir(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        return db.query(mTable, projection, selection, selectionArgs, null, null,
                sortOrder == null ? mSortOrder : sortOrder);

    }

    @Override
    public Cursor queryItem(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        return db.query(mTable, projection,
                ProviderUtils.addExtraWhere(selection, BaseColumns._ID + "=?"),
                ProviderUtils.addExtraWhereArgs(selectionArgs, uri.getLastPathSegment()), null,
                null, sortOrder == null ? mSortOrder : sortOrder);
    }
}
