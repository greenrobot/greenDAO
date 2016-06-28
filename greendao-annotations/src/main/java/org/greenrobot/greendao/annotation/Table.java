package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies additional properties for the corresponding entity's table
 * This annotation makes an effect only for classes which are marked with {@link Entity} annotation
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * Specifies the table name on the DB side. By default, the name is based on the entities class name.
     */
    String name() default "";

    /**
     * Indexes for the table
     * To create a single-column index consider using {@link Index} on the property itself
     */
    Index[] indexes() default {};

    /**
     * Whether corresponding table should be automatically created
     * Disabling creation can be useful for SQLITE_MASTER table
     */
    boolean create() default true;
}
