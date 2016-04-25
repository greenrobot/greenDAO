package org.greenrobot.greendao.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to define multi-column index
 * To index single column use {@link Column#index}
 *
 * @see Table#indexes()
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Index {
    /**
     * Comma-separated list of properties that should be indexed, e.g. "columnA, columnB, columnC"
     * To specify order, add ASC or DESC after column name, e.g.: "columnA DESC, columnB ASC, columnC DESC"
     */
    String value();

    /**
     * Optional name of the index
     */
    String name() default "";

    /**
     * Whether the index is unique
     */
    boolean unique() default false;
}
