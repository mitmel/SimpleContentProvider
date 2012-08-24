package edu.mit.mobile.android.content;
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
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * This hook runs right before insert or update, allowing the data to be
 * modified before being saved to the database.
 *
 * @author Steve Pomeroy
 *
 */
public interface OnSaveListener {

    /**
     * This hook runs right before insert or update, allowing the data to be
     * modified before being saved to the database.
     *
     * @param db
     *
     * @param uri the uri of the item being updated or null if the item is
     * being inserted.
     *
     * @param cv the requested data to be updated or inserted. There is no
     * guarantee that any of the values will be present.
     *
     * @return the data that will be used for the save. This will usually be
     * the same as the passed-in cv.
     */
    public ContentValues onPreSave(SQLiteDatabase db, Uri uri, ContentValues cv); }
