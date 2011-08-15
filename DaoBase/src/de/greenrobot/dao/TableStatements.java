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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class TableStatements {
    private final SQLiteDatabase db;
    private final String tablename;
    private final String[] allColumns;
    private final String[] pkColumns;

    private SQLiteStatement insertStatement;
    private SQLiteStatement insertOrReplaceStatement;
    private SQLiteStatement updateStatement;
    private SQLiteStatement deleteStatement;

    private volatile String selectAll;
    private volatile String selectByKey;
    private volatile String selectByRowId;

    public TableStatements(SQLiteDatabase db, String tablename, String[] allColumns, String[] pkColumns) {
        this.db = db;
        this.tablename = tablename;
        this.allColumns = allColumns;
        this.pkColumns = pkColumns;
    }

    protected SQLiteStatement getInsertStatement() {
        if (insertStatement == null) {
            String sql = createSqlForInsert("INSERT INTO ");
            insertStatement = db.compileStatement(sql);
        }
        return insertStatement;
    }

    protected SQLiteStatement getInsertOrReplaceStatement() {
        if (insertOrReplaceStatement == null) {
            String sql = createSqlForInsert("INSERT OR REPLACE INTO ");
            insertOrReplaceStatement = db.compileStatement(sql);
        }
        return insertOrReplaceStatement;
    }

    protected String createSqlForInsert(String insertInto) {
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append(tablename).append(" (");
        SqlUtils.appendCommaSeparated(builder, "", allColumns);
        builder.append(") VALUES (");
        SqlUtils.apppendPlaceholders(builder, allColumns.length);
        builder.append(')');
        return builder.toString();
    }

    protected SQLiteStatement getDeleteStatement() {
        if (deleteStatement == null) {
            StringBuilder builder = new StringBuilder("DELETE FROM ");
            builder.append(tablename).append(" WHERE ");
            SqlUtils.appendColumnsEqualPlaceholders(builder, pkColumns);
            deleteStatement = db.compileStatement(builder.toString());
        }
        return deleteStatement;
    }

    protected SQLiteStatement getUpdateStatement() {
        if (updateStatement == null) {
            StringBuilder builder = new StringBuilder("UPDATE ");
            builder.append(tablename).append(" SET ");
            // TODO Use getNonPkColumns() only (performance)
            SqlUtils.appendColumnsEqualPlaceholders(builder, allColumns);
            builder.append(" WHERE ");
            SqlUtils.appendColumnsEqualPlaceholders(builder, pkColumns);
            updateStatement = db.compileStatement(builder.toString());
        }
        return updateStatement;
    }

    /** ends with an space to simplify appending to this string. */
    protected String getSelectAll() {
        if (selectAll == null) {
            selectAll = SqlUtils.createSqlSelect(tablename, null, allColumns);
        }
        return selectAll;
    }

    // TODO precompile
    protected String getSelectByKey() {
        if (selectByKey == null) {
            StringBuilder builder = new StringBuilder(getSelectAll());
            builder.append("WHERE ");
            SqlUtils.appendCommaSeparatedEqPlaceholder(builder, "", pkColumns);
            selectByKey = builder.toString();
        }
        return selectByKey;
    }

    public String getSelectByRowId() {
        if (selectByRowId == null) {
            selectByRowId = getSelectAll() + "WHERE ROWID=?";
        }
        return selectByRowId;
    }

}
