package de.greenrobot.daogenerator;

/** NOT IMPLEMENTED YET. Check back later. */
public class QueryParam {
    private Property column;
    private String operator;
    
    public QueryParam(Property column, String operator) {
        this.column = column;
        this.operator = operator;
    }

    public Property getColumn() {
        return column;
    }

    public String getOperator() {
        return operator;
    }
    
}
