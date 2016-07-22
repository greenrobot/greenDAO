/*
 * Copyright (C) 2011-2015 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.greenrobot.greendao.daotest.customtype;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Just a sketch how an embedded List could be done using PropertyConverter. Usually, doing a separate table is
 * preferred because the values can be indexed and queried for.
 */
public class IntegerListConverter implements PropertyConverter<List<Integer>, byte[]> {
    @Override
    public List<Integer> convertToEntityProperty(byte[] databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        if (databaseValue.length % 4 != 0) {
            throw new RuntimeException("Length must be dividable by 4, but is " + databaseValue.length);
        }
        ArrayList<Integer> list = new ArrayList<Integer>(databaseValue.length / 4);
        for (int i = 0; i < databaseValue.length; i += 4) {
            int intValue = getIntBE(databaseValue, i);
            list.add(intValue);
        }

        return list;
    }

    @Override
    public byte[] convertToDatabaseValue(List<Integer> entityProperty) {
        if (entityProperty == null) {
            return null;
        }
        byte[] bytes = new byte[entityProperty.size() * 4];
        for (Integer integer : entityProperty) {
            // TODO
        }
        return bytes;

    }

    public int getIntBE(byte[] bytes, int index) {
        return (bytes[index + 3] & 0xff) | ((bytes[index + 2] & 0xff) << 8) |
                ((bytes[index + 1] & 0xff) << 16) | (bytes[index] << 24);
    }
}
