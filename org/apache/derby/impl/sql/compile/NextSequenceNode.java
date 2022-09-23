// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.dictionary.PrivilegedSQLObject;
import org.apache.derby.iapi.sql.depend.Provider;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.SequenceDescriptor;

public class NextSequenceNode extends ValueNode
{
    private TableName sequenceName;
    private SequenceDescriptor sequenceDescriptor;
    
    public void init(final Object o) throws StandardException {
        this.sequenceName = (TableName)o;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3, final boolean b) throws StandardException {
        if (this.sequenceDescriptor != null) {
            return this;
        }
        final CompilerContext compilerContext = this.getCompilerContext();
        if ((compilerContext.getReliability() & 0x4000) != 0x0) {
            throw StandardException.newException("42XAH");
        }
        this.sequenceDescriptor = this.getDataDictionary().getSequenceDescriptor(this.getSchemaDescriptor(this.sequenceName.getSchemaName()), this.sequenceName.getTableName());
        if (this.sequenceDescriptor == null) {
            throw StandardException.newException("42X94", "SEQUENCE", this.sequenceName.getFullTableName());
        }
        this.setType(this.sequenceDescriptor.getDataType());
        if (compilerContext.isReferenced(this.sequenceDescriptor)) {
            throw StandardException.newException("42XAI", this.sequenceName.getFullTableName());
        }
        compilerContext.addReferencedSequence(this.sequenceDescriptor);
        this.getCompilerContext().createDependency(this.sequenceDescriptor);
        if (this.isPrivilegeCollectionRequired()) {
            this.getCompilerContext().addRequiredUsagePriv(this.sequenceDescriptor);
        }
        return this;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final String string = this.sequenceDescriptor.getUUID().toString();
        final int typeFormatId = this.sequenceDescriptor.getDataType().getNull().getTypeFormatId();
        methodBuilder.pushThis();
        methodBuilder.push(string);
        methodBuilder.push(typeFormatId);
        methodBuilder.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "getCurrentValueAndAdvance", "org.apache.derby.iapi.types.NumberDataValue", 2);
    }
    
    public void generateConstant(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        switch (this.getTypeServices().getJDBCTypeId()) {
            case 4: {
                methodBuilder.push(1);
                break;
            }
        }
    }
    
    public String toString() {
        return "";
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        return false;
    }
}
