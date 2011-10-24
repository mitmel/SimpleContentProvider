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
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
public class GenericDBHelper extends DBHelper {

	private final String mTable;
	private final Uri mContentUri;
	private final Class<? extends ContentItem> mDataItem;

	/**
	 * @param contentItem
	 * 			  the class that defines the content item that will be managed by this helper.
	 * @param contentUri
	 *            the URI of the content directory. Eg. content://AUTHORITY/item
	 */
	public GenericDBHelper(Class<? extends ContentItem> contentItem, Uri contentUri) {
		mDataItem = contentItem;
		mTable = extractTableName();
		mContentUri = contentUri;
	}

	/**
	 * This default implementation drops existing tables and recreates them.
	 * If you want to preserve the user's data, please override this and handle
	 * migrations more carefully.
	 * 
	 * @see edu.mit.mobile.android.content.DBHelper#upgradeTables(android.database.sqlite.SQLiteDatabase,
	 *      int, int)
	 */
	@Override
	public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + mTable);
		createTables(db);
	}

	/**
	 * Inspects the {@link ContentItem} and extracts a table name from it. If
	 * there is a @DBTable annotation, uses that name. Otherwise, uses a
	 * lower-cased, sanitized version of the classname.
	 *
	 * @return a valid table name
	 * @throws SQLGenerationException
	 */
	private String extractTableName() throws SQLGenerationException {
		String tableName = null;
		final DBTable tableNameAnnotation = mDataItem.getAnnotation(DBTable.class);
		if (tableNameAnnotation != null){

			tableName = tableNameAnnotation.value();
			if (! SQLGenUtils.isValidName(tableName)){
				throw new SQLGenerationException("Illegal table name: '"+tableName+"'");
			}
		}else{
			tableName = SQLGenUtils.toValidName(mDataItem);
		}
		return tableName;
	}

	public String getTable(){
		return mTable;
	}

	@Override
	public String getPath(){
		return mTable;
	}

	private static final String DOUBLE_ESCAPE = DBColumnType.DEFAULT_VALUE_ESCAPE + DBColumnType.DEFAULT_VALUE_ESCAPE;

	@Override
	public void createTables(SQLiteDatabase db) throws SQLGenerationException {
		db.execSQL(getTableCreation());
	}


	/**
	 * Generates SQL code for creating this object's table. Creation is done by
	 * inspecting the static strings that are marked with {@link DBColumn}
	 * annotations.
	 * 
	 * @return CREATE TABLE code for creating this table.
	 * @throws SQLGenerationException
	 *             if there were any problems creating the table
	 * @see DBColumn
	 * @see DBTable
	 */
	public String getTableCreation() throws SQLGenerationException {
		try{
			final StringBuilder table = new StringBuilder();

			table.append("CREATE TABLE ");
			table.append(mTable);
			table.append(" (");

			boolean needSep = false;
			for (final Field field: mDataItem.getFields()){
				final DBColumn t = field.getAnnotation(DBColumn.class);
				if (t == null){
					continue;
				}

				final int m = field.getModifiers();

				if (!String.class.equals(field.getType()) || !Modifier.isStatic(m) || !Modifier.isFinal(m)){
					throw new SQLGenerationException("Columns defined using @DBColumn must be static final Strings.");
				}

				@SuppressWarnings("rawtypes")
				final Class<? extends DBColumnType> columnType = t.type();
				@SuppressWarnings("rawtypes")
				final DBColumnType typeInstance = columnType.newInstance();

				if (needSep){
					table.append(',');
				}
				final String dbColumnName = (String) field.get(null);
				if (! SQLGenUtils.isValidName(dbColumnName)){
					throw new SQLGenerationException("@DBColumn '"+dbColumnName+"' is not a valid SQLite column name.");
				}

				table.append(typeInstance.toCreateColumn(dbColumnName));
				if (t.primaryKey()){
					table.append(" PRIMARY KEY");
					if (t.autoIncrement()){
						table.append(" AUTOINCREMENT");
					}
				}

				if (t.unique()){
					table.append(" UNIQUE");
				}

				if (t.notnull()){
					table.append(" NOT NULL");
				}

				switch (t.collate()){
				case BINARY:
					table.append(" COLLATE BINARY");
					break;
				case NOCASE:
					table.append(" COLLATE NOCASE");
					break;
				case RTRIM:
					table.append(" COLLATE RTRIM");
					break;

				}

				final String defaultValue = t.defaultValue();
				final int defaultValueInt = t.defaultValueInt();
				final long defaultValueLong = t.defaultValueLong();
				final float defaultValueFloat = t.defaultValueFloat();
				final double defaultValueDouble = t.defaultValueDouble();


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
				}else if (defaultValueInt != DBColumn.NULL_INT){
					table.append(" DEFAULT ");
					table.append(defaultValueInt);

				}else if (defaultValueLong != DBColumn.NULL_LONG){
					table.append(" DEFAULT ");
					table.append(defaultValueLong);

				}else if (defaultValueFloat != DBColumn.NULL_FLOAT){
					table.append(" DEFAULT ");
					table.append(defaultValueFloat);

				}else if (defaultValueDouble != DBColumn.NULL_DOUBLE){
					table.append(" DEFAULT ");
					table.append(defaultValueDouble);
				}

				final String extraColDef = t.extraColDef();
				if (! DBColumn.NULL.equals(extraColDef)){
					table.append(extraColDef);
				}

				needSep = true;
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
			ContentValues values) throws SQLException {
		if (mOnSaveListener != null){
			values = mOnSaveListener.onPreSave(db, null, values);
		}
		final long id = db.insertOrThrow(mTable, null, values);
		if (id != -1){
			return ContentUris.withAppendedId(mContentUri, id);
		}else{
			throw new SQLException("error inserting into " + mTable);
		}
	}

	@Override
	public int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values, String where, String[] whereArgs) {

		if (mOnSaveListener != null){
			values = mOnSaveListener.onPreSave(db, uri, values);
		}

		return db.update(mTable, values,
				ProviderUtils.addExtraWhere(where, BaseColumns._ID + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, uri.getLastPathSegment()));
	}

	@Override
	public int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values, String where, String[] whereArgs) {
		if (mOnSaveListener != null){
			values = mOnSaveListener.onPreSave(db, uri, values);
		}
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
