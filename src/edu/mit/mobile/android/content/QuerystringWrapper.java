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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import edu.mit.mobile.android.content.query.QuerystringParser;
import edu.mit.mobile.android.content.query.QuerystringParser.ParseException;

/**
 * <p>
 * This class wraps another {@link DBHelper} and makes it searchable by passing it special URIs.
 * </p>
 *
 * <p>
 * This overrides the {@link #queryDir(SQLiteDatabase, Uri, String[], String, String[], String)
 * queryDir},
 * {@link #updateDir(SQLiteDatabase, ContentProvider, Uri, ContentValues, String, String[])
 * updateDir}, and {@link #deleteDir(SQLiteDatabase, ContentProvider, Uri, String, String[])
 * deleteDir} method in order to provide handling of select statement building using URI query
 * strings. To use, first construct a content uri for a content item's directory. For example
 * <kbd>content://org.example.test/message</kbd>. Then use the URI's query string (eg.
 * <kbd>content://org.example.test/message?QUERY</kbd>) to pass in the query parameters.
 * </p>
 *
 * <h3>Query Parameters</h3>
 * <p>
 * A query for this wrapper is different than standard URI query strings, although it may
 * superficially look similar or the same. The rules of the query are as follows:
 * </p>
 *
 * <dl>
 * <dt><kbd>=</kbd> exact match for value (SQL "IS")</dt>
 * <dd><kbd>column=value</kbd></dd>
 *
 * <dt><kbd>~=</kbd> inexact match for value (substring, case-insensitive; SQL "LIKE")</dt>
 * <dd><kbd>column~=value</kbd></dd>
 *
 * <dt><kbd>!=</kbd> exact non-match for value</dt>
 * <dd><kbd>column!=value</kbd></dd>
 *
 * <dt><kbd>!~=</kbd> inexact non-match</dt>
 * <dd><kbd>column!~=value</kbd></dd>
 *
 * <dt><kbd>&</kbd> joining parameters with AND</dt>
 * <dd><kbd>column1=value1&column2!=value2</kbd></dd>
 *
 * <dt><kbd>|</kbd> joining parameters with OR</dt>
 * <dd><kbd>column1=value1|column2~=value2</kbd></dd>
 *
 * <dt><kbd>()</kbd> grouping parameters</dt>
 * <dd><kbd>(column1=value1|column1=value2)&column2=value3</kbd></dd>
 * </dl>
 *
 * <p>
 * These rules can be combined together to make arbitrarily-complex queries which map directly to
 * SQL queries. They should be passed to the URI unescaped (that is, not percent-encoded). For
 * example, <kbd>content://org.example.test/message?(title~=robot|title~=kitten)&verb=find</kbd>
 * would match a message whose title contains either "robot" or "kitten" and whose verb is "find".
 * </p>
 *
 * <p>
 * Column names are validity-checked and values are passed in by reference in order to avoid SQL
 * injections.
 * </p>
 *
 * <p>
 * Note: the use of the "|" character in the query is technically illegal according to RFC3986,
 * however the Android Uri class doesn't seem to mind.
 * </p>
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 */
// TODO provide ability to limit columns that can be queried.
public class QuerystringWrapper extends DBHelper implements ContentItemRegisterable {
    public static final String TAG = QuerystringWrapper.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static final String QUERY_OPERATOR_OR = "|";

    public static final String QUERY_OPERATOR_AND = "&";

    public static final String QUERY_OPERATOR_EQUALS = "=";

    public static final String QUERY_OPERATOR_NOT_EQUALS = "!=";

    public static final String QUERY_OPERATOR_LIKE = "~=";

    public static final String QUERY_OPERATOR_NOT_LIKE = "!~=";

    public static final String QUERY_OPERATOR_GREATER_THAN = ">";

    public static final String QUERY_OPERATOR_GREATER_THAN_EQUALS = ">=";

    public static final String QUERY_OPERATOR_LESS_THAN = "<";

    public static final String QUERY_OPERATOR_LESS_THAN_EQUALS = "<=";

    private final HashMap<String, String> mColumnAliases = new HashMap<String, String>();

    private final DBHelper mWrappedHelper;

    public QuerystringWrapper(DBHelper wrappedHelper) {
        mWrappedHelper = wrappedHelper;
    }

    private static class QueryStringResult {
        public QueryStringResult(String selection, String[] selectionArgs) {
            this.selection = selection;
            this.selectionArgs = selectionArgs;
        }

        /**
         * The new, composite selection string.
         */
        final String selection;
        /**
         * The new, composite selection arguments.
         */
        final String[] selectionArgs;
    }

    /**
     * Performs the query string extraction.
     *
     * @param uri
     *            the full URI, including the query string
     * @param selection
     *            the selection string passed in by the
     *            {@link DBHelper#queryDir(SQLiteDatabase, Uri, String[], String, String[], String)
     *            queryDir}, etc. method. Null is ok.
     * @param selectionArgs
     *            the arguments accompanying the aforementioned selection string. Null is ok.
     * @return the query string translated and integrated into the selection and selectionArgs that
     *         were passed in
     * @throws IllegalArgumentException
     *             if there are any errors parsing the query
     */
    public QueryStringResult queryStringToSelection(Uri uri, String selection,
            String[] selectionArgs) throws SQLGenerationException {
        final String query = uri.getEncodedQuery();

        String newSelection = selection;
        String[] newSelectionArgs = selectionArgs;
        if (DEBUG) {
            Log.d(TAG, "query uri " + uri);
        }
        try {
            if (query != null) {

                final QuerystringParser parser = new QuerystringParser(query, mColumnAliases);

                parser.parse();

                newSelection = ProviderUtils.addExtraWhere(selection, parser.getResult());
                newSelectionArgs = ProviderUtils.addExtraWhereArgs(selectionArgs,
                        parser.getSelectionArgs());
                if (DEBUG) {
                    Log.d(TAG,
                            "query:" + newSelection + "; args: ["
                                    + TextUtils.join(",", Arrays.asList(newSelectionArgs)) + "]");
                }
            }
        } catch (final ParseException e) {
            final IllegalArgumentException e2 = new IllegalArgumentException("parse error");
            e2.initCause(e);
            throw e2;

        } catch (final IOException e) {
            final IllegalArgumentException se = new IllegalArgumentException(
                    "could not understand query string");
            se.initCause(e);
            throw se;
        }
        return new QueryStringResult(newSelection, newSelectionArgs);
    }

    /**
     * Adds an alias for the input key in the query string and outputs the specified column instead.
     *
     * @param columnAlias
     *            the input name. This is the key from the query string.
     * @param column
     *            a non-escaped column name. Can be qualified with a table name.
     */
    public void addColumnAlias(String columnAlias, String column) {
        mColumnAliases.put(columnAlias, column);
    }

    @Override
    public Cursor queryDir(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        final QueryStringResult qr = queryStringToSelection(uri, selection, selectionArgs);

        return mWrappedHelper.queryDir(db, uri, projection, qr.selection, qr.selectionArgs,
                sortOrder);
    }

    @Override
    public Cursor queryItem(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return mWrappedHelper.queryItem(db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Uri insertDir(SQLiteDatabase db, ContentProvider provider, Uri uri, ContentValues values)
            throws SQLException {

        return mWrappedHelper.insertDir(db, provider, uri, values);
    }

    @Override
    public int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {
        return mWrappedHelper.updateItem(db, provider, uri, values, where, whereArgs);
    }

    @Override
    public int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
            ContentValues values, String where, String[] whereArgs) {
        final QueryStringResult qr = queryStringToSelection(uri, where, whereArgs);

        return mWrappedHelper.updateDir(db, provider, uri, values, qr.selection, qr.selectionArgs);
    }

    @Override
    public int deleteItem(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        return mWrappedHelper.deleteItem(db, provider, uri, where, whereArgs);
    }

    @Override
    public int deleteDir(SQLiteDatabase db, ContentProvider provider, Uri uri, String where,
            String[] whereArgs) {
        final QueryStringResult qr = queryStringToSelection(uri, where, whereArgs);

        return mWrappedHelper.deleteDir(db, provider, uri, qr.selection, qr.selectionArgs);
    }

    @Override
    public void createTables(SQLiteDatabase db) throws SQLGenerationException {
        mWrappedHelper.createTables(db);
    }

    @Override
    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion)
            throws SQLGenerationException {
        mWrappedHelper.upgradeTables(db, oldVersion, newVersion);
    }

    @Override
    public String getTargetTable() {
        return mWrappedHelper.getTargetTable();
    }

    @Override
    public void setOnSaveListener(OnSaveListener onSaveListener) {
        mWrappedHelper.setOnSaveListener(onSaveListener);
    }

    @Override
    public void removeOnSaveListener() {
        mWrappedHelper.removeOnSaveListener();
    }

    @Override
    public Class<? extends ContentItem> getContentItem(boolean isItem) {
        if (mWrappedHelper instanceof ContentItemRegisterable) {
            return ((ContentItemRegisterable) mWrappedHelper).getContentItem(isItem);
        } else {
            throw new IllegalArgumentException(
                    "wrapped content item does not implement ContentItemRegisterable");
        }
    }

    @Override
    public String getDirType(String authority, String path) {
        return mWrappedHelper.getDirType(authority, path);
    }

    @Override
    public String getItemType(String authority, String path) {
        return mWrappedHelper.getItemType(authority, path);
    }
}
