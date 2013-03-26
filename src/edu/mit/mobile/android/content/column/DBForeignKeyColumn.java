package edu.mit.mobile.android.content.column;
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.mit.mobile.android.content.ContentItem;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DBForeignKeyColumn {

    Class<? extends ContentItem> parent();

    /**
     * Sets this column to be NOT NULL.
     *
     * @return true if the column is NOT NULL
     */
    boolean notnull() default false;

    /**
     * Suffixes the column declaration with this string.
     *
     * @return a string of any supplemental column declarations
     */
    String extraColDef() default DBColumn.NULL;

}
