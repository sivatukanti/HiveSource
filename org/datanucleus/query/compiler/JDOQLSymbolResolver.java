// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;

public class JDOQLSymbolResolver extends AbstractSymbolResolver
{
    public JDOQLSymbolResolver(final MetaDataManager mmgr, final ClassLoaderResolver clr, final SymbolTable symtbl, final Class cls, final String alias) {
        super(mmgr, clr, symtbl, cls, alias);
    }
    
    @Override
    public Class resolveClass(final String className) {
        final AbstractClassMetaData acmd = this.metaDataManager.getMetaDataForEntityName(className);
        if (acmd != null) {
            final String fullClassName = acmd.getFullClassName();
            if (fullClassName != null) {
                return this.clr.classForName(fullClassName);
            }
        }
        throw new ClassNotResolvedException("Class " + className + " for query has not been resolved. Check the query and any imports specification");
    }
    
    @Override
    public boolean caseSensitiveSymbolNames() {
        return true;
    }
    
    @Override
    public boolean supportsImplicitVariables() {
        return true;
    }
}
