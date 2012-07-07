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

import de.greenrobot.dao.wrapper.SQLiteDatabaseWrapper;
import de.greenrobot.dao.wrapper.SQLiteStatementWrapper;

/** Helper class to create SQL statements for specific tables (used by greenDAO internally). */
public class TableStatements {
    private final SQLiteDatabaseWrapper db;
    private final String tablename;
    private final String[] allColumns;
    private final String[] pkColumns;

    private SQLiteStatementWrapper insertStatement;
    private SQLiteStatementWrapper insertOrReplaceStatement;
    private SQLiteStatementWrapper updateStatement;
    private SQLiteStatementWrapper deleteStatement;

    private volatile String selectAll;
    private volatile String selectByKey;
    private volatile String selectByRowId;

    protected TableStatements(SQLiteDatabaseWrapper db, String tablename, String[] allColumns, String[] pkColumns) {
        this.db = db;
        this.tablename = tablename;
        this.allColumns = allColumns;
        this.pkColumns = pkColumns;
    }

    protected SQLiteStatementWrapper getInsertStatement() {
        if (insertStatement == null) {
            String sql = SqlUtils.createSqlInsert("INSERT INTO ", tablename, allColumns);
            insertStatement = db.compileStatement(sql);
        }
        return insertStatement;
    }

    protected SQLiteStatementWrapper getInsertOrReplaceStatement() {
        if (insertOrReplaceStatement == null) {
            String sql = SqlUtils.createSqlInsert("INSERT OR REPLACE INTO ", tablename, allColumns);
            insertOrReplaceStatement = db.compileStatement(sql);
        }
        return insertOrReplaceStatement;
    }

    protected SQLiteStatementWrapper getDeleteStatement() {
        if (deleteStatement == null) {
            String sql = SqlUtils.createSqlDelete(tablename, pkColumns);
            deleteStatement = db.compileStatement(sql);
        }
        return deleteStatement;
    }

    protected SQLiteStatementWrapper getUpdateStatement() {
        if (updateStatement == null) {
            String sql = SqlUtils.createSqlUpdate(tablename, allColumns, pkColumns);
            updateStatement = db.compileStatement(sql);
        }
        return updateStatement;
    }

    /** ends with an space to simplify appending to this string. */
    protected String getSelectAll() {
        if (selectAll == null) {
            selectAll = SqlUtils.createSqlSelect(tablename, "T", allColumns);
        }
        return selectAll;
    }

    // TODO precompile
    protected String getSelectByKey() {
        if (selectByKey == null) {
            StringBuilder builder = new StringBuilder(getSelectAll());
            builder.append("WHERE ");
            SqlUtils.appendColumnsEqValue(builder, "T", pkColumns);
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
