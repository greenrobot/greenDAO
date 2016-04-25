package org.greenrobot.greendao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines *-to-1 relation
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface ToOne {
    /**
     * Name of the property inside the current entity which holds the key of related entity
     * You should specify either foreignKey or {@link #mappedBy}
     */
    String foreignKey() default "";

    /**
     * Name of the property inside the target entity which holds id of the source (current) entity
     * You should specify either {@link #foreignKey} or mappedBy
     */
    String mappedBy() default "";

    /**
     * Whether the relation is unique. Applicable only if {@link #mappedBy} is in use
     */
    boolean unique() default false;
}
