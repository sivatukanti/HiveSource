// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.sql.dictionary.SPSDescriptor;

public class ExecSPSNode extends StatementNode
{
    private TableName name;
    private SPSDescriptor spsd;
    private ExecPreparedStatement ps;
    
    public void init(final Object o) {
        this.name = (TableName)o;
    }
    
    public void bindStatement() throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final String schemaName = this.name.getSchemaName();
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(this.name.getSchemaName());
        if (schemaName == null) {
            this.name.setSchemaName(schemaDescriptor.getSchemaName());
        }
        if (schemaDescriptor.getUUID() != null) {
            this.spsd = dataDictionary.getSPSDescriptor(this.name.getTableName(), schemaDescriptor);
        }
        if (this.spsd == null) {
            throw StandardException.newException("42X94", "STATEMENT", this.name);
        }
        final char type = this.spsd.getType();
        final SPSDescriptor spsd = this.spsd;
        if (type == 'T') {
            throw StandardException.newException("42Y41", this.name);
        }
        this.getCompilerContext().createDependency(this.spsd);
    }
    
    public boolean isAtomic() {
        return this.ps.isAtomic();
    }
    
    public GeneratedClass generate(final ByteArray byteArray) throws StandardException {
        if (!this.spsd.isValid()) {
            this.getLanguageConnectionContext().commitNestedTransaction();
            this.getLanguageConnectionContext().beginNestedTransaction(true);
        }
        this.ps = this.spsd.getPreparedStatement();
        this.getCompilerContext().setSavedObjects(this.ps.getSavedObjects());
        this.getCompilerContext().setCursorInfo(this.ps.getCursorInfo());
        return this.ps.getActivationClass();
    }
    
    public ResultDescription makeResultDescription() {
        return this.ps.getResultDescription();
    }
    
    public Object getCursorInfo() {
        return this.ps.getCursorInfo();
    }
    
    public DataTypeDescriptor[] getParameterTypes() throws StandardException {
        return this.spsd.getParams();
    }
    
    public ConstantAction makeConstantAction() {
        return this.ps.getConstantAction();
    }
    
    public boolean needsSavepoint() {
        return this.ps.needsSavepoint();
    }
    
    public String executeStatementName() {
        return this.name.getTableName();
    }
    
    public String executeSchemaName() {
        return this.name.getSchemaName();
    }
    
    public String getSPSName() {
        return this.spsd.getQualifiedName();
    }
    
    int activationKind() {
        return 2;
    }
    
    public String statementToString() {
        return "EXECUTE STATEMENT";
    }
}
