package annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used by Jacoco test coverage gradle plugin to exclude items from scan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Generated { }
