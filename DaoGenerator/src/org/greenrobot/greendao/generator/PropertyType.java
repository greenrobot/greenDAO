/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao.generator;

/**
 * Currently available types for properties.
 *
 * @author Markus
 */
public enum PropertyType {

    Byte(true), Short(true), Int(true), Long(true), Boolean(true), Float(true), Double(true),
    String(false), ByteArray(false), Date(false);

    private final boolean scalar;

    PropertyType(boolean scalar) {
        this.scalar = scalar;
    }

    /** True if the type can be prepresented using a scalar (primitive type). */
    public boolean isScalar() {
        return scalar;
    }
}
