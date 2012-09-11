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
