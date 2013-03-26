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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Interface to provide common shortcuts for interacting with {@link ContentItem}s that relate to
 * one another.
 *
 */
public interface Manager {

    /**
     * @param parent
     * @return the URI of the list of items under this parent
     */
    public abstract Uri getUri(Uri parent);

    /**
     * Adds an item to the destination
     *
     * @param cr
     * @param parent
     * @param cv
     * @return
     */
    public abstract Uri insert(ContentResolver cr, Uri parent, ContentValues cv);

    public abstract Cursor query(ContentResolver cr, Uri parent, String[] projection);

    /**
     * @return the sort order of the given {@link ContentItem}
     */
    public abstract String getSortOrder();

    /**
     * @return the path segment used in URIs for this {@link ContentItem}
     */
    public abstract String getPath();

}
