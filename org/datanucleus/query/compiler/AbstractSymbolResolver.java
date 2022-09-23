// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import java.lang.reflect.Field;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.List;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.query.symbol.SymbolResolver;

public abstract class AbstractSymbolResolver implements SymbolResolver
{
    protected MetaDataManager metaDataManager;
    protected ClassLoaderResolver clr;
    protected SymbolTable symtbl;
    protected Class candidateClass;
    protected String candidateAlias;
    
    public AbstractSymbolResolver(final MetaDataManager mmgr, final ClassLoaderResolver clr, final SymbolTable symtbl, final Class cls, final String alias) {
        this.metaDataManager = mmgr;
        this.clr = clr;
        this.symtbl = symtbl;
        this.candidateClass = cls;
        this.candidateAlias = alias;
    }
    
    @Override
    public Class getType(final List tuples) {
        Class type = null;
        Symbol symbol = null;
        final String firstTuple = tuples.get(0);
        if (this.caseSensitiveSymbolNames()) {
            symbol = this.symtbl.getSymbol(firstTuple);
        }
        else {
            symbol = this.symtbl.getSymbol(firstTuple);
            if (symbol == null) {
                symbol = this.symtbl.getSymbol(firstTuple.toUpperCase());
            }
            if (symbol == null) {
                symbol = this.symtbl.getSymbol(firstTuple.toLowerCase());
            }
        }
        if (symbol != null) {
            type = symbol.getValueType();
            if (type == null) {
                throw new NucleusUserException("Cannot find type of " + (Object)tuples.get(0) + " since symbol has no type; implicit variable?");
            }
            for (int i = 1; i < tuples.size(); ++i) {
                type = this.getType(type, tuples.get(i));
            }
        }
        else {
            symbol = this.symtbl.getSymbol(this.candidateAlias);
            type = symbol.getValueType();
            for (int i = 0; i < tuples.size(); ++i) {
                type = this.getType(type, tuples.get(i));
            }
        }
        return type;
    }
    
    Class getType(final Class cls, final String fieldName) {
        final AbstractClassMetaData acmd = this.metaDataManager.getMetaDataForClass(cls, this.clr);
        if (acmd != null) {
            final AbstractMemberMetaData fmd = acmd.getMetaDataForMember(fieldName);
            if (fmd == null) {
                throw new NucleusUserException("Cannot access field " + fieldName + " on type " + cls.getName());
            }
            return fmd.getType();
        }
        else {
            final Field field = ClassUtils.getFieldForClass(cls, fieldName);
            if (field == null) {
                throw new NucleusUserException("Cannot access field " + fieldName + " on type " + cls.getName());
            }
            return field.getType();
        }
    }
    
    @Override
    public Class getPrimaryClass() {
        return this.candidateClass;
    }
}
