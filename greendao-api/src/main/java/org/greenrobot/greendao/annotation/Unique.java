package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks property should have a UNIQUE constraint during table creation.
 * This annotation is also applicable with @ToOne without additional foreign key property
 *
 * <p>
 * To have a unique constraint after table creation you can use {@link Index#unique()}
 * </p>
 *
 * <p>
 * Note having both @Unique and {@link Index} is redundant and causes performance decrease
 * on DB level. See <a href="https://www.sqlite.org/lang_createtable.html">here</a> for more information.
 * </p>
 *
 * @see Index#unique()
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Unique {
}
