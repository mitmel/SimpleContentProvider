package edu.mit.mobile.android.content.column;

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
