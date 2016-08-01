package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used to:
 * - specifies that the property should be indexed
 * - define multi-column index through {@link Entity#indexes()}
 *
 * @see Entity#indexes()
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Index {
    /**
     * Comma-separated list of properties that should be indexed, e.g. "propertyA, propertyB, propertyC"
     * To specify order, add ASC or DESC after column name, e.g.: "propertyA DESC, propertyB ASC"
     * This should be only set if this annotation is used in {@link Entity#indexes()}
     */
    String value() default "";

    /**
     * Optional name of the index.
     * If omitted, then generated automatically by greenDAO with base on property/properties column name(s)
     */
    String name() default "";

    /**
     * Whether the unique constraint should be created with base on this index
     */
    boolean unique() default false;
}
