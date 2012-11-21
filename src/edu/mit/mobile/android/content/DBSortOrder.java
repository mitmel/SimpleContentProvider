package edu.mit.mobile.android.content;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate your {@link ContentItem} class with this in order to specify a default sort order when
 * queried using {@link GenericDBHelper}. It's often easiest to define the sort order as a
 * {@code static final String} in the class and then refer to it in this annotation value.
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface DBSortOrder {

    /**
     * @return the default sort order for this type
     */
    public String value();
}
