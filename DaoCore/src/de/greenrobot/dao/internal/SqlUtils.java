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
package de.greenrobot.dao.internal;

import de.greenrobot.dao.DaoException;

/** Helper class to create SQL statements as used by greenDAO internally. */
public class SqlUtils {

    public static StringBuilder appendColumn(StringBuilder builder, String column) {
        builder.append('\'').append(column).append('\'');
        return builder;
    }

    public static StringBuilder appendColumn(StringBuilder builder, String tableAlias, String column) {
        builder.append(tableAlias).append(".'").append(column).append('\'');
        return builder;
    }

    public static StringBuilder appendColumns(StringBuilder builder, String tableAlias, String[] columns) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            appendColumn(builder, tableAlias, columns[i]);
            if (i < length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    public static StringBuilder appendColumns(StringBuilder builder, String[] columns) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            builder.append('\'').append(columns[i]).append('\'');
            if (i < length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    public static StringBuilder appendPlaceholders(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            if (i < count - 1) {
                builder.append("?,");
            } else {
                builder.append('?');
            }
        }
        return builder;
    }

    public static StringBuilder appendColumnsEqualPlaceholders(StringBuilder builder, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            appendColumn(builder, columns[i]).append("=?");
            if (i < columns.length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    public static StringBuilder appendColumnsEqValue(StringBuilder builder, String tableAlias, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            appendColumn(builder, tableAlias, columns[i]).append("=?");
            if (i < columns.length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    public static String createSqlInsert(String insertInto, String tablename, String[] columns) {
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append(tablename).append(" (");
        appendColumns(builder, columns);
        builder.append(") VALUES (");
        appendPlaceholders(builder, columns.length);
        builder.append(')');
        return builder.toString();
    }

    /** Creates an select for given columns with a trailing space */
    public static String createSqlSelect(String tablename, String tableAlias, String[] columns) {
        StringBuilder builder = new StringBuilder("SELECT ");
        if (tableAlias == null || tableAlias.length() < 0) {
            throw new DaoException("Table alias required");
        }

        SqlUtils.appendColumns(builder, tableAlias, columns).append(" FROM ");
        builder.append(tablename).append(' ').append(tableAlias).append(' ');
        return builder.toString();
    }

    /** Creates SELECT COUNT(*) with a trailing space. */
    public static String createSqlSelectCountStar(String tablename, String tableAliasOrNull) {
        StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM ");
        builder.append(tablename).append(' ');
        if(tableAliasOrNull != null) {
            builder.append(tableAliasOrNull).append(' ');
        }
        return builder.toString();
    }

    /** Remember: SQLite does not support joins nor table alias for DELETE. */
    public static String createSqlDelete(String tablename, String[] columns) {
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append(tablename);
        if (columns != null && columns.length > 0) {
            builder.append(" WHERE ");
            appendColumnsEqValue(builder, tablename, columns);
        }
        return builder.toString();
    }

    public static String createSqlUpdate(String tablename, String[] updateColumns, String[] whereColumns) {
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append(tablename).append(" SET ");
        appendColumnsEqualPlaceholders(builder, updateColumns);
        builder.append(" WHERE ");
        appendColumnsEqValue(builder, tablename, whereColumns);
        return builder.toString();
    }

}
