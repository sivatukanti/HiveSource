// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;

public class DropAliasNode extends DDLStatementNode
{
    private char aliasType;
    private char nameSpace;
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.initAndCheck(o);
        switch (this.aliasType = (char)o2) {
            case 'G': {
                this.nameSpace = 'G';
                break;
            }
            case 'P': {
                this.nameSpace = 'P';
                break;
            }
            case 'F': {
                this.nameSpace = 'F';
                break;
            }
            case 'S': {
                this.nameSpace = 'S';
                break;
            }
            case 'A': {
                this.nameSpace = 'A';
                break;
            }
        }
    }
    
    public char getAliasType() {
        return this.aliasType;
    }
    
    public String statementToString() {
        return "DROP ".concat(aliasTypeName(this.aliasType));
    }
    
    public void bindStatement() throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final String relativeName = this.getRelativeName();
        AliasDescriptor aliasDescriptor = null;
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor();
        if (schemaDescriptor.getUUID() != null) {
            aliasDescriptor = dataDictionary.getAliasDescriptor(schemaDescriptor.getUUID().toString(), relativeName, this.nameSpace);
        }
        if (aliasDescriptor == null) {
            throw StandardException.newException("42Y55", this.statementToString(), relativeName);
        }
        if (aliasDescriptor.getSystemAlias()) {
            throw StandardException.newException("42Y71", relativeName);
        }
        this.getCompilerContext().createDependency(aliasDescriptor);
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getDropAliasConstantAction(this.getSchemaDescriptor(), this.getRelativeName(), this.nameSpace);
    }
    
    private static String aliasTypeName(final char c) {
        String s = null;
        switch (c) {
            case 'G': {
                s = "DERBY AGGREGATE";
                break;
            }
            case 'P': {
                s = "PROCEDURE";
                break;
            }
            case 'F': {
                s = "FUNCTION";
                break;
            }
            case 'S': {
                s = "SYNONYM";
                break;
            }
            case 'A': {
                s = "TYPE";
                break;
            }
        }
        return s;
    }
}
