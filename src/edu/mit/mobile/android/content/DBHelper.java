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
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Classes should extend this class and pass the result to {@link DBHelperMapper} to create their
 * content providers.
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
public abstract class DBHelper {

    public abstract Uri insertDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values) throws SQLException;

    public abstract int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs);

    public abstract int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs);

    public abstract int deleteItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
            String where, String[] whereArgs);

    public abstract int deleteDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
            String where, String[] whereArgs);

    public abstract Cursor queryDir(SQLiteDatabase db, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder);

    public abstract Cursor queryItem(SQLiteDatabase db, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder);

    public abstract String getDirType(String authority, String path);

    public abstract String getItemType(String authority, String path);

    /**
     * Creates the tables for the items of this helper.
     *
     * @param db
     */
    public abstract void createTables(SQLiteDatabase db) throws SQLGenerationException;

    /**
     * Upgrades the tables for the times of this helper.
     *
     * @param db
     * @param oldVersion
     *            the old version number of the database
     * @param newVersion
     *            the new, current version number
     */
    public abstract void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion)
            throws SQLGenerationException;

    public abstract String getTargetTable();

    protected OnSaveListener mOnSaveListener;

    /**
     * Registers an {@link OnSaveListener} to be called right before insert/update.
     *
     * @param onSaveListener
     */
    public void setOnSaveListener(OnSaveListener onSaveListener) {
        mOnSaveListener = onSaveListener;
    }

    /**
     * Removes any {@link OnSaveListener} formerly registered with
     * {@link #setOnSaveListener(OnSaveListener)}.
     */
    public void removeOnSaveListener() {
        mOnSaveListener = null;
    }
}
