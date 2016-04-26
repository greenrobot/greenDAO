package org.greenrobot.greendao.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines source and target properties for relations
 *
 * @see ToMany
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface JoinOn {
    /** Name of the property in the source entity, which matches {@link #target()} */
    String source();

    /** Name of the property in the target entity, which matches {@link #source()} */
    String target();
}
