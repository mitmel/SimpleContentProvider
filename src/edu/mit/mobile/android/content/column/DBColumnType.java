package edu.mit.mobile.android.content.column;
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

import android.database.Cursor;

public abstract class DBColumnType<T> {
    /**
     * Generates the SQL necessary to create a column
     *
     * @param colName
     * @return the column creation SQL code
     */
    public abstract String toCreateColumn(String colName);

    /**
     * If you wish to execute extra SQL before the table is created, override this method. No need
     * to call the superclass.
     * 
     * @param column
     *            the name of this column
     * @return SQL to execute or null (this class's implementation)
     */
    public String preTableSql(String table, String column, int flags) {
        return null;
    };

    /**
     * If you wish to execute extra SQL after the table is created, override this method. No need to
     * call the superclass.
     * 
     * @param column
     *            the name of this column
     * @return SQL to execute or null (this class's implementation)
     */
    public String postTableSql(String table, String column, int flags) {
        return null;
    };

    /**
     * @param c
     * @param colNumber
     * @return the value of the given column on the supplied cursor
     */
    public abstract T get(Cursor c, int colNumber);

    protected String toColumnDef(String colName, String type) {
        return "'" + colName + "' " + type;
    }

    /**
     * Prefix the default value with this character in order to prevent auto-quoting. Prefix the
     * default value with this character twice to insert it literally. This character only needs to
     * be escaped if it's at the beginning of the string.
     */
    public static final String DEFAULT_VALUE_ESCAPE = "\\";
}
