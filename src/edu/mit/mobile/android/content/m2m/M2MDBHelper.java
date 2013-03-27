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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import edu.mit.mobile.android.content.AndroidVersions;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.SQLGenUtils;
import edu.mit.mobile.android.content.dbhelper.ContentItemDBHelper;

/**
 * Database helper to make it easier to create many-to-many relationships between two arbitrary
 * tables.
 *
 * <pre>
 *     relation
 *        ↓
 * [from] → [to]
 *        → [to 2]
 * </pre>
 *
 * For example, you could have an Person who has a "friend" relationship to other Person objects.
 *
 * To use,
 *
 * @author steve
 *
 */
public class M2MDBHelper extends ContentItemDBHelper {
    private static final String WILDCARD_PATH_SEGMENT = "*";
    private final String mFromTable, mToTable, mJoinTable;
    private final Uri mToContentUri;

    private final IdenticalChildFinder mIdenticalChildFinder;
    private final String mToDefaultSortOrder;
    private final String mToTableEscaped;
    private final String mJoinTableEscaped;
    private final ContentItemDBHelper mTo;
    private final ContentItemDBHelper mFrom;

    private String mTableConstraint = null;

    private boolean mCreatedTables = false;

    public M2MDBHelper(ContentItemDBHelper from, ContentItemDBHelper to) {
        this(from, to, (Uri) null);
    }

    public M2MDBHelper(ContentItemDBHelper from, ContentItemDBHelper to, Uri toContentUri) {
        this(from, to, toContentUri, null);
    }

    public M2MDBHelper(ContentItemDBHelper from, ContentItemDBHelper to,
            IdenticalChildFinder identicalChildFinder) {
        this(from, to, null, identicalChildFinder);
    }

    public M2MDBHelper(ContentItemDBHelper from, ContentItemDBHelper to, Uri toContentUri,
            IdenticalChildFinder identicalChildFinder) {
        this(from.getTargetTable(), from, to, toContentUri, identicalChildFinder);
    }

    private M2MDBHelper(String fromTable, ContentItemDBHelper from, ContentItemDBHelper to,
            Uri toContentUri, IdenticalChildFinder identicalChildFinder) {
        super(to.getContentItem(false), to.getContentItem(true));

        mFromTable = fromTable;
        mToTable = to.getTargetTable();
        mTo = to;
        mFrom = from;
        mToTableEscaped = SQLGenUtils.escapeTableName(mToTable);
        mToDefaultSortOrder = getToDefaultSortOrder(to);
        mJoinTable = genJoinTableName(mFromTable, mToTable);
        mJoinTableEscaped = SQLGenUtils.escapeTableName(mJoinTable);

        mIdenticalChildFinder = identicalChildFinder;
        mToContentUri = toContentUri;
    }

    private String getToDefaultSortOrder(ContentItemDBHelper to) {

        String sortOrder = to.getDefaultSortOrder();

        if (sortOrder == null) {
            return null;
        }

        sortOrder = sortOrder.replaceAll("(?i)((?<!\\.)\\b\\w+\\s+(?:DESC|ASC))", mToTableEscaped
                + ".$1");

        return sortOrder;
    }

    @Override
    public String getDirType(String authority, String path) {
        return ProviderUtils.toDirType(authority, mToTable);
    }

    @Override
    public String getItemType(String authority, String path) {
        return ProviderUtils.toItemType(authority, mToTable);
    }

    private String genJoinTableName(String from, String to) {
        return from + "_" + to;
    }

    /**
     * @return the name of the join table
     */
    public String getJoinTableName() {
        return mJoinTable;
    }

    /**
     * @return the name of the from table
     */
    public String getFromTable() {
        return mFromTable;
    }

    /**
     * @return the name of the to table
     */
    public String getToTable() {
        return mToTable;
    }

    @Override
    public String getTargetTable() {
        return getToTable();
    }

    public void setTableConstraint(String tableConstraint) {
        mTableConstraint = tableConstraint;
    }

    /**
     * Generates a join table.
     */
    @Override
    public void createTables(SQLiteDatabase db) {
        // the instanceof below is a hack. This whole M2M thing needs to be reworked.
        if (mCreatedTables || mFrom instanceof M2MDBHelper) {
            return;
        }
        db.execSQL("CREATE TABLE "
                + mJoinTableEscaped
                + " ("
                + M2MColumns._ID
                + " INTEGER PRIMARY KEY,"
                + M2MColumns.TO_ID
                + " INTEGER"
                + (AndroidVersions.SQLITE_SUPPORTS_FOREIGN_KEYS ? " REFERENCES '" + mToTable
                        + "' (" + BaseColumns._ID + ")" + " ON DELETE CASCADE" : "")
                + ","
                + M2MColumns.FROM_ID
                + " INTEGER"
                + (AndroidVersions.SQLITE_SUPPORTS_FOREIGN_KEYS ? " REFERENCES '" + mFromTable
                        + "' (" + BaseColumns._ID + ")" + " ON DELETE CASCADE" : "")
                + (mTableConstraint != null ? "," + mTableConstraint : "") + ");");
        mCreatedTables = true;
    }

    /**
     * Deletes the join table.
     *
     */
    public void deleteJoinTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + mJoinTableEscaped);
    }

    /**
     * Creates a link from `from' to `to'.
     *
     * @param db
     *            database that has the many-to-many table
     * @param from
     *            ID of the item in the FROM table
     * @param to
     *            ID of the item in the TO table
     * @return ID of the newly created relation
     */
    public long addRelation(SQLiteDatabase db, long from, long to) {
        final ContentValues relation = new ContentValues();
        // make a many-to-many relation
        relation.put(M2MColumns.FROM_ID, from);
        relation.put(M2MColumns.TO_ID, to);
        return db.insert(mJoinTable, null, relation);
    }

    /**
     * Removes all relations from a given item.
     *
     * @param db
     * @param from
     * @return the count of deleted relations
     */
    public int removeRelation(SQLiteDatabase db, long from) {
        return db.delete(mJoinTable, M2MColumns.FROM_ID + "=?",
                new String[] { Long.toString(from) });
    }

    /**
     * Removes the relationship from `from' to `to'.
     *
     * @param db
     * @param from
     *            ID of the from item
     * @param to
     *            ID of the to item
     * @return the count of deleted relations
     */
    public int removeRelation(SQLiteDatabase db, long from, long to) {
        return db.delete(mJoinTable, M2MColumns.TO_ID + "=? AND " + M2MColumns.FROM_ID + "=?",
                new String[] { Long.toString(to), Long.toString(from) });
    }

    /**
     * <p>
     * Removes a relationship from `from' to any items matching the given selection. An inner select
     * is probably needed to do anything fancy:
     * </p>
     *
     * {@code
     * removeRelation(db, id, mTags.getJoinTableName() + "." + M2MColumns.TO_ID
     *                      + " IN (SELECT " + Tag._ID + " FROM " + tagTable + " WHERE " + tagTable
     * + "." + Tag.COL_NAME + "=?" + ")", new String[] tag });
     *
     * }
     *
     *
     * @param db
     * @param from
     * @param selection
     * @param selectionArgs
     * @return
     */
    public int removeRelation(SQLiteDatabase db, long from, String selection, String[] selectionArgs) {
        return db.delete(mJoinTable, ProviderUtils.addExtraWhere(selection, M2MColumns.FROM_ID
                + "=?"), ProviderUtils.addExtraWhereArgs(selectionArgs,
                new String[] { Long.toString(from) }));
    }

    @Override
    public Uri insertDir(SQLiteDatabase db, ContentProvider provider, Uri uri, ContentValues values) {

        return insertItemWithRelation(db, provider, uri, values);
    }

    /**
     * Inserts a child into the database and adds a relation to its parent. If the item described by
     * values is already present, only adds the relation.
     *
     * @param db
     * @param parentChildDir
     *            URI to insert into. This must be a be a hierarchical URI that points to the
     *            directory of the desired parent's children. Eg. "/itinerary/1/casts/"
     * @param values
     *            values for the child
     * @return the URI of the child that was either related or inserted.
     */
    public Uri insertItemWithRelation(SQLiteDatabase db, ContentProvider provider,
            Uri parentChildDir, ContentValues values) {
        final Uri parent = ProviderUtils.removeLastPathSegment(parentChildDir);

        final long parentId = ContentUris.parseId(parent);
        Uri newItem;

        db.beginTransaction();
        try {

            if (mIdenticalChildFinder != null) {
                newItem = mIdenticalChildFinder.getIdenticalChild(this, parentChildDir, db,
                        mToTable, values);
            } else {
                newItem = null;
            }

            long childId = -1;

            // existing item found, but no relation has been established yet.
            if (newItem != null) {
                childId = ContentUris.parseId(newItem);

                // no existing child or relation
            } else {
                if (mToContentUri != null) {
                    newItem = provider.insert(mToContentUri, values);
                    childId = ContentUris.parseId(newItem);
                } else {
                    if (mTo != null) {
                        newItem = mTo.insertDir(db, provider, parentChildDir, values);
                        childId = ContentUris.parseId(newItem);
                    } else {
                        childId = db.insert(mToTable, null, values);
                        if (childId != -1) {
                            newItem = ContentUris.withAppendedId(parentChildDir, childId);
                        }
                    }
                }
            }

            if (newItem != null && childId != -1) {
                addRelation(db, parentId, childId);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return newItem;
    }

    /**
     * Updates the item in the "to" table whose URI is specified.
     *
     * XXX Does not verify that there's actually a relationship between from and to.
     *
     * @param db
     * @param provider
     * @param uri
     *            the URI of the child. Child uri must end in its ID
     * @param values
     * @param where
     * @param whereArgs
     * @return the number of items that have been updated
     */
    @Override
    public int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {
        int count;
        if (mToContentUri != null) {
            count = provider.update(
                    ContentUris.withAppendedId(mToContentUri, ContentUris.parseId(uri)), values,
                    where, whereArgs);
        } else {
            count = db.update(mToTable, values,
                    ProviderUtils.addExtraWhere(where, BaseColumns._ID + "=?"),
                    ProviderUtils.addExtraWhereArgs(whereArgs, uri.getLastPathSegment()));
        }

        return count;
    }

    // TODO does not yet verify a relationship.
    @Override
    public int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {
        int count;
        if (mToContentUri != null) {
            count = provider.update(mToContentUri, values, where, whereArgs);
        } else {
            count = db.update(mToTable, values, where, whereArgs);
        }
        return count;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.mit.mobile.android.content.DBHelper#deleteItem(android.database.sqlite.SQLiteDatabase,
     * android.content.ContentProvider, android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int deleteItem(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        int count;
        try {
            db.beginTransaction();
            final long childId = ContentUris.parseId(uri);
            final Uri parent = ProviderUtils.removeLastPathSegments(uri, 2);

            final int rows = removeRelation(db, ContentUris.parseId(parent), childId);

            if (rows == 0) {
                throw new IllegalArgumentException("There is no relation between " + parent
                        + " and " + mToTable + ": ID " + childId);
            }

            if (mToContentUri != null) {
                count = provider.delete(ContentUris.withAppendedId(mToContentUri, childId), where,
                        whereArgs);
            } else {
                count = db.delete(mToTable,
                        ProviderUtils.addExtraWhere(where, BaseColumns._ID + "=?"),
                        ProviderUtils.addExtraWhereArgs(whereArgs, String.valueOf(childId)));
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
        return count;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.mit.mobile.android.content.DBHelper##deleteDir(android.database.sqlite.SQLiteDatabase,
     * android.content.ContentProvider, android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int deleteDir(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        int count;
        try {
            db.beginTransaction();
            final Uri parent = ProviderUtils.removeLastPathSegment(uri);

            // as this is m2m, we cannot delete the destination content so this is a bit different
            // than the insert (which is really just a shortcut)
            count = removeRelation(db, ContentUris.parseId(parent));

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
        return count;
    }

    /**
     * Selects rows from the TO table that have a relation from any of the items in the FROM table.
     * The ID of the item in the FROM table that matches can be selected using
     * {@link #getJoinTableName()}.{@link M2MColumns#FROM_ID}
     *
     * @param db
     *            DB that contains all the tables
     * @param toProjection
     *            projection for the TO table
     * @param selection
     *            any extra selection query or null
     * @param selectionArgs
     *            any extra selection arguments or null
     * @param sortOrder
     *            the desired sort order or null
     * @return a cursor whose content represents the to table
     */
    public Cursor queryTo(SQLiteDatabase db, String[] toProjection, String selection,
            String[] selectionArgs, String sortOrder) {
        // XXX hack to get around ambiguous column names. Is there a better way to write this query?
        if (selection != null) {
            // matches "foo=bar" but not "foo.baz=bar"; only qualifies unqualified column names
            selection = selection.replaceAll("((?<!\\.)\\b\\w+=\\?)", mToTableEscaped + ".$1");
        }

        return db.query(mToTableEscaped + " INNER JOIN " + mJoinTableEscaped + " ON "
                + mJoinTableEscaped + "." + M2MColumns.TO_ID + "=" + mToTableEscaped + "."
                + BaseColumns._ID, ProviderUtils.addPrefixToProjection(mToTable, toProjection),
                selection, selectionArgs, null, null, sortOrder != null ? sortOrder
                        : mToDefaultSortOrder);
    }

    /**
     * Selects rows from the TO table that have a relation from the given item in the FROM table.
     *
     * @param fromId
     *            _ID of the item on the FROM side of the relationship
     * @param db
     *            DB that contains all the tables
     * @param toProjection
     *            projection for the TO table
     * @param selection
     *            any extra selection query or null
     * @param selectionArgs
     *            any extra selection arguments or null
     * @param sortOrder
     *            the desired sort order or null
     * @return a cursor whose content represents the to table
     */
    public Cursor queryTo(long fromId, SQLiteDatabase db, String[] toProjection, String selection,
            String[] selectionArgs, String sortOrder) {
        return queryTo(
                db,
                toProjection,
                ProviderUtils.addExtraWhere(selection, mJoinTableEscaped + "." + M2MColumns.FROM_ID
                        + "=?"),
                ProviderUtils.addExtraWhereArgs(selectionArgs, Long.toString(fromId)), sortOrder);
    }

    @Override
    public Cursor queryDir(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        final Uri parent = ProviderUtils.removeLastPathSegment(uri);

        if (WILDCARD_PATH_SEGMENT.equals(parent.getLastPathSegment())) {
            return queryTo(db, projection, selection, selectionArgs, sortOrder);
        } else {
            final long parentId = ContentUris.parseId(parent);
            return queryTo(parentId, db, projection, selection, selectionArgs, sortOrder);
        }
    }

    @Override
    public Cursor queryItem(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        final Uri parent = ProviderUtils.removeLastPathSegments(uri, 2);

        final long parentId = ContentUris.parseId(parent);

        final String childId = uri.getLastPathSegment();

        return queryTo(parentId, db, projection,
                ProviderUtils.addExtraWhere(selection, BaseColumns._ID + "=?"),
                ProviderUtils.addExtraWhereArgs(selectionArgs, childId), sortOrder);
    }

    @Override
    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteJoinTable(db);
        createTables(db);

    }
}
