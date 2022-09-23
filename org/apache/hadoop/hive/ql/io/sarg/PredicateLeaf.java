// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.ql.io.sarg;

import java.util.List;

public interface PredicateLeaf
{
    Operator getOperator();
    
    Type getType();
    
    String getColumnName();
    
    Object getLiteral();
    
    List<Object> getLiteralList();
    
    public enum Operator
    {
        EQUALS, 
        NULL_SAFE_EQUALS, 
        LESS_THAN, 
        LESS_THAN_EQUALS, 
        IN, 
        BETWEEN, 
        IS_NULL;
    }
    
    public enum Type
    {
        INTEGER, 
        LONG, 
        FLOAT, 
        STRING, 
        DATE, 
        DECIMAL, 
        TIMESTAMP, 
        BOOLEAN;
    }
}
