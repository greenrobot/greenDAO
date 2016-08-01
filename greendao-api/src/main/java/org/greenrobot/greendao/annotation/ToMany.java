package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines *-to-N relation
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface ToMany {
    /**
     * Name of the property inside the target entity which holds id of the source (current) entity
     * Required unless no {@link JoinProperty} or {@link JoinEntity} is specified
     */
    String referencedJoinProperty() default "";

    /**
     * Array of matching source -> target properties
     * Required unless {@link #referencedJoinProperty()} or {@link JoinEntity} is specified
     */
    JoinProperty[] joinProperties() default {};
}
