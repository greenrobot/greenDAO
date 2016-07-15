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

package org.greenrobot.greendao.internal;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

/** Helper class to create SQL statements for specific tables (used by greenDAO internally). */
// Note: avoid locking while compiling any statement (accessing the db) to avoid deadlocks on lock-savvy DBs like
// SQLCipher.
public class TableStatements {
    private final Database db;
    private final String tablename;
    private final String[] allColumns;
    private final String[] pkColumns;

    private DatabaseStatement insertStatement;
    private DatabaseStatement insertOrReplaceStatement;
    private DatabaseStatement updateStatement;
    private DatabaseStatement deleteStatement;
    private DatabaseStatement countStatement;

    private volatile String selectAll;
    private volatile String selectByKey;
    private volatile String selectByRowId;
    private volatile String selectKeys;

    public TableStatements(Database db, String tablename, String[] allColumns, String[] pkColumns) {
        this.db = db;
        this.tablename = tablename;
        this.allColumns = allColumns;
        this.pkColumns = pkColumns;
    }

    public DatabaseStatement getInsertStatement() {
        if (insertStatement == null) {
            String sql = SqlUtils.createSqlInsert("INSERT INTO ", tablename, allColumns);
            DatabaseStatement newInsertStatement = db.compileStatement(sql);
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

    public DatabaseStatement getInsertOrReplaceStatement() {
        if (insertOrReplaceStatement == null) {
            String sql = SqlUtils.createSqlInsert("INSERT OR REPLACE INTO ", tablename, allColumns);
            DatabaseStatement newInsertOrReplaceStatement = db.compileStatement(sql);
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

    public DatabaseStatement getDeleteStatement() {
        if (deleteStatement == null) {
            String sql = SqlUtils.createSqlDelete(tablename, pkColumns);
            DatabaseStatement newDeleteStatement = db.compileStatement(sql);
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

    public DatabaseStatement getUpdateStatement() {
        if (updateStatement == null) {
            String sql = SqlUtils.createSqlUpdate(tablename, allColumns, pkColumns);
            DatabaseStatement newUpdateStatement = db.compileStatement(sql);
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

    public DatabaseStatement getCountStatement() {
        if (countStatement == null) {
            String sql = SqlUtils.createSqlCount(tablename);
            countStatement = db.compileStatement(sql);
        }
        return countStatement;
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
