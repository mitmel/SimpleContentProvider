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
import android.database.Cursor;

public abstract class DBColumnType<T> {
	/**
	 *
	 * @param colName
	 * @return
	 */
	public abstract String toCreateColumn(String colName);

	public abstract T get(Cursor c, int colNumber);

	protected String toColumnDef(String colName, String type) {
		return "'" + colName + "' " + type;
	}

	/**
	 * Prefix the default value with this character in order to prevent
	 * auto-quoting. Prefix the default value with this character twice to
	 * insert it literally. This character only needs to be escaped if it's
	 * at the beginning of the string.
	 */
	public static final String DEFAULT_VALUE_ESCAPE = "\\";
}
