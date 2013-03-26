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

/**
 * If a {@link DBHelper} has a specific {@link ContentItem} that's associated with the given data,
 * it should implement this interface and return that ContentItem. This can later be retrieved from
 * {@link SimpleContentProvider#getContentItem(android.net.Uri)}.
 *
 */
public interface ContentItemRegisterable {

    /**
     * Implement this to return the {@link ContentItem} associated with the implementing class.
     *
     * @param isItem
     *            true if the registration is being done for an item. False if it's a directory.
     * @return the class of the {@link ContentItem}
     */
    public Class<? extends ContentItem> getContentItem(boolean isItem);
}
