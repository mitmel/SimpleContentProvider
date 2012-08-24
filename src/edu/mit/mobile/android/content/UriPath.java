package edu.mit.mobile.android.content;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional. For use on {@link ContentItem} classes. This specifies the path under which the content
 * items are found so that helpers like {@link ForeignKeyManager} can automatically construct URLs.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface UriPath {

    public String value();

    public static class Extractor {
        /**
         * Extract the UriPath value from the given class.
         *
         * @param contentItem
         * @param required
         * @return the path as specified by the UriPath annotation or null.
         * @throws SQLGenerationException
         *             if required is true and the annotation is missing.
         *
         */
        public static String extractUriPath(Class<? extends ContentItem> contentItem,
                boolean required) {
            String pathString;
            final UriPath path = contentItem.getAnnotation(UriPath.class);
            pathString = path != null ? path.value() : null;
            if (required && pathString == null) {
                throw new SQLGenerationException("ForeignKeyManager: missing @UriPath on "
                        + contentItem);
            }
            return pathString;
        }
    };
}
