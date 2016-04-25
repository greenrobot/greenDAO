package org.greenrobot.greendao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for entities
 * greenDAO only persist objects of classes which are marked with this annotation
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Entity {
    /**
     * Specifies schema name for the entity
     * greenDAO generates independent sets of classes for each schema
     * Entities which belong to different schemas should <strong>not</strong> have relations
     */
    String schema() default "default";

    /**
     * Whether update/delete/refresh methods should be generated
     * If entity has defined {@link ToMany} or {@link ToOne} relations, then it is active independently from this value
     */
    boolean active() default false;
}
