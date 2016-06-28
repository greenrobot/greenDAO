package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Transient fields are not persisted in the database.
 */
@Retention(SOURCE)
@Target(ElementType.FIELD)
public @interface Transient {
}
