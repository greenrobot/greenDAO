package de.greenrobot.daotest.entity;

import de.greenrobot.daotest.SimpleEntityDefaultValues;

/**
 * Create a test entity with all default values using null parameters for
 * primitive wrappers.
 */
public class SimpleEntityDefaultValuesHelper {
    public static SimpleEntityDefaultValues createEntity(Long key) {
        if (key == null) {
            return null;
        }

        SimpleEntityDefaultValues entity = new SimpleEntityDefaultValues(key,
                null,
                SimpleEntityDefaultValues.getDefaultSimpleBooleanFalse(),
                null,
                SimpleEntityDefaultValues.getDefaultSimpleByteMin(),
                null,
                SimpleEntityDefaultValues.getDefaultSimpleShortMin(),
                null,
                SimpleEntityDefaultValues.getDefaultSimpleIntMin(),
                null,
                SimpleEntityDefaultValues.getDefaultSimpleLongMin(),
                null,
                SimpleEntityDefaultValues.getDefaultSimpleFloatMin(),
                null,
                SimpleEntityDefaultValues.getDefaultSimpleDoubleMin(),
                SimpleEntityDefaultValues.getDefaultSimpleString(),
                SimpleEntityDefaultValues.getDefaultSimpleStringNotNull(),
                SimpleEntityDefaultValues.getDefaultSimpleDate(),
                SimpleEntityDefaultValues.getDefaultSimpleDateNotNull());

        return entity;
    }
}
