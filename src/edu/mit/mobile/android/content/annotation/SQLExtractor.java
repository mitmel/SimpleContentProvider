package edu.mit.mobile.android.content.annotation;

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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import android.database.DatabaseUtils;
import edu.mit.mobile.android.content.AndroidVersions;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.DBTable;
import edu.mit.mobile.android.content.SQLGenUtils;
import edu.mit.mobile.android.content.SQLGenerationException;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.DBColumn.OnConflict;
import edu.mit.mobile.android.content.column.DBColumnType;
import edu.mit.mobile.android.content.column.DBForeignKeyColumn;

/**
 * Generates SQL from a {@link ContentItem}. This inspects the class, parses all its
 * annotations, and so on.
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 * @see DBColumn
 *
 */
public class SQLExtractor {

    private static final String DOUBLE_ESCAPE = DBColumnType.DEFAULT_VALUE_ESCAPE
            + DBColumnType.DEFAULT_VALUE_ESCAPE;

    private final Class<? extends ContentItem> mDataItem;

    private final String mTable;

    public SQLExtractor(Class<? extends ContentItem> contentItem) throws SQLGenerationException {
        mDataItem = contentItem;
        mTable = extractTableName(mDataItem);
    }

    /**
     * Inspects the {@link ContentItem} and extracts a table name from it. If there is a @DBTable
     * annotation, uses that name. Otherwise, uses a lower-cased, sanitized version of the
     * classname.
     *
     * @return a valid table name
     * @throws SQLGenerationException
     */
    public static String extractTableName(Class<? extends ContentItem> dataItem)
            throws SQLGenerationException {
        String tableName = null;
        final DBTable tableNameAnnotation = dataItem.getAnnotation(DBTable.class);
        if (tableNameAnnotation != null) {

            tableName = tableNameAnnotation.value();
            if (!SQLGenUtils.isValidName(tableName)) {
                throw new SQLGenerationException("Illegal table name: '" + tableName + "'");
            }
        } else {
            tableName = SQLGenUtils.toValidName(dataItem);
        }
        return tableName;
    }

    /**
     * The table name is auto-generated using {@link #extractTableName(Class)}.
     *
     * @return the name of the table for this {@link ContentItem}.
     */
    public String getTableName() {
        return mTable;
    }

    /**
     * For a given {@code field}, return the value of the field. All fields must be
     * {@code static String}s whose content is the column name. This method ensures that they
     * fit this requirement.
     *
     * @param field
     *            the {@code static String} field
     * @return the value of the field.
     * @throws SQLGenerationException
     *             if the field doesn't meet the necessary requirements.
     */
    public String getDbColumnName(Field field) throws SQLGenerationException {
        String dbColumnName;
        try {
            dbColumnName = (String) field.get(null);

        } catch (final IllegalArgumentException e) {
            throw new SQLGenerationException("programming error", e);

        } catch (final IllegalAccessException e) {
            throw new SQLGenerationException("field '" + field.getName()
                    + "' cannot be accessed", e);

        } catch (final NullPointerException e) {
            throw new SQLGenerationException("field '" + field.getName() + "' is not static", e);
        }

        if (!SQLGenUtils.isValidName(dbColumnName)) {
            throw new SQLGenerationException("@DBColumn '" + dbColumnName
                    + "' is not a valid SQLite column name.");
        }

        return dbColumnName;
    }

    private void appendColumnDef(StringBuilder tableSQL, DBColumn t, Field field,
            List<String> preSql, List<String> postSql) throws IllegalAccessException,
            InstantiationException {
        @SuppressWarnings("rawtypes")
        final Class<? extends DBColumnType> columnType = t.type();
        @SuppressWarnings("rawtypes")
        final DBColumnType typeInstance = columnType.newInstance();

        final String colName = getDbColumnName(field);
        tableSQL.append(typeInstance.toCreateColumn(colName));
        if (t.primaryKey()) {
            tableSQL.append(" PRIMARY KEY");
            if (t.autoIncrement()) {
                tableSQL.append(" AUTOINCREMENT");
            }
        }

        if (t.unique()) {
            tableSQL.append(" UNIQUE");
            if (t.onConflict() != OnConflict.UNSPECIFIED) {
                tableSQL.append(" ON CONFLICT");
            }
            switch (t.onConflict()) {
                case ABORT:
                    tableSQL.append(" ABORT");
                    break;
                case FAIL:
                    tableSQL.append(" FAIL");
                    break;
                case IGNORE:
                    tableSQL.append(" IGNORE");
                    break;
                case REPLACE:
                    tableSQL.append(" REPLACE");
                    break;
                case ROLLBACK:
                    tableSQL.append(" ROLLBACK");
                    break;
                case UNSPECIFIED:
                    break;
            }
        }

        if (t.notnull()) {
            tableSQL.append(" NOT NULL");
        }

        switch (t.collate()) {
            case BINARY:
                tableSQL.append(" COLLATE BINARY");
                break;
            case NOCASE:
                tableSQL.append(" COLLATE NOCASE");
                break;
            case RTRIM:
                tableSQL.append(" COLLATE RTRIM");
                break;
            case DEFAULT:
                break;
        }

        final String defaultValue = t.defaultValue();
        final int defaultValueInt = t.defaultValueInt();
        final long defaultValueLong = t.defaultValueLong();
        final float defaultValueFloat = t.defaultValueFloat();
        final double defaultValueDouble = t.defaultValueDouble();

        if (!DBColumn.NULL.equals(defaultValue)) {
            tableSQL.append(" DEFAULT ");
            // double-escape to insert the escape character literally.
            if (defaultValue.startsWith(DOUBLE_ESCAPE)) {
                DatabaseUtils.appendValueToSql(tableSQL, defaultValue.substring(1));

            } else if (defaultValue.startsWith(DBColumnType.DEFAULT_VALUE_ESCAPE)) {
                tableSQL.append(defaultValue.substring(1));

            } else {

                DatabaseUtils.appendValueToSql(tableSQL, defaultValue);
            }
        } else if (defaultValueInt != DBColumn.NULL_INT) {
            tableSQL.append(" DEFAULT ");
            tableSQL.append(defaultValueInt);

        } else if (defaultValueLong != DBColumn.NULL_LONG) {
            tableSQL.append(" DEFAULT ");
            tableSQL.append(defaultValueLong);

        } else if (defaultValueFloat != DBColumn.NULL_FLOAT) {
            tableSQL.append(" DEFAULT ");
            tableSQL.append(defaultValueFloat);

        } else if (defaultValueDouble != DBColumn.NULL_DOUBLE) {
            tableSQL.append(" DEFAULT ");
            tableSQL.append(defaultValueDouble);
        }

        final String extraColDef = t.extraColDef();
        if (!DBColumn.NULL.equals(extraColDef)) {
            tableSQL.append(extraColDef);
        }

        final int flags = t.flags();

        final String pre = typeInstance.preTableSql(mTable, colName, flags);
        if (pre != null) {
            preSql.add(pre);
        }

        final String post = typeInstance.postTableSql(mTable, colName, flags);
        if (post != null) {
            postSql.add(post);
        }
    }

    /**
     * Gets the database column type of the field. The field must have a {@link DBColumn}
     * annotation.
     *
     * @param fieldName
     *            the name of the field. This is whatever name you've used for the
     *            {@code static String}, not its value.
     * @return the {@link DBColumnType} of the field
     * @throws SQLGenerationException
     * @throws NoSuchFieldException
     */
    public Class<? extends DBColumnType<?>> getFieldType(String fieldName)
            throws SQLGenerationException, NoSuchFieldException {

        final Field field = mDataItem.getField(fieldName);

        return getFieldType(field);
    }

    /**
     * Gets the database column type of the field. The field must have a {@link DBColumn}
     * annotation.
     *
     * @param field
     *            the given {@code static String} field.
     * @return the type of the field, as defined in the annotation or {@code null} if the given
     *         field has no {@link DBColumn} annotation.
     * @throws SQLGenerationException
     *             if an annotation is present, but there's an error in the field definition.
     */
    public Class<? extends DBColumnType<?>> getFieldType(Field field)
            throws SQLGenerationException {
        try {

            final DBColumn t = field.getAnnotation(DBColumn.class);

            if (t == null) {
                return null;
            }

            final int m = field.getModifiers();

            if (!String.class.equals(field.getType()) || !Modifier.isStatic(m)
                    || !Modifier.isFinal(m)) {
                throw new SQLGenerationException(
                        "Columns defined using @DBColumn must be static final Strings.");
            }
            return t.type();

        } catch (final IllegalArgumentException e) {
            throw new SQLGenerationException(
                    "field claimed to be static, but something went wrong on invocation", e);

        } catch (final SecurityException e) {
            throw new SQLGenerationException("cannot access class fields", e);
        }
    }

    /**
     * Generates SQL code for creating this object's table. Creation is done by inspecting the
     * static strings that are marked with {@link DBColumn} annotations.
     *
     * @return CREATE TABLE code for creating this table.
     * @throws SQLGenerationException
     *             if there were any problems creating the table
     * @see DBColumn
     * @see DBTable
     */
    public List<String> getTableCreation() throws SQLGenerationException {
        // pre, create table, post
        final LinkedList<String> preTableSql = new LinkedList<String>();
        final LinkedList<String> postTableSql = new LinkedList<String>();

        try {
            final StringBuilder tableSQL = new StringBuilder();

            tableSQL.append("CREATE TABLE ");
            tableSQL.append(mTable);
            tableSQL.append(" (");

            boolean needSep = false;
            for (final Field field : mDataItem.getFields()) {
                final DBColumn t = field.getAnnotation(DBColumn.class);
                final DBForeignKeyColumn fk = field.getAnnotation(DBForeignKeyColumn.class);
                if (t == null && fk == null) {
                    continue;
                }

                final int m = field.getModifiers();

                if (!String.class.equals(field.getType()) || !Modifier.isStatic(m)
                        || !Modifier.isFinal(m)) {
                    throw new SQLGenerationException(
                            "Columns defined using @DBColumn must be static final Strings.");
                }

                if (needSep) {
                    tableSQL.append(',');
                }

                if (t != null) {
                    appendColumnDef(tableSQL, t, field, preTableSql, postTableSql);

                } else if (fk != null) {
                    appendFKColumnDef(tableSQL, fk, field);
                }

                needSep = true;
            }
            tableSQL.append(")");

            preTableSql.add(tableSQL.toString());

            preTableSql.addAll(postTableSql);

            return preTableSql;

        } catch (final IllegalArgumentException e) {
            throw new SQLGenerationException(
                    "field claimed to be static, but something went wrong on invocation", e);

        } catch (final IllegalAccessException e) {
            throw new SQLGenerationException("default constructor not visible", e);

        } catch (final SecurityException e) {
            throw new SQLGenerationException("cannot access class fields", e);

        } catch (final InstantiationException e) {
            throw new SQLGenerationException("cannot instantiate field type class", e);
        }
    }

    private void appendFKColumnDef(StringBuilder tableSQL, DBForeignKeyColumn fk, Field field)
            throws IllegalArgumentException, IllegalAccessException {

        tableSQL.append("'");
        tableSQL.append(getDbColumnName(field));
        tableSQL.append("' INTEGER");
        if (fk.notnull()) {
            tableSQL.append(" NOT NULL");
        }

        if (AndroidVersions.SQLITE_SUPPORTS_FOREIGN_KEYS) {
            tableSQL.append(" REFERENCES ");
            final String parentTable = extractTableName(fk.parent());
            tableSQL.append("'");
            tableSQL.append(parentTable);
            tableSQL.append("' (");
            tableSQL.append(ContentItem._ID);
            tableSQL.append(") ON DELETE CASCADE"); // TODO make this configurable
        }
    }
}
