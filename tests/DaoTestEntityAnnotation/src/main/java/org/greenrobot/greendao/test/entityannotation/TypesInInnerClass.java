package org.greenrobot.greendao.test.entityannotation;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.converter.PropertyConverter;

@Entity
public class TypesInInnerClass {
    static class MyInnerType {

        public MyInnerType(String value) {
            this.value = value;
        }

        String value;
    }

    static class MyInnerTypeConverter implements PropertyConverter<MyInnerType, Long> {

        @Override
        public MyInnerType convertToEntityProperty(Long databaseValue) {
            return databaseValue != null ? new MyInnerType(Long.toHexString(databaseValue)) : null;
        }

        @Override
        public Long convertToDatabaseValue(MyInnerType entityProperty) {
            return entityProperty != null ? Long.parseLong(entityProperty.value, 16) : null;
        }
    }

    @Id
    Long id;

    @Convert(converter = MyInnerTypeConverter.class, columnType = Long.class)
    TypesInInnerClass.MyInnerType type;

    public MyInnerType getType() {
        return this.type;
    }

    public void setType(MyInnerType type) {
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 582873426)
    public TypesInInnerClass(Long id, TypesInInnerClass.MyInnerType type) {
        this.id = id;
        this.type = type;
    }

    @Generated(hash = 1754325029)
    public TypesInInnerClass() {
    }

}
