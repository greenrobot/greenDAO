package de.greenrobot.daogenerator;

public class QueryParam {
    private Column column;
    private String operator;
    
    public QueryParam(Column column, String operator) {
        this.column = column;
        this.operator = operator;
    }

    public Column getColumn() {
        return column;
    }

    public String getOperator() {
        return operator;
    }
    
}
