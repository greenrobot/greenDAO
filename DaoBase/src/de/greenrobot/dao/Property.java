/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
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

package de.greenrobot.dao;

import de.greenrobot.dao.WhereCondition.PropertyCondition;

/**
 * Meta data describing a property mapped to a database column.
 * 
 * @author Markus
 */
public class Property {
    public final int oridinal;
    public final Class<?> type;
    public final String name;
    public final boolean primaryKey;
    public final String columnName;

    public Property(int oridinal, Class<?> type, String name, boolean primaryKey, String columnName) {
        this.oridinal = oridinal;
        this.type = type;
        this.name = name;
        this.primaryKey = primaryKey;
        this.columnName = columnName;
    }

    public WhereCondition eq(Object value) {
        return new PropertyCondition(this, "=?", value);
    }

    public WhereCondition notEq(Object value) {
        return new PropertyCondition(this, "<>?", value);
    }

    public WhereCondition like(String value) {
        return new PropertyCondition(this, " LIKE ?", value);
    }

    public WhereCondition between(Object value1, Object value2) {
        Object[] values = { value1, value2 };
        return new PropertyCondition(this, " BETWEEN ? AND ?", values);
    }

    public WhereCondition in(Object... inValues) {
        StringBuilder condition = new StringBuilder(" IN (");
        SqlUtils.appendPlaceholders(condition, inValues.length).append(')');
        return new PropertyCondition(this, condition.toString(), inValues);
    }

    public WhereCondition gt(Object value) {
        return new PropertyCondition(this, ">?", value);
    }

    public WhereCondition lt(Object value) {
        return new PropertyCondition(this, "<?", value);
    }

    public WhereCondition ge(Object value) {
        return new PropertyCondition(this, ">=?", value);
    }

    public WhereCondition le(Object value) {
        return new PropertyCondition(this, "<=?", value);
    }

    public WhereCondition isNull() {
        return new PropertyCondition(this, " IS NULL");
    }

    public WhereCondition isNotNull() {
        return new PropertyCondition(this, " IS NOT NULL");
    }

}
