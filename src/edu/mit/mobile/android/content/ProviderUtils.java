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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ProviderUtils {

    public static final String TYPE_DIR_PREFIX = "vnd.android.cursor.dir/vnd.",
            TYPE_ITEM_PREFIX = "vnd.android.cursor.item/vnd.";

    /**
     * Adds extra where clauses, encased in () and joined by AND.
     *
     * @param where
     * @param extraWhere
     * @return a query string with the extra clauses added in
     */
    public static String addExtraWhere(String where, String... extraWhere) {
        // shortcut a common case
        if (where == null && extraWhere.length == 1) {
            return extraWhere[0];
        }

        final String extraWhereJoined = "(" + TextUtils.join(") AND (", Arrays.asList(extraWhere))
                + ")";
        return extraWhereJoined
                + (where != null && where.length() > 0 ? " AND (" + where + ")" : "");
    }

    /**
     * Adds in extra arguments to a where query. You'll have to put in the appropriate query
     * placeholders.
     *
     * @param whereArgs
     *            the original whereArgs passed in from the query. Can be null.
     * @param extraArgs
     *            Extra arguments needed for the query.
     * @return a new String[] with the extra arguments added in
     */
    public static String[] addExtraWhereArgs(String[] whereArgs, String... extraArgs) {
        // shortcut a common case
        if (whereArgs == null) {
            return extraArgs;
        }

        final List<String> whereArgs2 = new ArrayList<String>();
        if (whereArgs != null) {
            whereArgs2.addAll(Arrays.asList(whereArgs));
        }
        whereArgs2.addAll(0, Arrays.asList(extraArgs));
        return whereArgs2.toArray(new String[whereArgs2.size()]);
    }

    /**
     * Remove the last path segment of a URI
     *
     * @param uri
     * @return the given uri with the last path segment removed
     */
    public static Uri removeLastPathSegment(Uri uri) {
        return ProviderUtils.removeLastPathSegments(uri, 1);
    }

    /**
     * Remove count path segments from the end of a URI
     *
     * @param uri
     * @param count
     * @return a new uri built off the supplied uri with the last count path segments removed
     */
    public static Uri removeLastPathSegments(Uri uri, int count) {
        final List<String> pathWithoutLast = new Vector<String>(uri.getPathSegments());
        for (int i = 0; i < count; i++) {
            pathWithoutLast.remove(pathWithoutLast.size() - 1);
        }
        final String parentPath = TextUtils.join("/", pathWithoutLast);
        return uri.buildUpon().path(parentPath).build();
    }

    /**
     * Modify the projection so that all columns refers to that of the specified table, not any
     * others that may be joined. Without this, _ID and other columns would be ambiguous and the
     * query fails.
     *
     * All columns are aliased as the column name in the original projection so that most queries
     * should Just Work™.
     *
     * @param tableName
     *            the name of the table whose columns should be returned.
     * @param projection
     * @return a modified projection with a table prefix for all columns or null if the projection
     *         is null
     */
    public static String[] addPrefixToProjection(String tableName, String[] projection) {
        if (projection == null) {
            return null;
        }
        final String[] projection2 = new String[projection.length];
        final int len = projection2.length;
        for (int i = 0; i < len; i++) {
            projection2[i] = SQLGenUtils.escapeTableName(tableName) + "." + projection[i] + " as "
                    + projection[i];
        }
        return projection2;
    }

    /**
     * Handly helper
     *
     * @param c
     * @param projection
     */
    public static void dumpCursorToLog(Cursor c, String[] projection) {
        final StringBuilder testOut = new StringBuilder();
        for (final String row : projection) {
            testOut.append(row);
            testOut.append("=");

            if (c.isNull(c.getColumnIndex(row))) {
                testOut.append("<<null>>");
            } else {
                testOut.append(c.getString(c.getColumnIndex(row)));

            }
            testOut.append("; ");
        }
        Log.d("CursorDump", testOut.toString());
    }

    /**
     * Removes key from the given ContentValues and returns it.
     *
     * @param cv
     *            the input ContentValues whose key will be removed
     * @param key
     * @return the value
     */
    public static Object extractContentValueItem(ContentValues cv, String key) {
        final Object val = cv.get(key);
        cv.remove(key);
        return val;
    }

    /**
     * @param authority
     * @param path
     * @return a standard content:// uri with the given authority and path
     */
    public static Uri toContentUri(String authority, String path) {
        return Uri.parse("content://" + authority + "/" + path);
    }

    /**
     * @param uri
     *            the uri whose path segment you wish to extract
     * @param nth
     *            number of path segments from the end. 0 is the last one, 1 is the second to last
     *            one, etc.
     * @return
     */
    public static String getNthPathFromEnd(Uri uri, int nth) {

        final List<String> path = uri.getPathSegments();
        final int size = path.size();
        final int pos = size - 1 - nth;
        if (pos < 0) {
            throw new IllegalArgumentException("there are not " + nth
                    + " path segments from the end of " + uri);
        }

        return path.get(pos);
    }

    private static final Pattern MIME_INVALID_CHARS = Pattern.compile("[^\\w!#$&.+^-]+");
    public static final String MIME_INVALID_CHAR_REPLACEMENT = ".";

    /**
     * <p>
     * Generates a complete MIME type string in the following format:
     * {@code vnd.android.cursor.dir/vnd.AUTHORITY.SUFFIX}
     * </p>
     *
     * <p>
     * SUFFIX is filtered so all invalid characters (see <a
     * href="http://tools.ietf.org/html/bcp13">BCP13</a>) are replaced with
     * {@link #MIME_INVALID_CHAR_REPLACEMENT}.
     * </p>
     *
     * @param authority
     *            the authority for this type
     * @param suffix
     *            a raw suffix
     * @return the MIME type for the given suffix
     */
    public static String toDirType(String authority, String suffix) {
        suffix = MIME_INVALID_CHARS.matcher(suffix).replaceAll(MIME_INVALID_CHAR_REPLACEMENT);
        return ProviderUtils.TYPE_DIR_PREFIX + authority + "." + suffix;
    }

    /**
     * <p>
     * Generates a complete MIME type string in the following format:
     * {@code vnd.android.cursor.item/vnd.AUTHORITY.SUFFIX}
     * </p>
     *
     *
     * <p>
     * SUFFIX is filtered so all invalid characters (see <a
     * href="http://tools.ietf.org/html/bcp13">BCP13</a>) are replaced with
     * {@link #MIME_INVALID_CHAR_REPLACEMENT}.
     * </p>
     *
     * @param authority
     *            the authority for this type
     * @param suffix
     *            a raw suffix
     * @return the MIME type for the given suffix
     */
    public static String toItemType(String authority, String suffix) {
        suffix = MIME_INVALID_CHARS.matcher(suffix).replaceAll(MIME_INVALID_CHAR_REPLACEMENT);
        return ProviderUtils.TYPE_ITEM_PREFIX + authority + "." + suffix;
    }
}
