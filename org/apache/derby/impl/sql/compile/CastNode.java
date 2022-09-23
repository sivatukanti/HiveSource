// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.types.NumberDataType;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.types.DataTypeUtilities;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;

public class CastNode extends ValueNode
{
    ValueNode castOperand;
    private int targetCharType;
    TypeId sourceCTI;
    private boolean forDataTypeFunction;
    private boolean externallyGeneratedCastNode;
    private boolean assignmentSemantics;
    
    public CastNode() {
        this.sourceCTI = null;
        this.forDataTypeFunction = false;
        this.externallyGeneratedCastNode = false;
        this.assignmentSemantics = false;
    }
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.castOperand = (ValueNode)o;
        this.setType((DataTypeDescriptor)o2);
    }
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        this.castOperand = (ValueNode)o;
        final int intValue = (int)o3;
        this.targetCharType = (int)o2;
        if (intValue < 0) {
            return;
        }
        this.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(this.targetCharType, intValue));
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return this.castOperand.getOrderableVariantType();
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.castOperand = this.castOperand.bindExpression(list, list2, list3);
        if (this.getTypeServices() == null) {
            final DataTypeDescriptor typeServices = this.castOperand.getTypeServices();
            int n = -1;
            final TypeId typeId = typeServices.getTypeId();
            if (typeServices != null) {
                if (typeId.isNumericTypeId()) {
                    n = typeServices.getPrecision() + 1;
                    if (typeServices.getScale() > 0) {
                        ++n;
                    }
                }
                else if (typeId.isStringTypeId()) {
                    n = typeServices.getMaximumWidth();
                    if (this.targetCharType == 1) {
                        n = Math.min(n, 254);
                    }
                    else if (this.targetCharType == 12) {
                        n = Math.min(n, 32672);
                    }
                }
                else {
                    final TypeId typeId2 = typeServices.getTypeId();
                    if (n < 0) {
                        n = DataTypeUtilities.getColumnDisplaySize(typeId2.getJDBCTypeId(), -1);
                    }
                }
            }
            if (n < 0) {
                n = 1;
            }
            this.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(this.targetCharType, n));
        }
        if (this.castOperand instanceof UntypedNullConstantNode) {
            this.castOperand.setType(this.getTypeServices());
        }
        this.bindCastNodeOnly();
        if (this.castOperand instanceof ConstantNode && !(this.castOperand instanceof UntypedNullConstantNode)) {
            ValueNode valueNode = this;
            final int jdbcTypeId = this.sourceCTI.getJDBCTypeId();
            final int jdbcTypeId2 = this.getTypeId().getJDBCTypeId();
            switch (jdbcTypeId) {
                case -7:
                case 16: {
                    if (jdbcTypeId2 == -7 || jdbcTypeId2 == 16) {
                        valueNode = this.castOperand;
                        break;
                    }
                    if (jdbcTypeId2 == 1) {
                        valueNode = (ValueNode)this.getNodeFactory().getNode(61, ((BooleanConstantNode)this.castOperand).getValueAsString(), ReuseFactory.getInteger(this.getTypeServices().getMaximumWidth()), this.getContextManager());
                        break;
                    }
                    break;
                }
                case 1: {
                    valueNode = this.getCastFromCharConstant(jdbcTypeId2);
                    break;
                }
                case 91:
                case 92:
                case 93: {
                    if (jdbcTypeId2 == 1) {
                        valueNode = (ValueNode)this.getNodeFactory().getNode(61, ((UserTypeConstantNode)this.castOperand).getObjectValue().toString(), ReuseFactory.getInteger(this.getTypeServices().getMaximumWidth()), this.getContextManager());
                        break;
                    }
                    break;
                }
                case 3: {
                    if (jdbcTypeId2 == 3) {
                        break;
                    }
                    if (jdbcTypeId2 == 2) {
                        break;
                    }
                }
                case -6:
                case -5:
                case 4:
                case 5:
                case 7:
                case 8: {
                    valueNode = this.getCastFromNumericType(((ConstantNode)this.castOperand).getValue(), jdbcTypeId2);
                    break;
                }
            }
            return valueNode;
        }
        return this;
    }
    
    public void bindCastNodeOnly() throws StandardException {
        this.sourceCTI = this.castOperand.getTypeId();
        if (this.externallyGeneratedCastNode && this.getTypeId().isStringTypeId()) {
            this.setCollationUsingCompilationSchema();
        }
        if (this.getTypeId().userType()) {
            this.setType(this.bindUserType(this.getTypeServices()));
            this.verifyClassExist(this.getTypeId().getCorrespondingJavaTypeName());
        }
        if (this.castOperand.requiresTypeFromContext()) {
            this.castOperand.setType(this.getTypeServices());
        }
        else if (!(this.castOperand instanceof UntypedNullConstantNode) && !this.castOperand.getTypeCompiler().convertible(this.getTypeId(), this.forDataTypeFunction)) {
            throw StandardException.newException("42846", this.sourceCTI.getSQLTypeName(), this.getTypeId().getSQLTypeName());
        }
        if (this.castOperand.getTypeServices().getTypeId().isStringTypeId() && this.getTypeId().isBooleanTypeId()) {
            this.setNullability(true);
        }
        else {
            this.setNullability(this.castOperand.getTypeServices().isNullable());
        }
    }
    
    private ValueNode getCastFromCharConstant(final int n) throws StandardException {
        final String sqlToUpperCase = StringUtil.SQLToUpperCase(((CharConstantNode)this.castOperand).getString().trim());
        switch (n) {
            case -7:
            case 16: {
                if (sqlToUpperCase.equals("TRUE")) {
                    return (ValueNode)this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager());
                }
                if (sqlToUpperCase.equals("FALSE")) {
                    return (ValueNode)this.getNodeFactory().getNode(38, Boolean.FALSE, this.getContextManager());
                }
                if (sqlToUpperCase.equals("UNKNOWN")) {
                    return (ValueNode)this.getNodeFactory().getNode(38, null, this.getContextManager());
                }
                throw StandardException.newException("22018", "boolean");
            }
            case 91: {
                return (ValueNode)this.getNodeFactory().getNode(76, this.getDataValueFactory().getDateValue(sqlToUpperCase, false), this.getContextManager());
            }
            case 93: {
                return (ValueNode)this.getNodeFactory().getNode(76, this.getDataValueFactory().getTimestampValue(sqlToUpperCase, false), this.getContextManager());
            }
            case 92: {
                return (ValueNode)this.getNodeFactory().getNode(76, this.getDataValueFactory().getTimeValue(sqlToUpperCase, false), this.getContextManager());
            }
            case -6:
            case -5:
            case 4:
            case 5: {
                try {
                    return this.getCastFromIntegralType(new Double(sqlToUpperCase).longValue(), n);
                }
                catch (NumberFormatException ex) {
                    throw StandardException.newException("22018", TypeId.getBuiltInTypeId(n).getSQLTypeName());
                }
            }
            case 7: {
                Float value;
                try {
                    value = Float.valueOf(sqlToUpperCase);
                }
                catch (NumberFormatException ex2) {
                    throw StandardException.newException("22018", "float");
                }
                return (ValueNode)this.getNodeFactory().getNode(69, value, this.getContextManager());
            }
            case 8: {
                Double n2;
                try {
                    n2 = new Double(sqlToUpperCase);
                }
                catch (NumberFormatException ex3) {
                    throw StandardException.newException("22018", "double");
                }
                return (ValueNode)this.getNodeFactory().getNode(68, n2, this.getContextManager());
            }
            default: {
                return this;
            }
        }
    }
    
    private ValueNode getCastFromIntegralType(final long n, final int n2) throws StandardException {
        switch (n2) {
            case 1: {
                return (ValueNode)this.getNodeFactory().getNode(61, Long.toString(n), ReuseFactory.getInteger(this.getTypeServices().getMaximumWidth()), this.getContextManager());
            }
            case -6: {
                if (n < -128L || n > 127L) {
                    throw StandardException.newException("22003", "TINYINT");
                }
                return (ValueNode)this.getNodeFactory().getNode(75, ReuseFactory.getByte((byte)n), this.getContextManager());
            }
            case 5: {
                if (n < -32768L || n > 32767L) {
                    throw StandardException.newException("22003", "SHORT");
                }
                return (ValueNode)this.getNodeFactory().getNode(74, ReuseFactory.getShort((short)n), this.getContextManager());
            }
            case 4: {
                if (n < -2147483648L || n > 2147483647L) {
                    throw StandardException.newException("22003", "INTEGER");
                }
                return (ValueNode)this.getNodeFactory().getNode(70, ReuseFactory.getInteger((int)n), this.getContextManager());
            }
            case -5: {
                return (ValueNode)this.getNodeFactory().getNode(71, ReuseFactory.getLong(n), this.getContextManager());
            }
            case 7: {
                if (Math.abs(n) > Float.MAX_VALUE) {
                    throw StandardException.newException("22003", "REAL");
                }
                return (ValueNode)this.getNodeFactory().getNode(69, new Float((float)n), this.getContextManager());
            }
            case 8: {
                return (ValueNode)this.getNodeFactory().getNode(68, new Double((double)n), this.getContextManager());
            }
            default: {
                return this;
            }
        }
    }
    
    private ValueNode getCastFromNumericType(final DataValueDescriptor dataValueDescriptor, final int n) throws StandardException {
        int n2 = -1;
        Object o = null;
        switch (n) {
            case 1: {
                return (ValueNode)this.getNodeFactory().getNode(61, dataValueDescriptor.getString(), ReuseFactory.getInteger(this.getTypeServices().getMaximumWidth()), this.getContextManager());
            }
            case -6: {
                n2 = 75;
                o = new Byte(dataValueDescriptor.getByte());
                break;
            }
            case 5: {
                n2 = 74;
                o = ReuseFactory.getShort(dataValueDescriptor.getShort());
                break;
            }
            case 4: {
                n2 = 70;
                o = ReuseFactory.getInteger(dataValueDescriptor.getInt());
                break;
            }
            case -5: {
                n2 = 71;
                o = ReuseFactory.getLong(dataValueDescriptor.getLong());
                break;
            }
            case 7: {
                n2 = 69;
                o = new Float(NumberDataType.normalizeREAL(dataValueDescriptor.getDouble()));
                break;
            }
            case 8: {
                n2 = 68;
                o = new Double(dataValueDescriptor.getDouble());
                break;
            }
        }
        if (n2 == -1) {
            return this;
        }
        return (ValueNode)this.getNodeFactory().getNode(n2, o, this.getContextManager());
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        this.castOperand = this.castOperand.preprocess(n, list, list2, list3);
        return this;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        return this.castOperand.categorize(set, b);
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        this.castOperand = this.castOperand.remapColumnReferencesToExpressions();
        return this;
    }
    
    public boolean isConstantExpression() {
        return this.castOperand.isConstantExpression();
    }
    
    public boolean constantExpression(final PredicateList list) {
        return this.castOperand.constantExpression(list);
    }
    
    Object getConstantValueAsObject() throws StandardException {
        final Object constantValueAsObject = this.castOperand.getConstantValueAsObject();
        if (constantValueAsObject == null) {
            return null;
        }
        if (this.sourceCTI.getCorrespondingJavaTypeName().equals(this.getTypeId().getCorrespondingJavaTypeName())) {
            return constantValueAsObject;
        }
        return null;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.castOperand.generateExpression(expressionClassBuilder, methodBuilder);
        if (this.castOperand instanceof UntypedNullConstantNode) {
            return;
        }
        if (this.castOperand.requiresTypeFromContext()) {
            this.sourceCTI = this.getTypeId();
        }
        this.genDataValueConversion(expressionClassBuilder, methodBuilder);
    }
    
    private void genDataValueConversion(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, this.getTypeCompiler().interfaceName());
        expressionClassBuilder.generateNull(constructor, this.getTypeCompiler(this.getTypeId()), this.getTypeServices().getCollationType());
        constructor.setField(fieldDeclaration);
        if (!this.sourceCTI.userType() && !this.getTypeId().userType()) {
            methodBuilder.getField(fieldDeclaration);
            methodBuilder.swap();
            methodBuilder.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
            methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.DataValueDescriptor", "setValue", "void", 1);
        }
        else {
            methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.DataValueDescriptor", "getObject", "java.lang.Object", 0);
            methodBuilder.getField(fieldDeclaration);
            methodBuilder.swap();
            final String correspondingJavaTypeName = this.getTypeId().getCorrespondingJavaTypeName();
            methodBuilder.dup();
            methodBuilder.isInstanceOf(correspondingJavaTypeName);
            methodBuilder.push(correspondingJavaTypeName);
            methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.DataValueDescriptor", "setObjectForCast", "void", 3);
        }
        methodBuilder.getField(fieldDeclaration);
        if (this.getTypeId().variableLength()) {
            final boolean numericTypeId = this.getTypeId().isNumericTypeId();
            methodBuilder.dup();
            methodBuilder.push(numericTypeId ? this.getTypeServices().getPrecision() : this.getTypeServices().getMaximumWidth());
            methodBuilder.push(this.getTypeServices().getScale());
            methodBuilder.push(!this.sourceCTI.variableLength() || numericTypeId || this.assignmentSemantics);
            methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.VariableSizeDataValue", "setWidth", "void", 3);
        }
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.castOperand != null) {
            this.castOperand = (ValueNode)this.castOperand.accept(visitor);
        }
    }
    
    void setForExternallyGeneratedCASTnode() {
        this.externallyGeneratedCastNode = true;
    }
    
    void setForDataTypeFunction(final boolean forDataTypeFunction) {
        this.forDataTypeFunction = forDataTypeFunction;
    }
    
    void setAssignmentSemantics() {
        this.assignmentSemantics = true;
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        if (this.isSameNodeType(valueNode)) {
            final CastNode castNode = (CastNode)valueNode;
            return this.getTypeServices().equals(castNode.getTypeServices()) && this.castOperand.isEquivalent(castNode.castOperand);
        }
        return false;
    }
}
