package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional: configures the mapped column for a persistent field.
 * This annotation is also applicable with @ToOne without additional foreign key property
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Property {
    /**
     * Name of the database column for this property. Default is field name.
     */
    String nameInDb() default "";
}
