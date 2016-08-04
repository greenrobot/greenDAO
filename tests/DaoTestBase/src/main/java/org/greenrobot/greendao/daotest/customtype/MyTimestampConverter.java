package org.greenrobot.greendao.daotest.customtype;

import org.greenrobot.greendao.converter.PropertyConverter;

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
