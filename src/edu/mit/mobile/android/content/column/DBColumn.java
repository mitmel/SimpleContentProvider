package edu.mit.mobile.android.content.column;
/*
 * Copyright (C) 2011 MIT Mobile Experience Lab
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.database.DatabaseUtils;
import edu.mit.mobile.android.content.AndroidVersions;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.DBTable;
import edu.mit.mobile.android.content.SQLGenUtils;
import edu.mit.mobile.android.content.SQLGenerationException;
import edu.mit.mobile.android.content.SimpleContentProvider;

/**
 * This defines a database column for use with the {@link SimpleContentProvider}
 * framework. This should be used on static final Strings, the value of which
 * defines the column name. Various column definition parameters can be set.
 *
 * eg.:
 *
 * <pre>
 *     &#0064;DBColumn(type=IntegerColumn.class)
 *     final static String MY_COL = "my_col"
 * </pre>
 *
 * The names and structure of this are based loosely on Django's Model/Field framework.
 *
 * @author steve
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DBColumn {

	// this is required because Java doesn't allow null as a default value.
	// For some reason, null is not considered a constant expression.
	public static final String NULL = "██████NULL██████";

	public static final long NULL_LONG = Long.MIN_VALUE;
	public static final int NULL_INT = Integer.MIN_VALUE;
	public static final float  NULL_FLOAT = Float.MIN_VALUE;
	public static final double NULL_DOUBLE = Double.MIN_VALUE;

	/**
	 * Specify one of the column types by passing its class object.
	 *
	 *
	 * @see IntegerColumn
	 * @see TextColumn
	 * @see TimestampColumn
	 * @see DatetimeColumn
	 *
	 * @return the column type
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends DBColumnType> type();

	/**
	 * Sets this column to be NOT NULL.
	 *
	 * @return true if the column is NOT NULL
	 */
	boolean notnull() default false;

	/**
	 * Sets this column to be a PRIMARY KEY.
	 *
	 * @return true if the column is a primary key
	 */
	boolean primaryKey() default false;

	/**
	 * Adds the AUTOINCREMENT flag if {@link #primaryKey()} has also been set.
	 *
	 * @return true if this column should be auto-incremented
	 */
	boolean autoIncrement() default false;

	/**
	 * Sets a default value for the column. Values are automatically quoted as
	 * strings in SQL. To avoid escaping (for use with reserved words and such),
	 * prefix with {@link DBColumnType#DEFAULT_VALUE_ESCAPE}.
	 *
	 * @return the default value
	 */
	String defaultValue() default NULL;

	/**
	 * Sets the default value for the column.
	 * @return the default value
	 */
	int defaultValueInt() default NULL_INT;

	/**
	 * Sets the default value for the column.
	 * @return the default value
	 */
	long defaultValueLong() default NULL_LONG;

	/**
	 * Sets the default value for the column.
	 * @return the default value
	 */
	float defaultValueFloat() default NULL_FLOAT;

	/**
	 * Sets the default value for the column.
	 * @return the default value
	 */
	double defaultValueDouble() default NULL_DOUBLE;

	/**
	 * If true, ensures that this column is unique.
	 *
	 * @return true if this column is UNIQUE
	 */
	boolean unique() default false;

	public static enum CollationName {
		DEFAULT,
		BINARY,
		NOCASE,
		RTRIM
	}

	/**
	 * Defines a collation for the column.
	 *
	 * @return the collation type
	 */
	CollationName collate() default CollationName.DEFAULT;

	/**
	 * Suffixes the column declaration with this string.
	 * @return a string of any supplemental column declarations
	 */
	String extraColDef() default NULL;


	public static class Extractor {

		private static final String DOUBLE_ESCAPE = DBColumnType.DEFAULT_VALUE_ESCAPE + DBColumnType.DEFAULT_VALUE_ESCAPE;

		/**
		 * Inspects the {@link ContentItem} and extracts a table name from it. If
		 * there is a @DBTable annotation, uses that name. Otherwise, uses a
		 * lower-cased, sanitized version of the classname.
		 *
		 * @return a valid table name
		 * @throws SQLGenerationException
		 */
		public static String extractTableName(Class<? extends ContentItem> mDataItem) throws SQLGenerationException {
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

		private static String getDbColumnName(Field field) throws IllegalArgumentException, IllegalAccessException{
			final String dbColumnName = (String) field.get(null);
			if (! SQLGenUtils.isValidName(dbColumnName)){
				throw new SQLGenerationException("@DBColumn '"+dbColumnName+"' is not a valid SQLite column name.");
			}

			return dbColumnName;
		}

		private static void appendColumnDef(StringBuilder tableSQL, DBColumn t, Field field) throws IllegalAccessException, InstantiationException{
			@SuppressWarnings("rawtypes")
			final Class<? extends DBColumnType> columnType = t.type();
			@SuppressWarnings("rawtypes")
			final DBColumnType typeInstance = columnType.newInstance();

			tableSQL.append(typeInstance.toCreateColumn(getDbColumnName(field)));
			if (t.primaryKey()){
				tableSQL.append(" PRIMARY KEY");
				if (t.autoIncrement()){
					tableSQL.append(" AUTOINCREMENT");
				}
			}

			if (t.unique()){
				tableSQL.append(" UNIQUE");
			}

			if (t.notnull()){
				tableSQL.append(" NOT NULL");
			}

			switch (t.collate()){
			case BINARY:
				tableSQL.append(" COLLATE BINARY");
				break;
			case NOCASE:
				tableSQL.append(" COLLATE NOCASE");
				break;
			case RTRIM:
				tableSQL.append(" COLLATE RTRIM");
				break;

			}

			final String defaultValue = t.defaultValue();
			final int defaultValueInt = t.defaultValueInt();
			final long defaultValueLong = t.defaultValueLong();
			final float defaultValueFloat = t.defaultValueFloat();
			final double defaultValueDouble = t.defaultValueDouble();


			if (! DBColumn.NULL.equals(defaultValue)){
				tableSQL.append(" DEFAULT ");
				// double-escape to insert the escape character literally.
				if (defaultValue.startsWith(DOUBLE_ESCAPE)){
					DatabaseUtils.appendValueToSql(tableSQL, defaultValue.substring(1));

				}else if(defaultValue.startsWith(DBColumnType.DEFAULT_VALUE_ESCAPE)){
					tableSQL.append(defaultValue.substring(1));

				}else{

					DatabaseUtils.appendValueToSql(tableSQL, defaultValue);
				}
			}else if (defaultValueInt != DBColumn.NULL_INT){
				tableSQL.append(" DEFAULT ");
				tableSQL.append(defaultValueInt);

			}else if (defaultValueLong != DBColumn.NULL_LONG){
				tableSQL.append(" DEFAULT ");
				tableSQL.append(defaultValueLong);

			}else if (defaultValueFloat != DBColumn.NULL_FLOAT){
				tableSQL.append(" DEFAULT ");
				tableSQL.append(defaultValueFloat);

			}else if (defaultValueDouble != DBColumn.NULL_DOUBLE){
				tableSQL.append(" DEFAULT ");
				tableSQL.append(defaultValueDouble);
			}

			final String extraColDef = t.extraColDef();
			if (! DBColumn.NULL.equals(extraColDef)){
				tableSQL.append(extraColDef);
			}
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
		public static String getTableCreation(Class<? extends ContentItem> mDataItem, String mTable) throws SQLGenerationException {
			try{
				final StringBuilder tableSQL = new StringBuilder();

				tableSQL.append("CREATE TABLE ");
				tableSQL.append(mTable);
				tableSQL.append(" (");

				boolean needSep = false;
				for (final Field field: mDataItem.getFields()){
					final DBColumn t = field.getAnnotation(DBColumn.class);
					final DBForeignKeyColumn fk = field.getAnnotation(DBForeignKeyColumn.class);
					if (t == null && fk == null){
						continue;
					}

					final int m = field.getModifiers();

					if (!String.class.equals(field.getType()) || !Modifier.isStatic(m) || !Modifier.isFinal(m)){
						throw new SQLGenerationException("Columns defined using @DBColumn must be static final Strings.");
					}


					if (needSep){
						tableSQL.append(',');
					}

					if (t != null){
						appendColumnDef(tableSQL, t, field);

					}else if (fk != null){
						appendFKColumnDef(tableSQL, fk, field);
					}

					needSep = true;
				}
				tableSQL.append(")");

				final String result = tableSQL.toString();

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

		private static void appendFKColumnDef(StringBuilder tableSQL,
				DBForeignKeyColumn fk, Field field) throws IllegalArgumentException, IllegalAccessException {

			tableSQL.append("'");
			tableSQL.append(getDbColumnName(field));
			tableSQL.append("' INTEGER NOT NULL");

			if (AndroidVersions.SQLITE_SUPPORTS_FOREIGN_KEYS){
				tableSQL.append(" REFERENCES ");
				final String parentTable = extractTableName(fk.value());
				tableSQL.append("'");
				tableSQL.append(parentTable);
				tableSQL.append("' (");
				tableSQL.append(ContentItem._ID);
				tableSQL.append(") ON DELETE CASCADE"); // TODO make this configurable
			}
		}
	}
}

