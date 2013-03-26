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

/**
 * A simple boolean value. Internally, this is stored as an integer where 0 is false and 1 is true.
 *
 */
public class BooleanColumn extends DBColumnType<Boolean> {

    @Override
    public String toCreateColumn(String colName) {
        return toColumnDef(colName, "BOOLEAN");
    }

    @Override
    public Boolean get(Cursor c, int colNumber) {

        return c.getInt(colNumber) != 0;
    }
}
