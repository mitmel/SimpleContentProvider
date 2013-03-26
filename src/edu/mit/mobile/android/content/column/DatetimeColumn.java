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

import java.util.Date;

import android.database.Cursor;

/**
 * A timestamp based the number of milliseconds since the Unix epoch, stored as an integer.
 *
 * @author steve
 *
 */
public class DatetimeColumn extends DBColumnType<Date> {

    public static final int FLAG_AUTO_NOW = 0x1;

    // the formula below is from SQLite's manual
    private static final String NOW_IN_MILLISECONDS_RAW = "((julianday('now') - 2440587.5)*86400000)";
    public static final String NOW_IN_MILLISECONDS = DEFAULT_VALUE_ESCAPE + NOW_IN_MILLISECONDS_RAW;

    @Override
    public String toCreateColumn(String colName) {
        return toColumnDef(colName, "INTEGER");
    }

    @Override
    public String postTableSql(String table, String column, int flags) {
        if ((flags & FLAG_AUTO_NOW) != 0) {
            return "CREATE TRIGGER trigger_" + table + "_" + column + "_update AFTER UPDATE ON \""
                    + table + "\" FOR EACH ROW BEGIN UPDATE \"" + table + "\" SET \"" + column
                    + "\" = " + NOW_IN_MILLISECONDS_RAW + ";END";
        } else {
            return null;
        }
    }

    @Override
    public Date get(Cursor c, int colNumber) {
        return new Date(c.getLong(colNumber));
    }
}
