// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.types.TypeId;

public final class BooleanConstantNode extends ConstantNode
{
    boolean booleanValue;
    boolean unknownValue;
    
    public void init(final Object o) throws StandardException {
        if (o == null) {
            super.init(TypeId.BOOLEAN_ID, Boolean.TRUE, ReuseFactory.getInteger(1));
            this.setValue(null);
        }
        else if (o instanceof Boolean) {
            super.init(TypeId.BOOLEAN_ID, Boolean.FALSE, ReuseFactory.getInteger(1));
            this.booleanValue = (boolean)o;
            super.setValue(new SQLBoolean(this.booleanValue));
        }
        else {
            super.init(o, Boolean.TRUE, ReuseFactory.getInteger(0));
            this.unknownValue = true;
        }
    }
    
    Object getConstantValueAsObject() {
        return this.booleanValue ? Boolean.TRUE : Boolean.FALSE;
    }
    
    String getValueAsString() {
        if (this.booleanValue) {
            return "true";
        }
        return "false";
    }
    
    boolean isBooleanTrue() {
        return this.booleanValue && !this.unknownValue;
    }
    
    boolean isBooleanFalse() {
        return !this.booleanValue && !this.unknownValue;
    }
    
    public double selectivity(final Optimizable optimizable) {
        if (this.isBooleanTrue()) {
            return 1.0;
        }
        return 0.0;
    }
    
    ValueNode eliminateNots(final boolean b) {
        if (!b) {
            return this;
        }
        this.booleanValue = !this.booleanValue;
        super.setValue(new SQLBoolean(this.booleanValue));
        return this;
    }
    
    void generateConstant(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) {
        methodBuilder.push(this.booleanValue);
    }
    
    public void setValue(final DataValueDescriptor value) {
        super.setValue(value);
        this.unknownValue = true;
        try {
            if (value != null && value.isNotNull().getBoolean()) {
                this.booleanValue = value.getBoolean();
                this.unknownValue = false;
            }
        }
        catch (StandardException ex) {}
    }
}
