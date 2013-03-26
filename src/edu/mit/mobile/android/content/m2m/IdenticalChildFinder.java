package edu.mit.mobile.android.content.m2m;
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

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import edu.mit.mobile.android.content.DBHelper;

public interface IdenticalChildFinder {
    /**
     * Search the database and see if there is a child that is identical (using whatever criteria you prefer) to the one described in values.
     *
     * @param m2m the DBHelper for the parent/child relationship
     * @param parentChildDir the URI of the parent's children
     * @param db the database to do lookups on
     * @param childTable the child table to look into
     * @param values the values that describe the child in question.
     * @return if an identical child is found, returns its Uri. If none are found, returns null.
     */
    public Uri getIdenticalChild(DBHelper m2m, Uri parentChildDir, SQLiteDatabase db, String childTable, ContentValues values);
}
