// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;

public class ValueNodeList extends QueryTreeNodeVector
{
    public void addValueNode(final ValueNode valueNode) throws StandardException {
        this.addElement(valueNode);
    }
    
    public void bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            this.setElementAt(((ValueNode)this.elementAt(i)).bindExpression(list, list2, list3), i);
        }
    }
    
    public void genSQLJavaSQLTrees() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ValueNode valueNode = (ValueNode)this.elementAt(i);
            if (valueNode.getTypeId().userType()) {
                this.setElementAt(valueNode.genSQLJavaSQLTree(), i);
            }
        }
    }
    
    public DataTypeDescriptor getDominantTypeServices() throws StandardException {
        DataTypeDescriptor dataTypeDescriptor = null;
        int collationDerivation = -1;
        int collationType = -1;
        int n = 0;
        for (int i = 0; i < this.size(); ++i) {
            final ValueNode valueNode = (ValueNode)this.elementAt(i);
            if (!valueNode.requiresTypeFromContext()) {
                final DataTypeDescriptor typeServices = valueNode.getTypeServices();
                if (typeServices.getTypeId().isStringTypeId()) {
                    if (collationDerivation == -1) {
                        collationDerivation = typeServices.getCollationDerivation();
                        collationType = typeServices.getCollationType();
                    }
                    else if (n == 0) {
                        if (collationDerivation != typeServices.getCollationDerivation()) {
                            n = 1;
                        }
                        else if (collationType != typeServices.getCollationType()) {
                            n = 1;
                        }
                    }
                }
                if (dataTypeDescriptor == null) {
                    dataTypeDescriptor = typeServices;
                }
                else {
                    dataTypeDescriptor = dataTypeDescriptor.getDominantType(typeServices, this.getClassFactory());
                }
            }
        }
        if (collationDerivation != -1 && n != 0) {
            dataTypeDescriptor = dataTypeDescriptor.getCollatedType(dataTypeDescriptor.getCollationType(), 0);
        }
        return dataTypeDescriptor;
    }
    
    public DataTypeDescriptor getTypeServices() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final DataTypeDescriptor typeServices = ((ValueNode)this.elementAt(i)).getTypeServices();
            if (typeServices != null) {
                return typeServices;
            }
        }
        return null;
    }
    
    boolean allSamePrecendence(final int n) {
        final boolean b = true;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final DataTypeDescriptor typeServices = ((ValueNode)this.elementAt(i)).getTypeServices();
            if (typeServices == null) {
                return false;
            }
            if (n != typeServices.getTypeId().typePrecedence()) {
                return false;
            }
        }
        return b;
    }
    
    public void compatible(final ValueNode valueNode) throws StandardException {
        final int size = this.size();
        final TypeId typeId = valueNode.getTypeId();
        final TypeCompiler typeCompiler = valueNode.getTypeCompiler();
        for (int i = 0; i < size; ++i) {
            final ValueNode valueNode2 = (ValueNode)this.elementAt(i);
            if (!valueNode2.requiresTypeFromContext()) {
                if (!typeCompiler.compatible(valueNode2.getTypeId())) {
                    throw StandardException.newException("42815.S.171", typeId.getSQLTypeName(), valueNode2.getTypeId().getSQLTypeName());
                }
            }
        }
    }
    
    public void comparable(final ValueNode valueNode) throws StandardException {
        final int size = this.size();
        valueNode.getTypeId();
        for (int i = 0; i < size; ++i) {
            final ValueNode valueNode2 = (ValueNode)this.elementAt(i);
            if (!valueNode.getTypeServices().comparable(valueNode2.getTypeServices(), false, this.getClassFactory())) {
                throw StandardException.newException("42818", valueNode.getTypeServices().getSQLTypeNameWithCollation(), valueNode2.getTypeServices().getSQLTypeNameWithCollation());
            }
        }
    }
    
    public boolean isNullable() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((ValueNode)this.elementAt(i)).getTypeServices().isNullable()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsParameterNode() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((ValueNode)this.elementAt(i)).requiresTypeFromContext()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsAllParameterNodes() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (!((ValueNode)this.elementAt(i)).requiresTypeFromContext()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean containsAllConstantNodes() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (!(((ValueNode)this.elementAt(i)) instanceof ConstantNode)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean containsOnlyConstantAndParamNodes() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ValueNode valueNode = (ValueNode)this.elementAt(i);
            if (!valueNode.requiresTypeFromContext() && !(valueNode instanceof ConstantNode)) {
                return false;
            }
        }
        return true;
    }
    
    void sortInAscendingOrder(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final int size = this.size();
        int i = 1;
        while (i != 0) {
            i = 0;
            for (int j = 1; j < size; ++j) {
                final ConstantNode constantNode = (ConstantNode)this.elementAt(j);
                final DataValueDescriptor value = constantNode.getValue();
                final ConstantNode constantNode2 = (ConstantNode)this.elementAt(j - 1);
                final DataValueDescriptor value2 = constantNode2.getValue();
                if ((dataValueDescriptor == null && value2.compare(value) > 0) || (dataValueDescriptor != null && dataValueDescriptor.greaterThan(value2, value).equals(true))) {
                    this.setElementAt(constantNode, j - 1);
                    this.setElementAt(constantNode2, j);
                    i = 1;
                }
            }
        }
    }
    
    public void setParameterDescriptor(final DataTypeDescriptor type) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ValueNode valueNode = (ValueNode)this.elementAt(i);
            if (valueNode.requiresTypeFromContext()) {
                valueNode.setType(type);
            }
        }
    }
    
    public void preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ValueNode)this.elementAt(i)).preprocess(n, list, list2, list3);
        }
    }
    
    public ValueNodeList remapColumnReferencesToExpressions() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            this.setElementAt(((ValueNode)this.elementAt(i)).remapColumnReferencesToExpressions(), i);
        }
        return this;
    }
    
    boolean isEquivalent(final ValueNodeList list) throws StandardException {
        if (this.size() != list.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); ++i) {
            if (!((ValueNode)this.elementAt(i)).isEquivalent((ValueNode)list.elementAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isConstantExpression() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final boolean constantExpression = ((ValueNode)this.elementAt(i)).isConstantExpression();
            if (!constantExpression) {
                return constantExpression;
            }
        }
        return true;
    }
    
    public boolean constantExpression(final PredicateList list) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final boolean constantExpression = ((ValueNode)this.elementAt(i)).constantExpression(list);
            if (!constantExpression) {
                return constantExpression;
            }
        }
        return true;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        boolean b2 = true;
        for (int size = this.size(), i = 0; i < size; ++i) {
            b2 = (((ValueNode)this.elementAt(i)).categorize(set, b) && b2);
        }
        return b2;
    }
    
    protected int getOrderableVariantType() throws StandardException {
        int min = 3;
        for (int size = this.size(), i = 0; i < size; ++i) {
            min = Math.min(min, ((ValueNode)this.elementAt(i)).getOrderableVariantType());
        }
        return min;
    }
}
