package org.greenrobot.greendao.test.entityannotation;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class NotNullThing {

    @Id
    private Long id;

    Boolean nullableBoolean;
    Integer nullableInteger;

    @NotNull
    Boolean notNullBoolean;
    @NotNull
    Integer notNullInteger;

    public Integer getNotNullInteger() {
        return this.notNullInteger;
    }
    public void setNotNullInteger(Integer notNullInteger) {
        this.notNullInteger = notNullInteger;
    }
    public Boolean getNotNullBoolean() {
        return this.notNullBoolean;
    }
    public void setNotNullBoolean(Boolean notNullBoolean) {
        this.notNullBoolean = notNullBoolean;
    }
    public Integer getNullableInteger() {
        return this.nullableInteger;
    }
    public void setNullableInteger(Integer nullableInteger) {
        this.nullableInteger = nullableInteger;
    }
    public Boolean getNullableBoolean() {
        return this.nullableBoolean;
    }
    public void setNullableBoolean(Boolean nullableBoolean) {
        this.nullableBoolean = nullableBoolean;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 2048873927)
    public NotNullThing(Long id, Boolean nullableBoolean, Integer nullableInteger,
            @NotNull Boolean notNullBoolean, @NotNull Integer notNullInteger) {
        this.id = id;
        this.nullableBoolean = nullableBoolean;
        this.nullableInteger = nullableInteger;
        this.notNullBoolean = notNullBoolean;
        this.notNullInteger = notNullInteger;
    }
    @Generated(hash = 521031743)
    public NotNullThing() {
    }

}
