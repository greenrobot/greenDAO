/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/** Helper class to create SQL statements for specific tables (used by greenDAO internally). */
// Note: avoid locking while compiling any statement (accessing the db) to avoid deadlocks on lock-savvy DBs like
// SQLCipher.
public class TableStatements {
    private final SQLiteDatabase db;
    private final String tablename;
    private final String[] allColumns;
    private final String[] pkColumns;

    private volatile SQLiteStatement insertStatement;
    private volatile SQLiteStatement insertOrReplaceStatement;
    private volatile SQLiteStatement updateStatement;
    private volatile SQLiteStatement deleteStatement;

    private volatile String selectAll;
    private volatile String selectByKey;
    private volatile String selectByRowId;
    private volatile String selectKeys;

    public TableStatements(SQLiteDatabase db, String tablename, String[] allColumns, String[] pkColumns) {
        this.db = db;
        this.tablename = tablename;
        this.allColumns = allColumns;
        this.pkColumns = pkColumns;
    }

    public SQLiteStatement getInsertStatement() {
        if (insertStatement == null) {
            String sql = SqlUtils.createSqlInsert("INSERT INTO ", tablename, allColumns);
            SQLiteStatement newInsertStatement = db.compileStatement(sql);
            synchronized (this) {
                if (insertStatement == null) {
                    insertStatement = newInsertStatement;
                }
            }
            if (insertStatement != newInsertStatement) {
                newInsertStatement.close();
            }
        }
        return insertStatement;
    }

    public SQLiteStatement getInsertOrReplaceStatement() {
        if (insertOrReplaceStatement == null) {
            String sql = SqlUtils.createSqlInsert("INSERT OR REPLACE INTO ", tablename, allColumns);
            SQLiteStatement newInsertOrReplaceStatement = db.compileStatement(sql);
            synchronized (this) {
                if (insertOrReplaceStatement == null) {
                    insertOrReplaceStatement = newInsertOrReplaceStatement;
                }
            }
            if (insertOrReplaceStatement != newInsertOrReplaceStatement) {
                newInsertOrReplaceStatement.close();
            }
        }
        return insertOrReplaceStatement;
    }

    public SQLiteStatement getDeleteStatement() {
        if (deleteStatement == null) {
            String sql = SqlUtils.createSqlDelete(tablename, pkColumns);
            SQLiteStatement newDeleteStatement = db.compileStatement(sql);
            synchronized (this) {
                if (deleteStatement == null) {
                    deleteStatement = newDeleteStatement;
                }
            }
            if (deleteStatement != newDeleteStatement) {
                newDeleteStatement.close();
            }
        }
        return deleteStatement;
    }

    public SQLiteStatement getUpdateStatement() {
        if (updateStatement == null) {
            String sql = SqlUtils.createSqlUpdate(tablename, allColumns, pkColumns);
            SQLiteStatement newUpdateStatement = db.compileStatement(sql);
            synchronized (this) {
                if (updateStatement == null) {
                    updateStatement = newUpdateStatement;
                }
            }
            if (updateStatement != newUpdateStatement) {
                newUpdateStatement.close();
            }
        }
        return updateStatement;
    }

    /** ends with an space to simplify appending to this string. */
    public String getSelectAll() {
        if (selectAll == null) {
            selectAll = SqlUtils.createSqlSelect(tablename, "T", allColumns, false);
        }
        return selectAll;
    }

    /** ends with an space to simplify appending to this string. */
    public String getSelectKeys() {
        if (selectKeys == null) {
            selectKeys = SqlUtils.createSqlSelect(tablename, "T", pkColumns, false);
        }
        return selectKeys;
    }

    // TODO precompile
    public String getSelectByKey() {
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
