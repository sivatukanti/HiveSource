// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.List;

public class ExtractOperatorNode extends UnaryOperatorNode
{
    private static final String[] fieldName;
    private static final String[] fieldMethod;
    private int extractField;
    
    public void init(final Object o, final Object o2) {
        this.extractField = (int)o;
        super.init(o2, "EXTRACT " + ExtractOperatorNode.fieldName[this.extractField], ExtractOperatorNode.fieldMethod[this.extractField]);
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindOperand(list, list2, list3);
        TypeId typeId = this.operand.getTypeId();
        int n = typeId.getJDBCTypeId();
        if (typeId.isStringTypeId()) {
            this.operand = (ValueNode)this.getNodeFactory().getNode(60, this.operand, DataTypeDescriptor.getBuiltInDataTypeDescriptor((this.extractField < 3) ? 91 : 92, true, this.operand.getTypeCompiler().getCastToCharWidth(this.operand.getTypeServices())), this.getContextManager());
            ((CastNode)this.operand).bindCastNodeOnly();
            typeId = this.operand.getTypeId();
            n = typeId.getJDBCTypeId();
        }
        if (n != 91 && n != 92 && n != 93) {
            throw StandardException.newException("42X25", "EXTRACT " + ExtractOperatorNode.fieldName[this.extractField], typeId.getSQLTypeName());
        }
        if (n == 91 && this.extractField > 2) {
            throw StandardException.newException("42X25", "EXTRACT " + ExtractOperatorNode.fieldName[this.extractField], typeId.getSQLTypeName());
        }
        if (n == 92 && this.extractField < 3) {
            throw StandardException.newException("42X25", "EXTRACT " + ExtractOperatorNode.fieldName[this.extractField], typeId.getSQLTypeName());
        }
        if (n == 93 && this.extractField == 5) {
            this.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(8), this.operand.getTypeServices().isNullable()));
        }
        else {
            this.setType(new DataTypeDescriptor(TypeId.INTEGER_ID, this.operand.getTypeServices().isNullable()));
        }
        return this;
    }
    
    public String toString() {
        return "";
    }
    
    static {
        fieldName = new String[] { "YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND" };
        fieldMethod = new String[] { "getYear", "getMonth", "getDate", "getHours", "getMinutes", "getSeconds" };
    }
}
