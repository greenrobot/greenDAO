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
    
    public QueryParam addEqualsParam(Property column) {
        return addParam(column, "=");
    }

    public QueryParam addParam(Property column, String operator) {
        QueryParam queryParam = new QueryParam(column, operator);
        parameters.add(queryParam);
        return queryParam;
    }
    
    public void distinct() {
        distinct = true;
    }


}
