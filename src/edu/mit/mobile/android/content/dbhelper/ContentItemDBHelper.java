package edu.mit.mobile.android.content.dbhelper;

/*
 * Copyright (C) 2013 MIT Mobile Experience Lab
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
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.ContentItemRegisterable;
import edu.mit.mobile.android.content.DBHelper;
import edu.mit.mobile.android.content.DBSortOrder;

/**
 * This class adds in registration of {@link ContentItem}s, implementing the
 * {@link ContentItemRegisterable} interface. This also extracts the {@link DBSortOrder} annotation
 * from the ContentItem, making it available with {@link #getDefaultSortOrder()}. If your DBHelper
 * is based on ContentItems, it should be built off this class.
 * 
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 * 
 */
public abstract class ContentItemDBHelper extends DBHelper implements ContentItemRegisterable {

    protected final Class<? extends ContentItem> mContentDir;
    protected final Class<? extends ContentItem> mContentItem;

    protected final String mSortOrder;

    /**
     * @param contentItem
     *            the ContentItem that should be used for all queries passed to this DBHelper.
     */
    public ContentItemDBHelper(Class<? extends ContentItem> contentItem) {
        mContentDir = contentItem;
        mContentItem = contentItem;
        mSortOrder = extractSortOrder();
    }

    /**
     * @param contentDir
     *            the ContentItem which should be used for dir queries
     * @param contentItem
     *            the ContentItem which should be used for item queries
     */
    public ContentItemDBHelper(Class<? extends ContentItem> contentDir,
            Class<? extends ContentItem> contentItem) {
        mContentItem = contentItem;
        mContentDir = contentDir;
        mSortOrder = extractSortOrder();
    }

    private String extractSortOrder() {
        final DBSortOrder sortOrder = mContentItem.getAnnotation(DBSortOrder.class);
        return sortOrder != null ? sortOrder.value() : null;
    }

    /**
     * Gets the sort order that was specified by the @{@link DBSortOrder} annotation.
     *
     * @return the default sort order on null if there was none specified
     */
    public String getDefaultSortOrder() {
        return mSortOrder;
    }

    @Override
    public Class<? extends ContentItem> getContentItem(boolean isItem) {
        return isItem ? mContentItem : mContentDir;
    }
}
