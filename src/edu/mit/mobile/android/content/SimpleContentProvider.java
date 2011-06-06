package edu.mit.mobile.android.content;
/*
 * Copyright (C) 2011 MIT Mobile Experience Lab
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
import java.util.ArrayList;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public abstract class SimpleContentProvider extends ContentProvider {
	private final String mAuthority;
	private final String mDBName;
	private final int mDBVersion;

	private final DBHelperMapper mDBHelperMapper;

	private static final UriMatcher MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	private DatabaseHelper mDatabaseHelper;

	private final List<DBHelper> mDBHelpers = new ArrayList<DBHelper>();

	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			for (final DBHelper dbHelper : mDBHelpers) {
				dbHelper.createTables(db);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			for (final DBHelper dbHelper : mDBHelpers) {
				dbHelper.upgradeTables(db, oldVersion, newVersion);
			}
		}
	}

	public SimpleContentProvider(String authority, String dbName, int dbVersion) {
		super();
		mAuthority = authority;
		mDBHelperMapper = new DBHelperMapper(mAuthority);
		mDBName = dbName;
		mDBVersion = dbVersion;
	}

	/**
	 * Registers a DBHelper with the content provider. You must call this in the
	 * constructor of any subclasses.
	 *
	 * @param dbHelper
	 */
	public void addDBHelper(DBHelper dbHelper) {
		mDBHelpers.add(dbHelper);
	}

	private int mMatcherID = 0;

	/**
	 * Adds an entry for a directory of a given type. This should be called in
	 * the constructor of any subclasses.
	 *
	 * @param dbHelper
	 *            the DBHelper to associate with the given path.
	 * @param path
	 *            a complete path on top of the content provider's authority.
	 * @param dbHelperType
	 *            one or more of {@link DBHelperMapper#TYPE_ALL},
	 *            {@link DBHelperMapper#TYPE_INSERT},
	 *            {@link DBHelperMapper#TYPE_QUERY},
	 *            {@link DBHelperMapper#TYPE_UPDATE},
	 *            {@link DBHelperMapper#TYPE_DELETE} joined bitwise.
	 */
	public void addDirUri(DBHelper dbHelper, String path, int dbHelperType) {
		mDBHelperMapper.addDirMapping(mMatcherID, dbHelper, dbHelperType);
		MATCHER.addURI(mAuthority, path, mMatcherID);
		mMatcherID++;
	}

	/**
	 * Adds an entry for a directory of a given type. This should be called in
	 * the constructor of any subclasses.
	 *
	 * Defaults to handle all method types.
	 *
	 * @param dbHelper
	 *            the DBHelper to associate with the given path.
	 * @param path
	 *            a complete path on top of the content provider's authority.
	 */
	public void addDirUri(DBHelper dbHelper, String path) {
		addDirUri(dbHelper, path, DBHelperMapper.TYPE_ALL);
	}

	/**
	 * Adds an entry for an item of a given type. This should be called in the
	 * constructor of any subclasses.
	 *
	 * @param dbHelper
	 *            the DBHelper to associate with the given path.
	 * @param path
	 *            a complete path on top of the content provider's authority.
	 *            <strong>This must end in <code>"/#"</code></strong>
	 * @param dbHelperType
	 *            one or more of {@link DBHelperMapper#TYPE_ALL},
	 *            {@link DBHelperMapper#TYPE_INSERT},
	 *            {@link DBHelperMapper#TYPE_QUERY},
	 *            {@link DBHelperMapper#TYPE_UPDATE},
	 *            {@link DBHelperMapper#TYPE_DELETE} joined bitwise.
	 */
	public void addItemUri(DBHelper dbHelper, String path, int dbHelperType) {
		mDBHelperMapper.addItemMapping(mMatcherID, dbHelper, dbHelperType);
		MATCHER.addURI(mAuthority, path, mMatcherID);
		mMatcherID++;
	}

	/**
	 * Adds an entry for an item of a given type. This should be called in the
	 * constructor of any subclasses.
	 *
	 * Defaults to handle all method types.
	 *
	 * @param dbHelper
	 *            the DBHelper to associate with the given path.
	 * @param path
	 *            a complete path on top of the content provider's authority.
	 *            <strong>This must end in <code>"/#"</code></strong>
	 */
	public void addItemUri(DBHelper dbHelper, String path) {
		addItemUri(dbHelper, path, DBHelperMapper.TYPE_ALL);
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DatabaseHelper(getContext(), mDBName, mDBVersion);
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		final int match = MATCHER.match(uri);
		if (!mDBHelperMapper.canDelete(match)) {
			throw new IllegalArgumentException("delete note supported");
		}
		return mDBHelperMapper.delete(match, this, db, uri, selection,
				selectionArgs);
	}

	@Override
	public String getType(Uri uri) {
		final int match = MATCHER.match(uri);
		return mDBHelperMapper.getType(match);
	}

	public static UriMatcher getMatcher() {
		return MATCHER;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		final int match = MATCHER.match(uri);
		if (!mDBHelperMapper.canInsert(match)) {
			throw new IllegalArgumentException("insert not supported");
		}
		final Uri newUri = mDBHelperMapper.insert(match, this, db, uri, values);
		if (newUri != null) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return newUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		final int match = MATCHER.match(uri);
		if (!mDBHelperMapper.canQuery(match)) {
			throw new IllegalArgumentException("query not supported");
		}
		final Cursor c = mDBHelperMapper.query(match, this, db, uri,
				projection, selection, selectionArgs, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		final int match = MATCHER.match(uri);
		if (!mDBHelperMapper.canUpdate(match)) {
			throw new IllegalArgumentException("update not supported");
		}
		final int changed = mDBHelperMapper.update(match, this, db, uri,
				values, selection, selectionArgs);
		if (changed != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return changed;
	}

}