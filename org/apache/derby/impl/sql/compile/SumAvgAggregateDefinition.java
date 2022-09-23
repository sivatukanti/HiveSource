// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public class SumAvgAggregateDefinition implements AggregateDefinition
{
    private boolean isSum;
    
    public final DataTypeDescriptor getAggregator(final DataTypeDescriptor dataTypeDescriptor, final StringBuffer sb) {
        try {
            final TypeId typeId = dataTypeDescriptor.getTypeId();
            final TypeCompiler typeCompiler = ((CompilerContext)ContextService.getContext("CompilerContext")).getTypeCompilerFactory().getTypeCompiler(typeId);
            if (typeId.isNumericTypeId()) {
                sb.append(this.getAggregatorClassName());
                return typeCompiler.resolveArithmeticOperation(dataTypeDescriptor, dataTypeDescriptor, this.getOperator()).getNullabilityType(true);
            }
        }
        catch (StandardException ex) {}
        return null;
    }
    
    private String getAggregatorClassName() {
        if (this.isSum) {
            return "org.apache.derby.impl.sql.execute.SumAggregator";
        }
        return "org.apache.derby.impl.sql.execute.AvgAggregator";
    }
    
    protected String getOperator() {
        if (this.isSum) {
            return "sum";
        }
        return "avg";
    }
    
    public final void setSumOrAvg(final boolean isSum) {
        this.isSum = isSum;
    }
}
