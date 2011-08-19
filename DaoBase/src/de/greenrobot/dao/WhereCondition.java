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

import java.util.List;

public interface WhereCondition {

    void appendTo(StringBuilder builder, String tableAlias);

    void appendValuesTo(List<Object> values);

    public abstract static class AbstractCondition implements WhereCondition {

        protected final boolean hasSingleValue;
        protected final Object value;
        protected final Object[] values;

        public AbstractCondition() {
            hasSingleValue = false;
            value = null;
            values = null;
        }

        public AbstractCondition(Object value) {
            this.value = value;
            hasSingleValue = true;
            values = null;
        }

        public AbstractCondition(Object[] values) {
            this.value = null;
            hasSingleValue = false;
            this.values = values;
        }

        @Override
        public void appendValuesTo(List<Object> valuesTarget) {
            if (hasSingleValue) {
                valuesTarget.add(value);
            }
            if (values != null) {
                for (Object value : values) {
                    valuesTarget.add(value);
                }
            }
        }
    }

    public static class PropertyCondition extends AbstractCondition {

        public final Property property;
        public final String op;

        public PropertyCondition(Property property, String op) {
            this.property = property;
            this.op = op;
        }

        public PropertyCondition(Property property, String op, Object value) {
            super(value);
            this.property = property;
            this.op = op;
        }

        public PropertyCondition(Property property, String op, Object[] values) {
            super(values);
            this.property = property;
            this.op = op;
        }

        @Override
        public void appendTo(StringBuilder builder, String tableAlias) {
            if (tableAlias != null) {
                builder.append(tableAlias).append('.');
            }
            builder.append(property.columnName).append(op);
        }

    }

    public static class StringCondition extends AbstractCondition {

        protected final String string;

        public StringCondition(String string) {
            this.string = string;
        }

        public StringCondition(String string, Object value) {
            super(value);
            this.string = string;
        }

        public StringCondition(String string, Object... values) {
            super(values);
            this.string = string;
        }

        @Override
        public void appendTo(StringBuilder builder, String tableAlias) {
            builder.append(string);
        }

    }

}
