// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public class MaxMinAggregateDefinition implements AggregateDefinition
{
    private boolean isMax;
    
    public final DataTypeDescriptor getAggregator(final DataTypeDescriptor dataTypeDescriptor, final StringBuffer sb) {
        final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext");
        final DataTypeDescriptor nullabilityType = dataTypeDescriptor.getNullabilityType(true);
        if (nullabilityType.getTypeId().orderable(languageConnectionContext.getLanguageConnectionFactory().getClassFactory())) {
            sb.append("org.apache.derby.impl.sql.execute.MaxMinAggregator");
            return nullabilityType;
        }
        return null;
    }
    
    public final void setMaxOrMin(final boolean isMax) {
        this.isMax = isMax;
    }
    
    public final boolean isMax() {
        return this.isMax;
    }
}
