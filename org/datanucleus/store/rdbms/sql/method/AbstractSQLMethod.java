// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.util.Localiser;

public abstract class AbstractSQLMethod implements SQLMethod
{
    protected static final Localiser LOCALISER;
    protected SQLStatement stmt;
    protected SQLExpressionFactory exprFactory;
    protected ClassLoaderResolver clr;
    
    @Override
    public void setStatement(final SQLStatement stmt) {
        this.stmt = stmt;
        this.exprFactory = stmt.getSQLExpressionFactory();
        if (stmt.getQueryGenerator() == null) {
            throw new NucleusException("Attempt to use SQLMethod with an SQLStatement which doesn't have a QueryGenerator assigned");
        }
        this.clr = stmt.getQueryGenerator().getClassLoaderResolver();
    }
    
    protected JavaTypeMapping getMappingForClass(final Class cls) {
        return this.exprFactory.getMappingForType(cls, true);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
