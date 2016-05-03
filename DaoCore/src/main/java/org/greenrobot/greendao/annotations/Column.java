package org.greenrobot.greendao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional. Configures the mapped column for a persistent field.
 * This annotation is also applicable with @ToOne without additional foreign key property
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * Name of the column. Default is field name.
     */
    String name() default "";
}
