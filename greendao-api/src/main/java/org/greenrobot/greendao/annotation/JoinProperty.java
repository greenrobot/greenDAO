package org.greenrobot.greendao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines name and referencedName properties for relations
 *
 * @see ToMany
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface JoinProperty {
    /** Name of the property in the name entity, which matches {@link #referencedName()} */
    String name();

    /** Name of the property in the referencedName entity, which matches {@link #name()} */
    String referencedName();
}
