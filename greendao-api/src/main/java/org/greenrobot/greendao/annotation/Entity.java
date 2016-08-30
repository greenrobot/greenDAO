package org.greenrobot.greendao.annotation;

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
     * Specifies the name on the DB side (e.g. table name) this entity maps to. By default, the name is based on the entities class name.
     */
    String nameInDb() default "";

    /**
     * Indexes for the entity.
     * <p/>
     * Note: To create a single-column index consider using {@link Index} on the property itself
     */
    Index[] indexes() default {};

    /**
     * Advanced flag to disable table creation in the database (when set to false). This can be used to create partial
     * entities, which may use only a sub set of properties. Be aware however that greenDAO does not sync multiple
     * entities, e.g. in caches.
     */
    boolean createInDb() default true;

    /**
     * Specifies schema name for the entity: greenDAO can generate independent sets of classes for each schema.
     * Entities which belong to different schemas should <strong>not</strong> have relations.
     */
    String schema() default "default";

    /**
     * Whether update/delete/refresh methods should be generated.
     * If entity has defined {@link ToMany} or {@link ToOne} relations, then it is active independently from this value
     */
    boolean active() default false;

    /**
     * Whether an all properties constructor should be generated. A no-args constructor is always required.
     */
    boolean generateConstructors() default true;

    /**
     * Whether getters and setters for properties should be generated if missing.
     */
    boolean generateGettersSetters() default true;

    /**
     * Define a protobuf class of this entity to create an additional, special DAO for.
     */
    Class protobuf() default void.class;

}
