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
import java.util.HashMap;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * A helper class that will do handle standard CRUD queries for URIs that
 * represent relationships between a from and to. For example, it expects that
 * the URIs passed to it be in the form of <code>/parent/1/child</code> or
 * <code>/parent/1/child/2</code> where 1 is the ID of the parent and 2 is the
 * ID of the child.
 *
 * The easiest way to use this is to have it be a static object of the
 * {@link ContentProvider} where the provider defaults to calling the CRUD
 * methods on the helper if no other matches are found.
 *
 * @author steve
 *
 */
public final class DBHelperMapper {

	private final String mAuthority;

	private final Map<Integer, DBHelperMapItem> mDbhMap = new HashMap<Integer, DBHelperMapItem>();

	public DBHelperMapper(String authority) {
		mAuthority = authority;
	}

	/**
	 * Makes a mapping from the code to the given DBHelper. This helper will be
	 * used to handle any queries for items that match the given code. All other
	 * items will throw an error. Check {@link #canHandle(int)} and
	 * {@link #canQuery(int)}, etc. first to ensure that a query will complete.
	 *
	 * @param code
	 *            A unique ID representing the given URI; usually a
	 *            {@link UriMatcher} code
	 * @param helper
	 *            The helper that should be used for this code.
	 * @param type
	 *            The type of requests that should be handled by the helper. Any
	 *            other requests will throw an error. Types can be joined
	 *            together, eg. <code>TYPE_INSERT | TYPE_QUERY</code>
	 */
	public void addDirMapping(int code, DBHelper helper, int type){
		mDbhMap.put(code, new DBHelperMapItem(type, false, helper));
	}

	public void addItemMapping(int code, DBHelper helper, int type){
		mDbhMap.put(code, new DBHelperMapItem(type, true, helper));
	}

	public boolean canHandle(int code){
		return mDbhMap.containsKey(code);
	}

	public boolean canInsert(int code){
		final DBHelperMapItem item = mDbhMap.get(code);
		return item != null && item.allowType(TYPE_INSERT);
	}

	public boolean canQuery(int code){
		final DBHelperMapItem item = mDbhMap.get(code);
		return item != null && item.allowType(TYPE_QUERY);
	}

	public boolean canUpdate(int code){
		final DBHelperMapItem item = mDbhMap.get(code);
		return item != null && item.allowType(TYPE_UPDATE);
	}

	public boolean canDelete(int code){
		final DBHelperMapItem item = mDbhMap.get(code);
		return item != null && item.allowType(TYPE_DELETE);
	}

	public String getType(int code){
		final DBHelperMapItem dbhmi = mDbhMap.get(code);
		if (dbhmi == null){
			throw new IllegalArgumentException("no mapping for code " + code);
		}
		if (dbhmi.isItem){
			return ProviderUtils.TYPE_ITEM_PREFIX + mAuthority + "." + dbhmi.dbHelper.getPath();
		}else{
			return ProviderUtils.TYPE_DIR_PREFIX + mAuthority + "." + dbhmi.dbHelper.getPath();
		}
	}

	private String getTypeDescription(int type){
		String typeString = null;
		if      ((type & TYPE_INSERT) != 0){
			typeString = "insert";

		}else if ((type & TYPE_QUERY) != 0){
			typeString = "query";

		}else if ((type & TYPE_UPDATE) != 0){
			typeString = "update";

		}else if ((type & TYPE_DELETE) != 0){
			typeString = "delete";
		}
		return typeString;
	}

	private DBHelperMapItem getMap(int type, int code){
		final DBHelperMapItem dbhmi = mDbhMap.get(code);

		if (dbhmi == null){
			throw new IllegalArgumentException("No mapping for code "+ code);
		}
		if ((dbhmi.type & type) == 0){
			throw new IllegalArgumentException("Cannot "+getTypeDescription(type)+" for code " + code);
		}
		return dbhmi;
	}

	public Uri insert(int code, ContentProvider provider, SQLiteDatabase db, Uri uri, ContentValues values) throws SQLException {
		final DBHelperMapItem dbhmi = getMap(TYPE_INSERT, code);

		return dbhmi.dbHelper.insertDir(db, provider, uri, values);
	}

	public Cursor query(int code, ContentProvider provider, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
		final DBHelperMapItem dbhmi = getMap(TYPE_QUERY, code);

		if (dbhmi.isItem){
			return dbhmi.dbHelper.queryItem(db, uri, projection, selection, selectionArgs, sortOrder);
		}else{
			return dbhmi.dbHelper.queryDir(db, uri, projection, selection, selectionArgs, sortOrder);
		}
	}

	public int update(int code, ContentProvider provider, SQLiteDatabase db, Uri uri, ContentValues cv, String selection, String[] selectionArgs){
		final DBHelperMapItem dbhmi = getMap(TYPE_QUERY, code);

		if (dbhmi.isItem){
			return dbhmi.dbHelper.updateItem(db, provider, uri, cv, selection, selectionArgs);
		}else{
			return dbhmi.dbHelper.updateDir(db, provider, uri, cv, selection, selectionArgs);
		}
	}

	public int delete(int code, ContentProvider provider, SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs){
		final DBHelperMapItem dbhmi = getMap(TYPE_QUERY, code);

		if (dbhmi.isItem){
			return dbhmi.dbHelper.deleteItem(db, provider,uri, selection, selectionArgs);
		}else{
			return dbhmi.dbHelper.deleteDir(db, provider, uri, selection, selectionArgs);
		}
	}

	private class DBHelperMapItem {
		public DBHelperMapItem(int type, boolean isItem, DBHelper dbHelper) {
			this.type = type;
			this.dbHelper = dbHelper;
			this.isItem = isItem;
		}

		public boolean allowType(int type){
			return (this.type & type) != 0;
		}

		final DBHelper dbHelper;
		final int type;
		final boolean isItem;

	}

	public static final int
		TYPE_INSERT = 1,
		TYPE_QUERY  = 2,
		TYPE_UPDATE = 4,
		TYPE_DELETE = 8,
		TYPE_ALL = TYPE_INSERT | TYPE_QUERY | TYPE_UPDATE | TYPE_DELETE;
}