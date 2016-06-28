package org.greenrobot.greendao.annotation;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies {@link PropertyConverter} for the field to support custom types
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Convert {
    /** Converter class */
    Class<? extends PropertyConverter> converter();

    /**
     * Class of the column which can be persisted in DB.
     * This is limited to all java classes which are supported natively by greenDAO.
     */
    Class columnType();
}
