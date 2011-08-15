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

public class SqlUtils {

    public static void appendCommaSeparated(StringBuilder builder, String valuePrefix, String[] values) {
        int length = values.length;
        for (int i = 0; i < length; i++) {
            builder.append(valuePrefix).append(values[i]);
            if (i < length - 1) {
                builder.append(',');
            }
        }
    }

    public static void appendCommaSeparatedEqPlaceholder(StringBuilder builder, String valuePrefix, String[] values) {
        int length = values.length;
        for (int i = 0; i < length; i++) {
            builder.append(valuePrefix).append(values[i]).append("=?");
            if (i < length - 1) {
                builder.append(',');
            }
        }
    }

    public static void apppendPlaceholders(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            if (i < count - 1) {
                builder.append("?,");
            } else {
                builder.append('?');
            }
        }
    }

    public static String createSqlInsert(String insertInto, String tablename, String[] columns) {
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append(tablename).append(" (");
        appendCommaSeparated(builder, "", columns);
        builder.append(") VALUES (");
        apppendPlaceholders(builder, columns.length);
        builder.append(')');
        return builder.toString();
    }

    /** Creates an select for given columns with a trailing space */
    public static String createSqlSelect(String tablename, String tableAlias, String[] columns) {
        StringBuilder builder = new StringBuilder("SELECT ");
        boolean useAlias = tableAlias != null && tableAlias.length() > 0;
        String valuePrefix = useAlias ? tableAlias + "." : "";
        SqlUtils.appendCommaSeparated(builder, valuePrefix, columns);
        builder.append(" FROM ").append(tablename).append(' ');
        if (useAlias) {
            builder.append(tableAlias).append(' ');
        }
        return builder.toString();
    }

    public static void appendColumnsEqualPlaceholders(StringBuilder builder, String[] pks) {
        for (int i = 0; i < pks.length; i++) {
            builder.append(pks[i]).append("=?");
            if (i < pks.length - 1) {
                builder.append(',');
            }
        }
    }

}
