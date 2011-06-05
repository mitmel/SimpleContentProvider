package edu.mit.mobile.android.content;
/*
 * Copyright (C) 2010-2011 MIT Mobile Experience Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import edu.mit.mobile.android.utils.ListUtils;

public class ProviderUtils {


	public static final String
		TYPE_DIR_PREFIX = "vnd.android.cursor.dir/vnd.",
		TYPE_ITEM_PREFIX = "vnd.android.cursor.item/vnd.";

    /**
     * Adds extra where clauses
     * @param where
     * @param extraWhere
     * @return
     */
    public static String addExtraWhere(String where, String ... extraWhere){
            final String extraWhereJoined = "(" + ListUtils.join(Arrays.asList(extraWhere), ") AND (") + ")";
            return extraWhereJoined + (where != null && where.length() > 0 ? " AND ("+where+")":"");
    }

    /**
     * Adds in extra arguments to a where query. You'll have to put in the appropriate
     * @param whereArgs the original whereArgs passed in from the query. Can be null.
     * @param extraArgs Extra arguments needed for the query.
     * @return
     */
    public static String[] addExtraWhereArgs(String[] whereArgs, String...extraArgs){
            final List<String> whereArgs2 = new ArrayList<String>();
            if (whereArgs != null){
                    whereArgs2.addAll(Arrays.asList(whereArgs));
            }
            whereArgs2.addAll(0, Arrays.asList(extraArgs));
            return whereArgs2.toArray(new String[]{});
    }

	/**
	 * Remove the last path segment of a URI
	 * @param uri
	 * @return
	 */
	public static Uri removeLastPathSegment(Uri uri){
		return ProviderUtils.removeLastPathSegments(uri, 1);
	}

	/**
	 * Remove count path segments from the end of a URI
	 * @param uri
	 * @param count
	 * @return
	 */
	public static Uri removeLastPathSegments(Uri uri, int count){
		final List<String> pathWithoutLast = new Vector<String>(uri.getPathSegments());
		for (int i = 0; i < count; i++){
			pathWithoutLast.remove(pathWithoutLast.size() - 1);
		}
		final String parentPath = ListUtils.join(pathWithoutLast, "/");
		return uri.buildUpon().path(parentPath).build();
	}

	/**
	 * Modify the projection so that all columns refers to that of the specified table,
	 * not any others that may be joined. Without this, _ID and other columns would be ambiguous
	 * and the query fails.
	 *
	 * All columns are aliased as the column name in the original projection so that most queries
	 * should Just Work™.
	 *
	 * @param tableName the name of the table whose columns should be returned.
	 * @param projection
	 * @return a modified projection with a table prefix for all columns.
	 */
	public static String[] addPrefixToProjection(String tableName, String[] projection){
		if (projection == null){
			return null;
		}
		final String[] projection2 = new String[projection.length];
		final int len = projection2.length;
		for (int i = 0; i < len; i++){
			projection2[i] = tableName + "."+projection[i] + " as " + projection[i];
		}
		return projection2;
	}

	/**
	 * Handly helper
	 * @param c
	 * @param projection
	 */
	public static void dumpCursorToLog(Cursor c, String[] projection){
		final StringBuilder testOut = new StringBuilder();
		for (final String row: projection){
			testOut.append(row);
			testOut.append("=");

			if (c.isNull(c.getColumnIndex(row))){
				testOut.append("<<null>>");
			}else{
				testOut.append(c.getString(c.getColumnIndex(row)));

			}
			testOut.append("; ");
		}
		Log.d("CursorDump", testOut.toString());
	}

	/**
	 * Removes key from the given ContentValues and returns it in a new container.
	 *
	 * @param cv
	 * @param key
	 * @return
	 */
	public static ContentValues extractContentValueItem(ContentValues cv, String key){
		final String val = cv.getAsString(key);
		cv.remove(key);
		final ContentValues cvNew = new ContentValues();
		cvNew.put(key, val);
		return cvNew;
	}
}
