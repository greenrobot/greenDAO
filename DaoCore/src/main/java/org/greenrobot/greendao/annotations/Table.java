package org.greenrobot.greendao.annotations;

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
     * Specifies name of the table in DB. By default class name of the entity is used
     */
    String name() default "";

    /**
     * Multi-column indexes for the table
     * To create a single-column index use {@link Column#index()} instead
     */
    Index[] indexes() default {};
}
