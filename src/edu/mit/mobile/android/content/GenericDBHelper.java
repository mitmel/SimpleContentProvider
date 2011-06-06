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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DBColumnType;

/**
 * Provides basic CRUD database calls to handle very simple object types, eg:
 *
 * content://AUTHORITY/item
 * content://AUTHORITY/item/1
 *
 *
 *
 * @author Steve Pomeroy <spomeroy@mit.edu>
 *
 */
public class GenericDBHelper implements DBHelper {

	private final String mTable;
	private final Uri mContentUri;
	private final Class<? extends ContentItem> mDataItem;

	/**
	 * @param table
	 *            the table that the items are stored in. Must have a
	 *            BaseColumns._ID column.
	 * @param contentUri
	 *            the URI of the content directory. Eg. content://AUTHORITY/item
	 */
	public GenericDBHelper(Class<? extends ContentItem> contentItem, Uri contentUri) {
		mDataItem = contentItem;
		mTable = extractTableName();
		mContentUri = contentUri;
	}

	public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + mTable);
		createTables(db);
	}

	private String extractTableName(){
		String tableName = null;
		try{
		for (final Field field: mDataItem.getFields()){
			final int m = field.getModifiers();
			if (!String.class.equals(field.getType()) && !Modifier.isStatic(m) && !Modifier.isFinal(m)){
				continue;
			}

			final DBTable t = field.getAnnotation(DBTable.class);
			if (t != null){
				tableName = (String) field.get(null);
				break;
			}
		}
		if (tableName == null){
			tableName = mDataItem.getSimpleName().toLowerCase();
		}
		}catch(final IllegalAccessException e){

		}
		return tableName;
	}

	public String getTable(){
		return mTable;
	}

	public String getPath(){
		return mTable;
	}

	private static final String DOUBLE_ESCAPE = DBColumnType.DEFAULT_VALUE_ESCAPE + DBColumnType.DEFAULT_VALUE_ESCAPE;

	public void createTables(SQLiteDatabase db){
		try {
			db.execSQL(getTableCreation());

		} catch (final SQLGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getTableCreation() throws SQLGenerationException {
		try{
			final StringBuilder table = new StringBuilder();

			table.append("CREATE TABLE ");
			table.append(mTable);
			table.append(" (");

			boolean needSep = false;
			for (final Field field: mDataItem.getFields()){
				final int m = field.getModifiers();
				if (!String.class.equals(field.getType()) && !Modifier.isStatic(m) && !Modifier.isFinal(m)){
					continue;
				}

				final DBColumn t = field.getAnnotation(DBColumn.class);
				if (t != null){
					@SuppressWarnings("rawtypes")
					final Class<? extends DBColumnType> columnType = t.type();
					@SuppressWarnings("rawtypes")
					final DBColumnType typeInstance = columnType.newInstance();

					if (needSep){
						table.append(',');
					}
					final Object fieldValue = field.get(null);
					if (!(fieldValue instanceof String)){
						throw new SQLGenerationException("static field '"+field.getName()+"' must be type String." );
					}
					final String dbColumnName = (String) fieldValue;

					table.append(typeInstance.toCreateColumn(dbColumnName));
					if (t.primaryKey()){
						table.append(" PRIMARY KEY");
						if (t.autoIncrement()){
							table.append(" AUTOINCREMENT");
						}
					}

					if (t.notnull()){
						table.append(" NOT NULL");
					}

					final String defaultValue = t.defaultValue();

					if (! DBColumn.NULL.equals(defaultValue)){
						table.append(" DEFAULT ");
						// double-escape to insert the escape character literally.
						if (defaultValue.startsWith(DOUBLE_ESCAPE)){
							DatabaseUtils.appendValueToSql(table, defaultValue.substring(1));

						}else if(defaultValue.startsWith(DBColumnType.DEFAULT_VALUE_ESCAPE)){
							table.append(defaultValue.substring(1));

						}else{

							DatabaseUtils.appendValueToSql(table, defaultValue);
						}
					}

					needSep = true;
				}
			}
			table.append(")");

			final String result = table.toString();

			return result;

		} catch (final IllegalArgumentException e) {
			throw new SQLGenerationException("field claimed to be static, but something went wrong on invocation", e);

		} catch (final IllegalAccessException e) {
			throw new SQLGenerationException("default constructor not visible", e);

		} catch (final SecurityException e) {
			throw new SQLGenerationException("cannot access class fields", e);

		} catch (final InstantiationException e) {
			throw new SQLGenerationException("cannot instantiate field type class", e);
		}
	}

	@Override
	public Uri insertDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values) {
		final long id = db.insert(mTable, null, values);
		if (id != -1){
			return ContentUris.withAppendedId(mContentUri, id);
		}else{
			throw new SQLException("error inserting into " + mTable);
		}
	}

	@Override
	public int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values, String where, String[] whereArgs) {

		return db.update(mTable, values,
				ProviderUtils.addExtraWhere(where, BaseColumns._ID + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, uri.getLastPathSegment()));
	}

	@Override
	public int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values, String where, String[] whereArgs) {
		return db.update(mTable, values, where, whereArgs);
	}

	@Override
	public int deleteItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
			String where, String[] whereArgs) {
		return db.delete(mTable,
				ProviderUtils.addExtraWhere(where, BaseColumns._ID + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, uri.getLastPathSegment()));
	}

	@Override
	public int deleteDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			String where, String[] whereArgs) {
		return db.delete(mTable, where, whereArgs);
	}

	@Override
	public Cursor queryDir(SQLiteDatabase db, Uri uri,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {

		return db.query(
				mTable,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder);

	}

	@Override
	public Cursor queryItem(SQLiteDatabase db, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {

		return db.query(
				mTable,
				projection,
				ProviderUtils.addExtraWhere(selection, BaseColumns._ID+"=?"),
				ProviderUtils.addExtraWhereArgs(selectionArgs, uri.getLastPathSegment()),
				null,
				null,
				sortOrder);
	}
}
