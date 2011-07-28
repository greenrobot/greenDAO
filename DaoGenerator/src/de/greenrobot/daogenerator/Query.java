package de.greenrobot.daogenerator;

import java.util.ArrayList;
import java.util.List;

public class Query {
    private String name;
    private List<QueryParam> parameters;
    private boolean distinct;

    public Query(String name) {
        this.name = name;
        parameters= new ArrayList<QueryParam>();
    }
    
    public QueryParam addEqualsParam(Column column) {
        return addParam(column, "=");
    }

    public QueryParam addParam(Column column, String operator) {
        QueryParam queryParam = new QueryParam(column, operator);
        parameters.add(queryParam);
        return queryParam;
    }
    
    public void distinct() {
        distinct = true;
    }


}
