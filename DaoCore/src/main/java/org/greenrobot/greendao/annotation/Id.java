package org.greenrobot.greendao.annotation;

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
    /**
     * Specifies that id should be auto-incremented (works only for Long/long fields)
     * Autoincrement on SQLite introduces additional resources usage and usually can be avoided
     * @see <a href="https://www.sqlite.org/autoinc.html">SQLite documentation</a>
     */
    boolean autoincrement() default false;
}
