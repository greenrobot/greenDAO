package org.greenrobot.greendao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the target should be kept during next run of greenDAO generation.
 * <p>
 * Using this annotation on an Entity class itself silently disables any class modification.
 * The user is responsible to write and support any code which is required for greenDAO.
 * </p>
 * <p>
 * Don't use this annotation on a class member if you are not completely sure what you are doing, because in
 * case of model changes greenDAO will not be able to make corresponding changes into the code of the target.
 * </p>
 *
 * @see Generated
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})
public @interface Keep {
}
