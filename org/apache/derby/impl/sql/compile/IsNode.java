// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class IsNode extends BinaryLogicalOperatorNode
{
    private boolean notMe;
    
    public void init(final Object o, final Object o2, final Object o3) {
        super.init(o, o2, "is");
        this.notMe = (boolean)o3;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        super.bindExpression(list, list2, list3);
        this.leftOperand.checkIsBoolean();
        this.rightOperand.checkIsBoolean();
        this.setType(this.leftOperand.getTypeServices());
        return this;
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        if (b) {
            this.notMe = !this.notMe;
        }
        this.leftOperand = this.leftOperand.eliminateNots(false);
        this.rightOperand = this.rightOperand.eliminateNots(false);
        return this;
    }
    
    public ValueNode putAndsOnTop() throws StandardException {
        this.leftOperand = this.leftOperand.putAndsOnTop();
        this.rightOperand = this.rightOperand.putAndsOnTop();
        return this;
    }
    
    public boolean verifyPutAndsOnTop() {
        return this.leftOperand.verifyPutAndsOnTop() && this.rightOperand.verifyPutAndsOnTop();
    }
    
    public ValueNode changeToCNF(final boolean b) throws StandardException {
        this.leftOperand = this.leftOperand.changeToCNF(false);
        this.rightOperand = this.rightOperand.changeToCNF(false);
        return this;
    }
    
    public boolean verifyChangeToCNF() {
        return this.leftOperand.verifyChangeToCNF() && this.rightOperand.verifyChangeToCNF();
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        String s;
        if (this.notMe) {
            s = "isNot";
        }
        else {
            s = "is";
        }
        this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
        this.rightOperand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.BooleanDataValue", s, "org.apache.derby.iapi.types.BooleanDataValue", 1);
    }
}
