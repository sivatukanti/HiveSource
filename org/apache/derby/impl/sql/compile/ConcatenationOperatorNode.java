// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.StringDataValue;

public class ConcatenationOperatorNode extends BinaryOperatorNode
{
    public void init(final Object o, final Object o2) {
        super.init(o, o2, "||", "concatenate", "org.apache.derby.iapi.types.ConcatableDataValue", "org.apache.derby.iapi.types.ConcatableDataValue");
    }
    
    ValueNode evaluateConstantExpressions() throws StandardException {
        if (this.leftOperand instanceof CharConstantNode && this.rightOperand instanceof CharConstantNode) {
            final CharConstantNode charConstantNode = (CharConstantNode)this.leftOperand;
            final CharConstantNode charConstantNode2 = (CharConstantNode)this.rightOperand;
            final StringDataValue stringDataValue = (StringDataValue)charConstantNode.getValue();
            final StringDataValue stringDataValue2 = (StringDataValue)charConstantNode2.getValue();
            final StringDataValue stringDataValue3 = (StringDataValue)this.getTypeServices().getNull();
            stringDataValue3.concatenate(stringDataValue, stringDataValue2, stringDataValue3);
            return (ValueNode)this.getNodeFactory().getNode(61, stringDataValue3.getString(), this.getContextManager());
        }
        return this;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.leftOperand = this.leftOperand.bindExpression(list, list2, list3);
        this.rightOperand = this.rightOperand.bindExpression(list, list2, list3);
        if (this.leftOperand.requiresTypeFromContext()) {
            if (this.rightOperand.requiresTypeFromContext()) {
                throw StandardException.newException("42X35", this.operator);
            }
            TypeId typeId;
            if (this.rightOperand.getTypeId().isBitTypeId()) {
                if (this.rightOperand.getTypeId().isBlobTypeId()) {
                    typeId = TypeId.getBuiltInTypeId(2004);
                }
                else {
                    typeId = TypeId.getBuiltInTypeId(-3);
                }
            }
            else if (this.rightOperand.getTypeId().isClobTypeId()) {
                typeId = TypeId.getBuiltInTypeId(2005);
            }
            else {
                typeId = TypeId.getBuiltInTypeId(12);
            }
            this.leftOperand.setType(new DataTypeDescriptor(typeId, true));
            if (this.rightOperand.getTypeId().isStringTypeId()) {
                this.leftOperand.setCollationInfo(this.rightOperand.getTypeServices());
            }
        }
        if (this.rightOperand.requiresTypeFromContext()) {
            TypeId typeId2;
            if (this.leftOperand.getTypeId().isBitTypeId()) {
                if (this.leftOperand.getTypeId().isBlobTypeId()) {
                    typeId2 = TypeId.getBuiltInTypeId(2004);
                }
                else {
                    typeId2 = TypeId.getBuiltInTypeId(-3);
                }
            }
            else if (this.leftOperand.getTypeId().isClobTypeId()) {
                typeId2 = TypeId.getBuiltInTypeId(2005);
            }
            else {
                typeId2 = TypeId.getBuiltInTypeId(12);
            }
            this.rightOperand.setType(new DataTypeDescriptor(typeId2, true));
            if (this.leftOperand.getTypeId().isStringTypeId()) {
                this.rightOperand.setCollationInfo(this.leftOperand.getTypeServices());
            }
        }
        if (this.leftOperand.getTypeId().userType()) {
            this.leftOperand = this.leftOperand.genSQLJavaSQLTree();
        }
        if (this.rightOperand.getTypeId().userType()) {
            this.rightOperand = this.rightOperand.genSQLJavaSQLTree();
        }
        final TypeCompiler typeCompiler = this.leftOperand.getTypeCompiler();
        if (!this.leftOperand.getTypeId().isStringTypeId() && !this.leftOperand.getTypeId().isBitTypeId()) {
            (this.leftOperand = (ValueNode)this.getNodeFactory().getNode(60, this.leftOperand, DataTypeDescriptor.getBuiltInDataTypeDescriptor(12, true, typeCompiler.getCastToCharWidth(this.leftOperand.getTypeServices())), this.getContextManager())).setCollationUsingCompilationSchema();
            ((CastNode)this.leftOperand).bindCastNodeOnly();
        }
        final TypeCompiler typeCompiler2 = this.rightOperand.getTypeCompiler();
        if (!this.rightOperand.getTypeId().isStringTypeId() && !this.rightOperand.getTypeId().isBitTypeId()) {
            (this.rightOperand = (ValueNode)this.getNodeFactory().getNode(60, this.rightOperand, DataTypeDescriptor.getBuiltInDataTypeDescriptor(12, true, typeCompiler2.getCastToCharWidth(this.rightOperand.getTypeServices())), this.getContextManager())).setCollationUsingCompilationSchema();
            ((CastNode)this.rightOperand).bindCastNodeOnly();
        }
        final TypeCompiler typeCompiler3 = this.leftOperand.getTypeCompiler();
        this.setType(this.resolveConcatOperation(this.leftOperand.getTypeServices(), this.rightOperand.getTypeServices()));
        this.setLeftRightInterfaceType(typeCompiler3.interfaceName());
        return this.evaluateConstantExpressions();
    }
    
    private DataTypeDescriptor resolveConcatOperation(final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2) throws StandardException {
        final TypeId typeId = dataTypeDescriptor.getTypeId();
        final TypeId typeId2 = dataTypeDescriptor2.getTypeId();
        if (!typeId.isConcatableTypeId() || !typeId2.isConcatableTypeId() || (typeId2.isBitTypeId() && typeId.isStringTypeId()) || (typeId.isBitTypeId() && typeId2.isStringTypeId())) {
            throw StandardException.newException("42884", "||", "FUNCTION");
        }
        String s = (typeId.typePrecedence() >= typeId2.typePrecedence()) ? dataTypeDescriptor.getTypeName() : dataTypeDescriptor2.getTypeName();
        int n = dataTypeDescriptor.getMaximumWidth() + dataTypeDescriptor2.getMaximumWidth();
        if (typeId.getJDBCTypeId() == 1 || typeId.getJDBCTypeId() == -2) {
            switch (typeId2.getJDBCTypeId()) {
                case -2:
                case 1: {
                    if (n <= 254) {
                        break;
                    }
                    if (typeId2.getJDBCTypeId() == 1) {
                        s = "VARCHAR";
                        break;
                    }
                    s = "VARCHAR () FOR BIT DATA";
                    break;
                }
                case -3:
                case 12: {
                    if (n <= 4000) {
                        break;
                    }
                    if (typeId2.getJDBCTypeId() == 12) {
                        s = "LONG VARCHAR";
                        break;
                    }
                    s = "LONG VARCHAR FOR BIT DATA";
                    break;
                }
                case 2004:
                case 2005: {
                    n = clobBlobHandling(dataTypeDescriptor2, dataTypeDescriptor);
                    break;
                }
            }
        }
        else if (typeId.getJDBCTypeId() == 12) {
            switch (typeId2.getJDBCTypeId()) {
                case 1:
                case 12: {
                    if (n > 4000) {
                        s = "LONG VARCHAR";
                        break;
                    }
                    break;
                }
                case 2005: {
                    n = clobBlobHandling(dataTypeDescriptor2, dataTypeDescriptor);
                    break;
                }
            }
        }
        else if (typeId.getJDBCTypeId() == -3) {
            switch (typeId2.getJDBCTypeId()) {
                case -3:
                case -2: {
                    if (n > 4000) {
                        s = "LONG VARCHAR FOR BIT DATA";
                        break;
                    }
                    break;
                }
                case 2004: {
                    n = clobBlobHandling(dataTypeDescriptor2, dataTypeDescriptor);
                    break;
                }
            }
        }
        else if (typeId.getJDBCTypeId() == 2005 || typeId.getJDBCTypeId() == 2004) {
            n = clobBlobHandling(dataTypeDescriptor, dataTypeDescriptor2);
        }
        else if (typeId2.getJDBCTypeId() == 2005 || typeId2.getJDBCTypeId() == 2004) {
            n = clobBlobHandling(dataTypeDescriptor2, dataTypeDescriptor);
        }
        if (s.equals("LONG VARCHAR")) {
            n = 32700;
        }
        else if (s.equals("LONG VARCHAR FOR BIT DATA")) {
            n = 32700;
        }
        final DataTypeDescriptor dataTypeDescriptor3 = new DataTypeDescriptor(TypeId.getBuiltInTypeId(s), dataTypeDescriptor.isNullable() || dataTypeDescriptor2.isNullable(), n);
        DataTypeDescriptor dataTypeDescriptor4;
        if (dataTypeDescriptor.getCollationDerivation() != dataTypeDescriptor2.getCollationDerivation() || dataTypeDescriptor.getCollationType() != dataTypeDescriptor2.getCollationType()) {
            dataTypeDescriptor4 = dataTypeDescriptor3.getCollatedType(dataTypeDescriptor3.getCollationDerivation(), 0);
        }
        else {
            dataTypeDescriptor4 = dataTypeDescriptor3.getCollatedType(dataTypeDescriptor.getCollationType(), dataTypeDescriptor.getCollationDerivation());
        }
        return dataTypeDescriptor4;
    }
    
    private static int clobBlobHandling(final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2) throws StandardException {
        int n;
        if (dataTypeDescriptor2.getTypeId().getJDBCTypeId() == -1 || dataTypeDescriptor2.getTypeId().getJDBCTypeId() == -4) {
            n = dataTypeDescriptor.getMaximumWidth() + 32768;
        }
        else {
            n = dataTypeDescriptor.getMaximumWidth() + dataTypeDescriptor2.getMaximumWidth();
        }
        if (n < 1) {
            return Integer.MAX_VALUE;
        }
        return n;
    }
}
