package edu.mit.mobile.android.content.dbhelper;
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

import java.util.ArrayList;
import java.util.LinkedList;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import edu.mit.mobile.android.content.BuildConfig;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.DBHelper;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.SQLGenerationException;

/**
 * <p>
 * An implementation of the {@link SearchManager} interface. This handles queries made to the search
 * URI, searching one or more tables and one or more columns within that table. Results contain
 * appropriate data URIs to link back to the application.
 * </p>
 *
 * <h2>To Use</h2>
 *
 * <p>
 * Create one or more {@link GenericDBHelper}s that you wish to search. For example, a blog post:
 * </p>
 *
 * <code><pre>
 * final GenericDBHelper blogPosts = new GenericDBHelper(BlogPost.class);
 *
 * </pre></code>
 * <p>
 * ...and comments on those posts:
 * </p>
 * <code><pre>
 *
 * final ForeignKeyDBHelper comments = new ForeignKeyDBHelper(BlogPost.class, Comment.class,
 *         Comment.POST);
 * </pre></code>
 * <p>
 * Add in the search interface:
 * </p>
 * <code><pre>
 * final SearchDBHelper searchHelper = new SearchDBHelper();
 * </pre></code>
 * <p>
 * And register your helpers with the search helper. The columns specified below indicate which
 * columns will be returned (in this case, the title and the body) in the search results and which
 * columns will be searched (the body and the title).
 * </p>
 * <code><pre>
 * searchHelper.registerDBHelper(blogPosts, BlogPost.CONTENT_URI, BlogPost.TITLE, BlogPost.BODY,
 *         BlogPost.BODY, BlogPost.TITLE);
 *
 * searchHelper.registerDBHelper(comments, Comment.ALL_COMMENTS, Comment.BODY, null, Comment.BODY);
 * </pre></code>
 * <p>
 * Define these as constants to make them easier to use in other contexts
 * </p>
 * <code><pre>
 * public static final String SEARCH_PATH = &quot;search&quot;;
 * public static final Uri SEARCH = ProviderUtils.toContentUri(AUTHORITY, SEARCH_PATH);
 * </pre></code>
 * <p>
 * This hooks in the search helper at the given path. In this case, the path will be
 * <code>content://vnd.android..../search</code>
 * </p>
 * <code><pre>
 * addSearchUri(searchHelper, SEARCH_PATH);
 * </pre></code>
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
public class SearchDBHelper extends DBHelper {

    private static final String TAG = SearchDBHelper.class.getSimpleName();
    LinkedList<RegisteredHelper> mRegisteredHelpers = new LinkedList<SearchDBHelper.RegisteredHelper>();

    public SearchDBHelper() {

    }

    /**
     * <p>
     * Adds a {@link GenericDBHelper} to the list of search helpers that will be queried for this
     * search. All the registered DBHelpers will be searched and results will be mixed together.
     * </p>
     *
     * <p>
     * The columns to search, provided in {@code searchColumns}, will be queried using a simple
     * {@code LIKE "%query%"} substring search. The results are concatenate using {@code UNION ALL}.
     * </p>
     *
     * @param helper
     *            the helper you wish to search. This must return a valid result for
     *            {@link GenericDBHelper#getTable()}.
     * @param contentUri
     *            the base URI that will be used when linking back to the item from the search
     *            results (see {@link SearchManager#SUGGEST_COLUMN_INTENT_DATA}). This can be null
     *            to disable this feature.
     * @param text1Column
     *            the text column that will be used for {@link SearchManager#SUGGEST_COLUMN_TEXT_1}.
     *            This is required.
     * @param text2Column
     *            the text column that will be used for {@link SearchManager#SUGGEST_COLUMN_TEXT_2}.
     *            This is optional and can be null.
     * @param searchColumns
     *            a list of the columns that will be searched for the given keyword.
     */
    public void registerDBHelper(GenericDBHelper helper, Uri contentUri, String text1Column,
            String text2Column, String... searchColumns) {
        mRegisteredHelpers.add(new RegisteredHelper(helper, contentUri, text1Column, text2Column,
                searchColumns));
    }

    private static class RegisteredHelper {
        public final GenericDBHelper mHelper;
        public final String[] mColumns;
        public final String mText2Column;
        public final String mText1Column;
        public final Uri mContentUri;

        public RegisteredHelper(GenericDBHelper helper, Uri contentUri, String text1Column,
                String text2Column, String... columns) {
            mHelper = helper;
            mColumns = columns;
            mContentUri = contentUri;
            mText1Column = text1Column;
            mText2Column = text2Column;
        }
    }

    @Override
    public Uri insertDir(SQLiteDatabase db, ContentProvider provider, Uri uri, ContentValues values)
            throws SQLException {
        throw new UnsupportedOperationException("insert not supported for this helper");
    }

    @Override
    public int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {
        throw new UnsupportedOperationException("update not supported for this helper");
    }

    @Override
    public int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {
        throw new UnsupportedOperationException("update not supported for this helper");
    }

    @Override
    public int deleteItem(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        throw new UnsupportedOperationException("delete not supported for this helper");
    }

    @Override
    public int deleteDir(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        throw new UnsupportedOperationException("delete not supported for this helper");
    }

    @Override
    public Cursor queryDir(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return search(db, uri, projection, selection, selectionArgs, sortOrder, true);
    }

    @Override
    public Cursor queryItem(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return search(db, uri, projection, selection, selectionArgs, sortOrder, false);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    private Cursor search(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder, boolean isDir) {

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "search(" + uri + ")");
        }
        final String searchQuery = isDir ? null : "%" + uri.getLastPathSegment() + "%";

        String limit = uri.getQueryParameter("limit");

        // only allow a limit string that's an integer
        try {
            Integer.valueOf(limit);
        } catch (final NumberFormatException e) {
            limit = null;
        }

        final StringBuilder multiSelect = new StringBuilder();

        multiSelect.append('(');

        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        boolean addUnion = false;
        for (final RegisteredHelper searchReg : mRegisteredHelpers) {

            // UNION ALL concatenates the inner queries
            if (addUnion) {
                multiSelect.append(" UNION ALL ");
            }
            addUnion = true;

            String searchSelection = null;

            if (!isDir) {
                final StringBuilder extSel = new StringBuilder();
                // build the selection that matches the search string in the given
                int i = 0;
                for (final String column : searchReg.mColumns) {
                    if (i > 0) {
                        extSel.append(" OR ");
                    }

                    extSel.append("\"");
                    extSel.append(column);
                    extSel.append("\" LIKE ?1");

                    i++;
                }
                searchSelection = extSel.toString();
            }

            final ArrayList<String> extProj = new ArrayList<String>();
            final String table = searchReg.mHelper.getTable();
            final String tablePrefix = '"' + table + "\".";

            extProj.add(tablePrefix + ContentItem._ID + " AS " + ContentItem._ID);

            extProj.add(tablePrefix + searchReg.mText1Column + " AS "
                    + SearchManager.SUGGEST_COLUMN_TEXT_1);

            if (searchReg.mText2Column != null) {
                extProj.add(tablePrefix + searchReg.mText2Column + "  AS "
                        + SearchManager.SUGGEST_COLUMN_TEXT_2);
            } else {
                // this is needed as sqlite3 crashes otherwise.
                extProj.add("'' AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);
            }

            if (searchReg.mContentUri != null) {
                extProj.add("'" + searchReg.mContentUri.toString() + "' AS "
                        + SearchManager.SUGGEST_COLUMN_INTENT_DATA);
                extProj.add(tablePrefix + ContentItem._ID + " AS "
                        + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
            } else {
                // this is needed as sqlite3 crashes otherwise.
                extProj.add("'' AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA);
                extProj.add("'' AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
            }

            qb.setTables(table);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                multiSelect.append(qb.buildQuery(
                        extProj.toArray(new String[extProj.size()]),
                        searchSelection != null ? ProviderUtils.addExtraWhere(selection,
                                searchSelection) : selection, null, null, sortOrder, null));
            } else {
                multiSelect.append(qb.buildQuery(
                        extProj.toArray(new String[extProj.size()]),
                        searchSelection != null ? ProviderUtils.addExtraWhere(selection,
                                searchSelection) : selection, null, null, null, sortOrder, null));
            }

        } // inner selects

        multiSelect.append(')');

        final Cursor c = db.query(multiSelect.toString(), null, null,
                searchQuery != null ? new String[] { searchQuery } : null, null, null, sortOrder,
                limit);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "search found " + c.getCount() + " results");
        }
        return c;
    }

    @Override
    public String getDirType(String authority, String path) {
        return SearchManager.SUGGEST_MIME_TYPE;
    }

    @Override
    public String getItemType(String authority, String path) {
        return SearchManager.SUGGEST_MIME_TYPE;
    }

    @Override
    public void createTables(SQLiteDatabase db) throws SQLGenerationException {
        // unused
    }

    @Override
    public String getTargetTable() {
        return null;
    }

    @Override
    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion)
            throws SQLGenerationException {
        // unused
    }

}
