package org.greenrobot.greendao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional. Specifies the mapped column for a persistent field.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * Name of the column. Default is field name.
     */
    String name() default "";

    /**
     * Whether the index should be created.
     *
     * <p>
     * If there is a multi-column index which has this column as a primary column,
     * then creating single-column index is redundant.
     * </p>
     *
     * <p>
     * Setting this to true is redundant if {@link #unique()} is set to true
     * </p>
     *
     * @see Index for multi-column index
     */
    boolean index() default false;

    /**
     * Whether the column values should be unique
     */
    boolean unique() default false;
}
