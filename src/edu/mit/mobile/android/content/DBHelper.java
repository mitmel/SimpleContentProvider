package edu.mit.mobile.android.content;
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
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public interface DBHelper {

	public abstract Uri insertDir(SQLiteDatabase db, ContentProvider provider,
			Uri uri, ContentValues values) throws SQLException;

	public abstract int updateItem(SQLiteDatabase db, ContentProvider provider,
			Uri uri, ContentValues values, String where, String[] whereArgs);

	public abstract int updateDir(SQLiteDatabase db, ContentProvider provider,
			Uri uri, ContentValues values, String where, String[] whereArgs);

	public abstract int deleteItem(SQLiteDatabase db, ContentProvider provider,
			Uri uri, String where, String[] whereArgs);

	public abstract int deleteDir(SQLiteDatabase db, ContentProvider provider,
			Uri uri, String where, String[] whereArgs);

	public abstract Cursor queryDir(SQLiteDatabase db, Uri uri,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder);

	public abstract Cursor queryItem(SQLiteDatabase db, Uri uri,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder);

	public abstract String getPath();

	/**
	 * Creates the tables for the items of this helper.
	 *
	 * @param db
	 */
	public abstract void createTables(SQLiteDatabase db);

	/**
	 * Upgrades the tables for the times of this helper.
	 * @param db
	 * @param oldVersion TODO
	 * @param newVersion TODO
	 */
	public abstract void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion);
}