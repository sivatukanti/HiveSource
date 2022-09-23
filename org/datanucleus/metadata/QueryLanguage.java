// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum QueryLanguage
{
    JDOQL, 
    SQL, 
    JPQL, 
    STOREDPROC;
    
    public static QueryLanguage getQueryLanguage(final String value) {
        if (value == null) {
            return QueryLanguage.JDOQL;
        }
        if (QueryLanguage.JDOQL.toString().equalsIgnoreCase(value)) {
            return QueryLanguage.JDOQL;
        }
        if (QueryLanguage.SQL.toString().equalsIgnoreCase(value)) {
            return QueryLanguage.SQL;
        }
        if (QueryLanguage.JPQL.toString().equalsIgnoreCase(value)) {
            return QueryLanguage.JPQL;
        }
        if (QueryLanguage.STOREDPROC.toString().equalsIgnoreCase(value)) {
            return QueryLanguage.STOREDPROC;
        }
        return null;
    }
}
