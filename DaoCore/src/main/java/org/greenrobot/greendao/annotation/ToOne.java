package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines *-to-1 relation with base on existing property as foreign key or with base on
 * automatically created backing column
 *
 * In case foreignKey is not specified, the following annotations can be applied together with @ToOne:
 * - {@link Property} to specify backing column name
 * - {@link Unique} to put the unique constraint on backing column during table creation
 * - {@link NotNull} to put the NOT NULL constraint on backing column during table creation
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface ToOne {
    /**
     * Name of the property inside the current entity which holds the key of related entity.
     * If this parameter is absent, then an additional column is automatically created to hold the key.
     */
    String joinProperty() default "";
}
