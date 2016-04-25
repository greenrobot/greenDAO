package org.greenrobot.greendao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Marks field is not persistent
 */
@Retention(SOURCE)
@Target(ElementType.FIELD)
public @interface Transient {
}
