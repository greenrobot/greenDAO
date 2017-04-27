package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines *-to-* relation with join table
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface JoinEntity {
    /** Reference to join-entity class, which holds the source and the target properties */
    Class<?> entity();

    /** Name of the property inside the join entity which holds id of the source (current) entity */
    String sourceProperty();

    /** Name of the property inside the join entity which holds id of the target entity */
    String targetProperty();
}
