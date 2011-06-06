package edu.mit.mobile.android.content.column;

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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
 * @author steve
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DBColumn {

	// this is required because Java doesn't allow null as a default value.
	public static final String NULL = "██████NULL██████";

	/**
	 * Specify one of the column types by passing its class object.
	 *
	 *
	 * @see IntegerColumn
	 * @see TextColumn
	 * @see DateColumn
	 *
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends DBColumnType> type();

	/**
	 * Sets this column to be NOT NULL.
	 *
	 * @return
	 */
	boolean notnull() default false;

	/**
	 * Sets this column to be a PRIMARY KEY.
	 *
	 * @return
	 */
	boolean primaryKey() default false;

	/**
	 * Adds the AUTOINCREMENT flag if {@link #primaryKey()} has also been set.
	 *
	 * @return
	 */
	boolean autoIncrement() default false;

	/**
	 * Sets a default value for the column. Values are automatically quoted as
	 * strings in SQL. To avoid escaping (for use with reserved words and such),
	 * prefix with {@link DBColumnType#DEFAULT_VALUE_ESCAPE}.
	 *
	 * @return
	 */
	String defaultValue() default NULL;

}
