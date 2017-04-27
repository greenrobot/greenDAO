package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies ordering of related collection of {@link ToMany} relation
 * E.g.: @OrderBy("name, age DESC") List collection;
 * If used as marker (@OrderBy List collection), then collection is ordered by primary key
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface OrderBy {
    /**
     * Comma-separated list of properties, e.g. "propertyA, propertyB, propertyC"
     * To specify direction, add ASC or DESC after property name, e.g.: "propertyA DESC, propertyB ASC"
     * Default direction for each property is ASC
     * If value is omitted, then collection is ordered by primary key
     */
    String value() default "";
}
