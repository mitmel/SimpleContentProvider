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

public class FloatColumn extends DBColumnType<java.lang.Float> {

    @Override
    public String toCreateColumn(String colName) {
        return toColumnDef(colName, "FLOAT");
    }

    @Override
    public java.lang.Float get(Cursor c, int colNumber) {

        return java.lang.Float.valueOf(c.getFloat(colNumber));
    }

}
