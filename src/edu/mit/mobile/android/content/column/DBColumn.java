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

import edu.mit.mobile.android.content.SimpleContentProvider;

/**
 * This defines a database column for use with the {@link SimpleContentProvider} framework. This
 * should be used on static final Strings, the value of which defines the column name. Various
 * column definition parameters can be set.
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
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
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
    public static final float NULL_FLOAT = Float.MIN_VALUE;
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
    Class<? extends DBColumnType<?>> type();

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
     * Sets a default value for the column. Values are automatically quoted as strings in SQL. To
     * avoid escaping (for use with reserved words and such), prefix with
     * {@link DBColumnType#DEFAULT_VALUE_ESCAPE}.
     *
     * @return the default value
     */
    String defaultValue() default NULL;

    /**
     * Sets the default value for the column.
     *
     * @return the default value
     */
    int defaultValueInt() default NULL_INT;

    /**
     * Sets the default value for the column.
     *
     * @return the default value
     */
    long defaultValueLong() default NULL_LONG;

    /**
     * Sets the default value for the column.
     *
     * @return the default value
     */
    float defaultValueFloat() default NULL_FLOAT;

    /**
     * Sets the default value for the column.
     *
     * @return the default value
     */
    double defaultValueDouble() default NULL_DOUBLE;

    /**
     * If true, ensures that this column is unique.
     *
     * @return true if this column is UNIQUE
     */
    boolean unique() default false;

    public static enum OnConflict {
        UNSPECIFIED, ROLLBACK, ABORT, FAIL, IGNORE, REPLACE
    }

    /**
     * If the column is marked unique, this determines what to do if there's a conflict. This is
     * ignored if the column is not unique.
     *
     * @return the desired conflict resolution
     */
    OnConflict onConflict() default OnConflict.UNSPECIFIED;

    public static enum CollationName {
        DEFAULT, BINARY, NOCASE, RTRIM
    }

    /**
     * Defines a collation for the column.
     *
     * @return the collation type
     */
    CollationName collate() default CollationName.DEFAULT;

    /**
     * Suffixes the column declaration with this string.
     *
     * @return a string of any supplemental column declarations
     */
    String extraColDef() default NULL;

    /**
     * Column type-specific flags.
     *
     * @return
     */
    int flags() default 0;
}
