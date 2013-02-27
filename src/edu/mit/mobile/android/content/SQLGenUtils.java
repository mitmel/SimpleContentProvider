package edu.mit.mobile.android.content;

/*
 * Copyright (C) 2011 MIT Mobile Experience Lab
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper functions for SQL generation.
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
public class SQLGenUtils {

    private static final String VALID_NAME_STR = "([A-Za-z0-9_]+)";
    // this pattern defines what a valid name (table name, column name, etc.) is in SQLite.
    private static final Pattern VALID_NAME = Pattern.compile(VALID_NAME_STR);
    // the inverse of the above pattern.
    private static final Pattern NON_NAME_CHARS = Pattern.compile("[^A-Za-z0-9_]+");

    private static final Pattern VALID_QUALIFIED_COLUMN_NAME = Pattern.compile("(?:"
            + VALID_NAME_STR + "\\.)?" + VALID_NAME_STR);

    /**
     * Creates a valid SQLite name from the Java classname, lowercased.
     *
     * @param myClass
     * @return a valid SQL name
     */
    public static final String toValidName(Class<? extends Object> myClass) {
        return toValidName(myClass.getSimpleName().toLowerCase(Locale.US));
    }

    /**
     * Removes any non-name characters from the given name.
     *
     * @param name
     * @return a valid SQL name
     */
    public static final String toValidName(String name) {
        // strip out any non-name characters from the name.
        final Matcher m = NON_NAME_CHARS.matcher(name);
        name = m.replaceAll("");

        return name;
    }

    /**
     * @param name
     * @return true if the name is a valid SQLite name.
     */
    public static boolean isValidName(String name) {
        return VALID_NAME.matcher(name).matches();
    }

    /**
     * Unlike {@link #isValidName(String)}, this permits table prefixing of the supplied name. Eg.
     * {@code foo.bar}, and {@code bar} are both valid.
     *
     * @param column
     * @return
     */
    public static boolean isValidQualifiedColumnName(String column) {
        return VALID_QUALIFIED_COLUMN_NAME.matcher(column).matches();
    }

    /**
     * Escapes table names so they can be used in SQL queries.
     *
     * @param tableName
     *            a plain table name
     * @return a quoted, escaped table name
     * @see <a
     *      href="http://stackoverflow.com/a/6701665/90934">http://stackoverflow.com/a/6701665/90934</a>
     */
    public static String escapeTableName(String tableName) {
        return '"' + tableName.replaceAll("\"", "\"\"") + '"';

    }

    /**
     * Escapes a qualified column name, eg. {@code foo.bar} or {@code bar}.
     *
     * @param column
     * @return
     * @throws SQLGenerationException
     */
    public static String escapeQualifiedColumn(String column) throws SQLGenerationException {
        if (!isValidQualifiedColumnName(column)) {
            throw new SQLGenerationException("column name is not valid");
        }

        return VALID_NAME.matcher(column).replaceAll("\"$1\"");
    }
}
