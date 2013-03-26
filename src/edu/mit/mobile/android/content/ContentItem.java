package edu.mit.mobile.android.content;
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

import android.provider.BaseColumns;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.IntegerColumn;

/**
 * A simple extension of {@link BaseColumns} that tags {@link #_ID} as an integer primary key.
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
public interface ContentItem extends BaseColumns {

    @DBColumn(type = IntegerColumn.class, primaryKey = true, autoIncrement = true)
    public static final String _ID = BaseColumns._ID;

}
