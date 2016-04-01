package de.greenrobot.daotest.customtype;

import de.greenrobot.dao.converter.PropertyConverter;

public class MyTimestampConverter implements PropertyConverter<MyTimestamp, Long> {
    @Override
    public MyTimestamp convertToEntityProperty(Long databaseValue) {
        MyTimestamp myTimestamp = new MyTimestamp();
        myTimestamp.timestamp=databaseValue;
        return myTimestamp;
    }

    @Override
    public Long convertToDatabaseValue(MyTimestamp entityProperty) {
        return entityProperty.timestamp;
    }
}
