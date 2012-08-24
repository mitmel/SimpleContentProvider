package edu.mit.mobile.android.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A helpful class that can make using tables with foreign key relations easier.
 *
 * To use, simply add a static instance of this in your {@link ContentItem} class.
 *
 * @author steve
 *
 */
public class ForeignKeyManager implements Manager {
    private final Class<? extends ContentItem> mChild;
    private final String mPath;
    private final String mSortOrder;

    /**
     *
     * @param child
     *            the child class that has this class as a foreign key
     * @param childRelationshipPath
     *            the path that is used in the URI to refer to this relationship
     */
    public ForeignKeyManager(Class<? extends ContentItem> child, String childRelationshipPath){
        mChild = child;
        mPath = childRelationshipPath;
        final DBSortOrder sortOrder = mChild.getAnnotation(DBSortOrder.class);
        mSortOrder = sortOrder != null ? sortOrder.value() : null;
    }

    /**
     * Unlike {@link #ForeignKeyManager(Class, String)}, the path is extracted from the
     * {@link UriPath} annotation on the child class.
     *
     * @param child
     *            the child class that has this class as a foreign key
     */
    public ForeignKeyManager(Class<? extends ContentItem> child) {
        mChild = child;

        mPath = UriPath.Extractor.extractUriPath(child, true);

        final DBSortOrder sortOrder = mChild.getAnnotation(DBSortOrder.class);
        mSortOrder = sortOrder != null ? sortOrder.value() : null;
    }

    public Uri getUri(Uri parent) {
        return Uri.withAppendedPath(parent, mPath);
    }

    /**
     * Retrieves the Uri of the specific child using this parent / relationship.
     *
     * @param parent
     *            uri of the parent item
     * @param childId
     *            the {@link BaseColumns#_ID} of the child
     * @return
     */
    public Uri getUri(Uri parent, long childId) {
        return ContentUris.withAppendedId(getUri(parent), childId);
    }

    public Uri insert(ContentResolver cr, Uri parent, ContentValues cv) {
        return cr.insert(getUri(parent), cv);
    }

    public Cursor query(ContentResolver cr, Uri parent, String[] projection) {
        return cr.query(getUri(parent), projection, null, null, mSortOrder);
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public String getPath() {
        return mPath;
    }
}
