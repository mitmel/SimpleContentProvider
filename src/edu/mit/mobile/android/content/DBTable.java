package edu.mit.mobile.android.content;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the table name for the given {@link ContentItem}. Pass a string, which will be used as the table name.
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface DBTable {

	/**
	 * @return the name of the table
	 */
	String value();

}
