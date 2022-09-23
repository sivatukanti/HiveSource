// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.UnboundExpression;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.ExecutionContext;
import org.datanucleus.ClassLoaderResolver;

public interface QueryGenerator
{
    String getQueryLanguage();
    
    ClassLoaderResolver getClassLoaderResolver();
    
    ExecutionContext getExecutionContext();
    
    CompilationComponent getCompilationComponent();
    
    Object getProperty(final String p0);
    
    void useParameterExpressionAsLiteral(final SQLLiteral p0);
    
    Class getTypeOfVariable(final String p0);
    
    void bindVariable(final String p0, final AbstractClassMetaData p1, final SQLTable p2, final JavaTypeMapping p3);
    
    SQLExpression bindVariable(final UnboundExpression p0, final Class p1);
    
    boolean hasExplicitJoins();
    
    void bindParameter(final String p0, final Class p1);
    
    SQLTable getSQLTableForAlias(final String p0);
    
    Class resolveClass(final String p0);
    
    boolean hasExtension(final String p0);
    
    Object getValueForExtension(final String p0);
}
