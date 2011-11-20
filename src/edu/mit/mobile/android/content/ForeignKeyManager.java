package edu.mit.mobile.android.content;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * A helpful class that can make using tables with foreign key relations easier.
 *
 * To use, simply add a static instance of this in your {@link ContentItem} class.
 *
 * @author steve
 *
 */
public class ForeignKeyManager {
	private final Class<? extends ContentItem> mChild;
	private final String mPath;
	private final String mSortOrder;

	public ForeignKeyManager(Class<? extends ContentItem> child) {
		mChild = child;
		final UriPath path = mChild.getAnnotation(UriPath.class);
		mPath = path != null ? path.value() : null;
		final DBSortOrder sortOrder = mChild.getAnnotation(DBSortOrder.class);
		mSortOrder = sortOrder != null ? sortOrder.value() : null;
	}

	public Uri getUri(Uri parent){
		return Uri.withAppendedPath(parent, mPath);
	}

	public Uri getUri(Uri parent, long childId){
		return ContentUris.withAppendedId(getUri(parent), childId);
	}

	public Uri insert(ContentResolver cr, Uri parent, ContentValues cv){
		return cr.insert(getUri(parent), cv);
	}

	public Cursor query(ContentResolver cr, Uri parent, String[] projection){
		return cr.query(getUri(parent), projection, null, null, mSortOrder);
	}

	public String getSortOrder(){
		return mSortOrder;
	}

	public String getPath(){
		return mPath;
	}
}
