package de.greenrobot.dao;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class TableStatements {
    protected final SQLiteDatabase db;
    protected final String tablename;

    protected SQLiteStatement insertStatement;
    protected SQLiteStatement insertOrReplaceStatement;
    protected SQLiteStatement updateStatement;
    protected SQLiteStatement deleteStatement;

    private volatile String selectAll;
    private volatile String selectByKey;
    private volatile String selectByRowId;
    private final String[] allColumns;
    private final String[] pkColumns;

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
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendCommaSeparated(builder, "", allColumns);
            builder.append(" FROM ").append(tablename).append(' ');
            selectAll = builder.toString();
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
