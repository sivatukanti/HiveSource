// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.conn;

import org.apache.derby.iapi.sql.compile.Parser;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.compile.TypeCompilerFactory;
import org.apache.derby.iapi.sql.compile.OptimizerFactory;
import org.apache.derby.iapi.services.property.PropertyFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.iapi.services.compiler.JavaFactory;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.sql.LanguageFactory;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.Statement;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

public interface LanguageConnectionFactory
{
    public static final String MODULE = "org.apache.derby.iapi.sql.conn.LanguageConnectionFactory";
    
    Statement getStatement(final SchemaDescriptor p0, final String p1, final boolean p2);
    
    LanguageConnectionContext newLanguageConnectionContext(final ContextManager p0, final TransactionController p1, final LanguageFactory p2, final Database p3, final String p4, final String p5, final String p6) throws StandardException;
    
    UUIDFactory getUUIDFactory();
    
    ClassFactory getClassFactory();
    
    JavaFactory getJavaFactory();
    
    NodeFactory getNodeFactory();
    
    ExecutionFactory getExecutionFactory();
    
    PropertyFactory getPropertyFactory();
    
    OptimizerFactory getOptimizerFactory();
    
    TypeCompilerFactory getTypeCompilerFactory();
    
    DataValueFactory getDataValueFactory();
    
    CacheManager getStatementCache();
    
    Parser newParser(final CompilerContext p0);
}
