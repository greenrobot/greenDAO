package org.greenrobot.greendao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks field is the primary key of the entity's table
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Id {
    /** Specifies that id should be auto-incremented (works only for Long/long fields) */
    boolean autoincrement() default false;
}
