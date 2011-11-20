package edu.mit.mobile.android.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import edu.mit.mobile.android.content.column.DBColumn;

/**
 * Database helper to make it easier to access foreign key relationships between
 * a parent and a child with a foreign key pointing to that parent.
 *
 * <pre>
 *      relation
 *          ↓
 * [parent] → [child]
 *          → [child 2]
 * </pre>
 *
 * For example, you could have an BlogPost that has a relation to multiple
 * Comments.
 *
 * Unfortunately, if your version of SQLite doesn't support foreign keys (see {@link AndroidVersions}),
 * this will not automatically cascade deletes for you or verify any relationships.
 * It will otherwise function, though, just with
 *
 * @author steve
 *
 */
public class ForeignKeyDBHelper extends GenericDBHelper {
	private final String mChildTable, mParentTable;
	private final String mColumn;

	public ForeignKeyDBHelper(Class<? extends ContentItem> parent,
			Class<? extends ContentItem> child, String column) {
		super(child);
		mColumn = column;

		mChildTable = getTable();
		mParentTable = DBColumn.Extractor.extractTableName(parent);

	}

	@Override
	public Uri insertDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values) throws SQLException {
		final long parentId = Long.valueOf(ProviderUtils.getNthPathFromEnd(uri,
				1));
		values.put(mColumn, parentId);
		return super.insertDir(db, provider, uri, values);
	}

	@Override
	public int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values, String where, String[] whereArgs) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 2);

		return super.updateItem(db, provider, uri, values,
				ProviderUtils.addExtraWhere(where, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, parentId));
	}

	@Override
	public int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values, String where, String[] whereArgs) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 1);

		return super.updateDir(db, provider, uri, values,
				ProviderUtils.addExtraWhere(where, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, parentId));
	}

	@Override
	public int deleteItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
			String where, String[] whereArgs) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 2);

		return super.deleteItem(db, provider, uri,
				ProviderUtils.addExtraWhere(where, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, parentId));
	}

	@Override
	public int deleteDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			String where, String[] whereArgs) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 1);

		return super.deleteDir(db, provider, uri,
				ProviderUtils.addExtraWhere(where, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, parentId));
	}

	@Override
	public Cursor queryDir(SQLiteDatabase db, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 1);

		return super.queryDir(db, uri, projection,
				ProviderUtils.addExtraWhere(selection, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(selectionArgs, parentId),
				sortOrder);
	}

	@Override
	public Cursor queryItem(SQLiteDatabase db, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 2);

		return super.queryItem(db, uri, projection,
				ProviderUtils.addExtraWhere(selection, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(selectionArgs, parentId),
				sortOrder);
	}
}