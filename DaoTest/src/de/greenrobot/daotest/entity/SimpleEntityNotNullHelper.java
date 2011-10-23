/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daotest.entity;

import de.greenrobot.daotest.SimpleEntityNotNull;


public class SimpleEntityNotNullHelper {
    public static SimpleEntityNotNull createEntity(Long key) {
        if (key == null) {
            return null;
        }
        SimpleEntityNotNull entity = new SimpleEntityNotNull();
        entity.setId(key);
        entity.setSimpleBoolean(true);
        entity.setSimpleByte(Byte.MAX_VALUE);
        entity.setSimpleShort(Short.MAX_VALUE);
        entity.setSimpleInt(Integer.MAX_VALUE);
        entity.setSimpleLong(Long.MAX_VALUE);
        entity.setSimpleFloat(Float.MAX_VALUE);
        entity.setSimpleDouble(Double.MAX_VALUE);
        entity.setSimpleString("greenrobot greenDAO");
        byte[] bytes = { 42, -17, 23, 0, 127, -128 };
        entity.setSimpleByteArray(bytes);
        return entity;
    }
}
