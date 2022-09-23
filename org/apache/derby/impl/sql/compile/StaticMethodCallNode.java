// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.lang.reflect.Method;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.iapi.types.JSQLType;
import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.iapi.services.compiler.LocalField;

public class StaticMethodCallNode extends MethodCallNode
{
    private TableName procedureName;
    private LocalField[] outParamArrays;
    private int[] applicationParameterNumbers;
    private boolean isSystemCode;
    private boolean isInsideBind;
    private LocalField returnsNullOnNullState;
    private String routineDefiner;
    AliasDescriptor ad;
    private AggregateNode resolvedAggregate;
    private boolean appearsInGroupBy;
    
    public StaticMethodCallNode() {
        this.routineDefiner = null;
        this.appearsInGroupBy = false;
    }
    
    public void init(final Object o, final Object o2) {
        if (o instanceof String) {
            this.init(o);
        }
        else {
            this.procedureName = (TableName)o;
            this.init(this.procedureName.getTableName());
        }
        this.javaClassName = (String)o2;
    }
    
    public AggregateNode getResolvedAggregate() {
        return this.resolvedAggregate;
    }
    
    public void setAppearsInGroupBy() {
        this.appearsInGroupBy = true;
    }
    
    public JavaValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        if (this.isInsideBind) {
            return this;
        }
        this.isInsideBind = true;
        try {
            return this.bindExpressionMinion(list, list2, list3);
        }
        finally {
            this.isInsideBind = false;
        }
    }
    
    private JavaValueNode bindExpressionMinion(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindParameters(list, list2, list3);
        if (this.javaClassName == null) {
            final CompilerContext compilerContext = this.getCompilerContext();
            final String schemaName = this.procedureName.getSchemaName();
            final boolean b = schemaName == null;
            SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(schemaName, schemaName != null);
            this.resolveRoutine(list, list2, list3, schemaDescriptor);
            if (this.ad != null && this.ad.getAliasType() == 'G') {
                this.resolvedAggregate = (AggregateNode)this.getNodeFactory().getNode(115, ((SQLToJavaValueNode)this.methodParms[0]).getSQLValueNode(), new UserAggregateDefinition(this.ad), Boolean.FALSE, this.ad.getJavaClassName(), this.getContextManager());
                if (this.appearsInGroupBy) {
                    throw StandardException.newException("42Y26");
                }
                return this;
            }
            else {
                final SchemaDescriptor schemaDescriptor2 = schemaDescriptor;
                if (this.ad == null && b && !this.forCallStatement) {
                    schemaDescriptor = this.getSchemaDescriptor("SYSFUN", true);
                    this.resolveRoutine(list, list2, list3, schemaDescriptor);
                }
                if (this.ad == null) {
                    schemaDescriptor = schemaDescriptor2;
                    if (!this.forCallStatement) {
                        this.forCallStatement = true;
                        this.resolveRoutine(list, list2, list3, schemaDescriptor);
                        this.forCallStatement = false;
                        if (this.ad != null) {
                            throw StandardException.newException("42Y03.S.3", this.procedureName);
                        }
                    }
                    else {
                        this.forCallStatement = false;
                        this.resolveRoutine(list, list2, list3, schemaDescriptor);
                        this.forCallStatement = true;
                        if (this.ad != null) {
                            throw StandardException.newException("42Y03.S.4", this.procedureName);
                        }
                    }
                }
                if (this.ad == null) {
                    throw StandardException.newException("42Y03.S.0", this.procedureName);
                }
                if (!this.routineInfo.isDeterministic()) {
                    this.checkReliability(this.getMethodName(), 4096);
                }
                if (this.permitsSQL(this.routineInfo)) {
                    this.checkReliability(this.getMethodName(), 8192);
                }
                compilerContext.createDependency(this.ad);
                this.methodName = this.ad.getAliasInfo().getMethodName();
                this.javaClassName = this.ad.getJavaClassName();
                if (this.javaClassName.startsWith("org.apache.derby.") && !this.javaClassName.startsWith("org.apache.derby.impl.tools.optional.") && !this.javaClassName.startsWith("org.apache.derby.vti.") && !schemaDescriptor.isSystemSchema()) {
                    throw StandardException.newException("42X51", null, this.javaClassName);
                }
            }
        }
        this.verifyClassExist(this.javaClassName);
        this.resolveMethodCall(this.javaClassName, true);
        if (this.isPrivilegeCollectionRequired()) {
            this.getCompilerContext().addRequiredRoutinePriv(this.ad);
        }
        if (this.routineInfo != null) {
            if (this.methodParms != null) {
                this.optimizeDomainValueConversion();
            }
            final TypeDescriptor returnType = this.routineInfo.getReturnType();
            if (returnType != null) {
                this.createTypeDependency(DataTypeDescriptor.getType(returnType));
            }
            if (returnType != null && !returnType.isRowMultiSet() && !returnType.isUserDefinedType()) {
                final TypeId builtInTypeId = TypeId.getBuiltInTypeId(returnType.getJDBCTypeId());
                if (builtInTypeId.variableLength()) {
                    final ValueNode valueNode = (ValueNode)this.getNodeFactory().getNode(60, this.getNodeFactory().getNode(36, this, this.getContextManager()), new DataTypeDescriptor(builtInTypeId, returnType.getPrecision(), returnType.getScale(), returnType.isNullable(), returnType.getMaximumWidth()), this.getContextManager());
                    valueNode.setCollationInfo(returnType.getCollationType(), 1);
                    final JavaValueNode javaValueNode = (JavaValueNode)this.getNodeFactory().getNode(28, valueNode, this.getContextManager());
                    javaValueNode.setCollationType(returnType.getCollationType());
                    return javaValueNode.bindExpression(list, list2, list3);
                }
            }
        }
        return this;
    }
    
    private boolean permitsSQL(final RoutineAliasInfo routineAliasInfo) {
        switch (routineAliasInfo.getSQLAllowed()) {
            case 0:
            case 1:
            case 2: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void optimizeDomainValueConversion() throws StandardException {
        if (!this.routineInfo.calledOnNullInput()) {
            return;
        }
        for (int length = this.methodParms.length, i = 0; i < length; ++i) {
            if (this.methodParms == null || !this.methodParms[i].mustCastToPrimitive()) {
                if (this.methodParms[i] instanceof SQLToJavaValueNode && ((SQLToJavaValueNode)this.methodParms[i]).getSQLValueNode() instanceof JavaToSQLValueNode) {
                    final JavaValueNode javaValueNode = ((JavaToSQLValueNode)((SQLToJavaValueNode)this.methodParms[i]).getSQLValueNode()).getJavaValueNode();
                    if (javaValueNode instanceof StaticMethodCallNode) {
                        final StaticMethodCallNode staticMethodCallNode = (StaticMethodCallNode)javaValueNode;
                        if (staticMethodCallNode.routineInfo != null && staticMethodCallNode.routineInfo.calledOnNullInput()) {
                            this.methodParms[i] = ((JavaToSQLValueNode)((SQLToJavaValueNode)this.methodParms[i]).getSQLValueNode()).getJavaValueNode();
                        }
                    }
                }
            }
        }
    }
    
    private void resolveRoutine(final FromList list, final SubqueryList list2, final List list3, final SchemaDescriptor schemaDescriptor) throws StandardException {
        if (schemaDescriptor.getUUID() != null) {
            final List routineList = this.getDataDictionary().getRoutineList(schemaDescriptor.getUUID().toString(), this.methodName, this.forCallStatement ? 'P' : 'F');
            int i = routineList.size() - 1;
            while (i >= 0) {
                final AliasDescriptor ad = routineList.get(i);
                final RoutineAliasInfo routineInfo = (RoutineAliasInfo)ad.getAliasInfo();
                final int parameterCount = routineInfo.getParameterCount();
                final boolean hasVarargs = routineInfo.hasVarargs();
                Label_0132: {
                    if (hasVarargs) {
                        if (this.methodParms.length >= parameterCount - 1) {
                            break Label_0132;
                        }
                    }
                    else if (parameterCount == this.methodParms.length) {
                        break Label_0132;
                    }
                    --i;
                    continue;
                }
                final TypeDescriptor[] parameterTypes = routineInfo.getParameterTypes();
                int n = parameterCount;
                if (routineInfo.getMaxDynamicResultSets() > 0) {
                    ++n;
                }
                this.signature = new JSQLType[n];
                for (int j = 0; j < parameterCount; ++j) {
                    final TypeDescriptor typeDescriptor = parameterTypes[j];
                    final TypeId typeId;
                    TypeId userDefinedTypeId = typeId = TypeId.getTypeId(typeDescriptor);
                    final int n2 = routineInfo.getParameterModes()[this.getRoutineArgIdx(routineInfo, j)];
                    if (n2 != 1) {
                        String s = null;
                        switch (userDefinedTypeId.getJDBCTypeId()) {
                            case -5:
                            case 4:
                            case 5:
                            case 7:
                            case 8:
                            case 16: {
                                s = this.getTypeCompiler(userDefinedTypeId).getCorrespondingPrimitiveTypeName().concat("[]");
                                break;
                            }
                            default: {
                                s = userDefinedTypeId.getCorrespondingJavaTypeName().concat("[]");
                                break;
                            }
                        }
                        userDefinedTypeId = TypeId.getUserDefinedTypeId(s);
                    }
                    this.signature[j] = new JSQLType(new DataTypeDescriptor(userDefinedTypeId, typeDescriptor.getPrecision(), typeDescriptor.getScale(), typeDescriptor.isNullable(), typeDescriptor.getMaximumWidth()));
                    final DataTypeDescriptor dataTypeDescriptor = new DataTypeDescriptor(typeId, typeDescriptor.getPrecision(), typeDescriptor.getScale(), typeDescriptor.isNullable(), typeDescriptor.getMaximumWidth());
                    if (hasVarargs && j == parameterCount - 1) {
                        for (int k = j; k < this.methodParms.length; ++k) {
                            this.coerceMethodParameter(list, list2, list3, routineInfo, this.methodParms.length, dataTypeDescriptor, typeId, n2, k);
                        }
                    }
                    else {
                        this.coerceMethodParameter(list, list2, list3, routineInfo, this.methodParms.length, dataTypeDescriptor, typeId, n2, j);
                    }
                }
                if (n != parameterCount) {
                    this.signature[parameterCount] = new JSQLType(new DataTypeDescriptor(TypeId.getUserDefinedTypeId("java.sql.ResultSet[]"), 0, 0, false, -1));
                }
                this.routineInfo = routineInfo;
                this.ad = ad;
                if (schemaDescriptor.isSystemSchema() && this.routineInfo.getReturnType() == null && this.routineInfo.getSQLAllowed() != 3) {
                    this.isSystemCode = true;
                }
                this.routineDefiner = schemaDescriptor.getAuthorizationId();
                break;
            }
        }
        if (this.ad == null && this.methodParms.length == 1) {
            this.ad = AggregateNode.resolveAggregate(this.getDataDictionary(), schemaDescriptor, this.methodName);
        }
    }
    
    private void coerceMethodParameter(final FromList list, final SubqueryList list2, final List list3, final RoutineAliasInfo routineAliasInfo, final int n, final DataTypeDescriptor dataTypeDescriptor, final TypeId typeId, final int n2, final int n3) throws StandardException {
        ValueNode sqlValueNode = null;
        if (this.methodParms[n3] instanceof SQLToJavaValueNode) {
            sqlValueNode = ((SQLToJavaValueNode)this.methodParms[n3]).getSQLValueNode();
        }
        boolean b = true;
        if (sqlValueNode == null || !sqlValueNode.requiresTypeFromContext()) {
            if (n2 != 1) {
                throw StandardException.newException("42886", RoutineAliasInfo.parameterMode(n2), routineAliasInfo.getParameterNames()[n3]);
            }
            b = false;
        }
        else {
            if (this.applicationParameterNumbers == null) {
                this.applicationParameterNumbers = new int[n];
            }
            if (sqlValueNode instanceof UnaryOperatorNode) {
                this.applicationParameterNumbers[n3] = ((UnaryOperatorNode)sqlValueNode).getParameterOperand().getParameterNumber();
            }
            else {
                this.applicationParameterNumbers[n3] = ((ParameterNode)sqlValueNode).getParameterNumber();
            }
        }
        boolean b2 = false;
        if (!b) {
            if (sqlValueNode instanceof UntypedNullConstantNode) {
                sqlValueNode.setType(dataTypeDescriptor);
            }
            else {
                TypeId typeId2;
                DataTypeDescriptor dataTypeDescriptor2;
                if (sqlValueNode != null) {
                    typeId2 = sqlValueNode.getTypeId();
                    dataTypeDescriptor2 = sqlValueNode.getTypeServices();
                }
                else {
                    dataTypeDescriptor2 = DataTypeDescriptor.getSQLDataTypeDescriptor(this.methodParms[n3].getJavaTypeName());
                    if (dataTypeDescriptor2 == null) {
                        throw StandardException.newException("X0X57.S", this.methodParms[n3].getJavaTypeName());
                    }
                    typeId2 = dataTypeDescriptor2.getTypeId();
                }
                if (!this.getTypeCompiler(typeId).storable(typeId2, this.getClassFactory())) {
                    throw StandardException.newException("42821", typeId.getSQLTypeName(), typeId2.getSQLTypeName());
                }
                if (!dataTypeDescriptor.isExactTypeAndLengthMatch(dataTypeDescriptor2)) {
                    b2 = true;
                }
            }
        }
        else if (typeId.variableLength() && n2 != 4) {
            b2 = true;
        }
        if (b2) {
            if (sqlValueNode == null) {
                sqlValueNode = (ValueNode)this.getNodeFactory().getNode(36, this.methodParms[n3], this.getContextManager());
            }
            this.methodParms[n3] = (JavaValueNode)this.getNodeFactory().getNode(28, makeCast(sqlValueNode, dataTypeDescriptor, this.getNodeFactory(), this.getContextManager()), this.getContextManager());
            this.methodParms[n3] = this.methodParms[n3].bindExpression(list, list2, list3);
        }
        if (b) {
            sqlValueNode.setType(dataTypeDescriptor);
        }
    }
    
    public static ValueNode makeCast(final ValueNode valueNode, final DataTypeDescriptor dataTypeDescriptor, final NodeFactory nodeFactory, final ContextManager contextManager) throws StandardException {
        final ValueNode valueNode2 = (ValueNode)nodeFactory.getNode(60, valueNode, dataTypeDescriptor, contextManager);
        ((CastNode)valueNode2).setAssignmentSemantics();
        return valueNode2;
    }
    
    private void generateSetupNestedSessionContext(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder, final boolean b, final String s) throws StandardException {
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.callMethod((short)185, null, "getLanguageConnectionContext", "org.apache.derby.iapi.sql.conn.LanguageConnectionContext", 0);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(b);
        methodBuilder.push(s);
        methodBuilder.callMethod((short)185, null, "setupNestedSessionContext", "void", 3);
    }
    
    public void generateOneParameter(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final int n) throws StandardException {
        SQLToJavaValueNode sqlToJavaValueNode = null;
        if (this.methodParms[n] instanceof SQLToJavaValueNode) {
            sqlToJavaValueNode = (SQLToJavaValueNode)this.methodParms[n];
        }
        int n2;
        if (this.routineInfo != null) {
            n2 = this.routineInfo.getParameterModes()[this.getRoutineArgIdx(n)];
        }
        else {
            n2 = 1;
            if (sqlToJavaValueNode != null && sqlToJavaValueNode.getSQLValueNode().requiresTypeFromContext()) {
                ParameterNode parameterOperand;
                if (sqlToJavaValueNode.getSQLValueNode() instanceof UnaryOperatorNode) {
                    parameterOperand = ((UnaryOperatorNode)sqlToJavaValueNode.getSQLValueNode()).getParameterOperand();
                }
                else {
                    parameterOperand = (ParameterNode)sqlToJavaValueNode.getSQLValueNode();
                }
                final int parameterNumber = parameterOperand.getParameterNumber();
                if (this.methodParameterTypes[this.getRoutineArgIdx(n)].endsWith("[]")) {
                    final MethodBuilder constructor = expressionClassBuilder.getConstructor();
                    expressionClassBuilder.pushThisAsActivation(constructor);
                    constructor.callMethod((short)185, null, "getParameterValueSet", "org.apache.derby.iapi.sql.ParameterValueSet", 0);
                    constructor.push(parameterNumber);
                    constructor.push(0);
                    constructor.callMethod((short)185, null, "setParameterMode", "void", 2);
                    constructor.endStatement();
                }
            }
        }
        switch (n2) {
            case 0:
            case 1:
            case 2: {
                if (sqlToJavaValueNode != null) {
                    sqlToJavaValueNode.returnsNullOnNullState = this.returnsNullOnNullState;
                }
                super.generateOneParameter(expressionClassBuilder, methodBuilder, n);
                break;
            }
        }
        switch (n2) {
            case 2:
            case 4: {
                String stripOneArrayLevel = this.methodParameterTypes[this.getRoutineArgIdx(n)];
                String s = stripOneArrayLevel.substring(0, stripOneArrayLevel.length() - 2);
                if (this.isVararg(n)) {
                    stripOneArrayLevel = this.stripOneArrayLevel(stripOneArrayLevel);
                    s = this.stripOneArrayLevel(s);
                }
                final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, stripOneArrayLevel);
                if (this.outParamArrays == null) {
                    this.outParamArrays = new LocalField[this.methodParms.length];
                }
                this.outParamArrays[n] = fieldDeclaration;
                methodBuilder.pushNewArray(s, 1);
                methodBuilder.putField(fieldDeclaration);
                if (n2 != 4) {
                    methodBuilder.swap();
                    methodBuilder.setArrayElement(0);
                    methodBuilder.getField(fieldDeclaration);
                    break;
                }
                break;
            }
        }
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        return !b && true && super.categorize(set, b);
    }
    
    public String toString() {
        return "";
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.routineInfo != null && !this.routineInfo.calledOnNullInput() && this.routineInfo.getParameterCount() != 0) {
            this.returnsNullOnNullState = expressionClassBuilder.newFieldDeclaration(2, "boolean");
        }
        if (this.returnsNullOnNullState != null) {
            methodBuilder.push(false);
            methodBuilder.setField(this.returnsNullOnNullState);
            methodBuilder.pushThis();
        }
        int generateParameters = this.generateParameters(expressionClassBuilder, methodBuilder);
        LocalField fieldDeclaration = null;
        if (this.routineInfo != null) {
            final short sqlAllowed = this.routineInfo.getSQLAllowed();
            if (sqlAllowed != 3) {
                int n;
                if (sqlAllowed == 1) {
                    n = 1;
                }
                else if (sqlAllowed == 0) {
                    n = 0;
                }
                else {
                    n = 2;
                }
                this.generateAuthorizeCheck((ActivationClassBuilder)expressionClassBuilder, methodBuilder, n);
            }
            int n2 = this.isSystemCode ? 2 : 1;
            final boolean b = this.routineInfo.getReturnType() != null;
            if (b) {
                ++n2;
            }
            if (n2 != 0) {
                expressionClassBuilder.pushThisAsActivation(methodBuilder);
                methodBuilder.callMethod((short)185, null, "getLanguageConnectionContext", "org.apache.derby.iapi.sql.conn.LanguageConnectionContext", 0);
                methodBuilder.callMethod((short)185, null, "getStatementContext", "org.apache.derby.iapi.sql.conn.StatementContext", 0);
                for (int i = 1; i < n2; ++i) {
                    methodBuilder.dup();
                }
            }
            if (this.isSystemCode) {
                methodBuilder.callMethod((short)185, null, "setSystemCode", "void", 0);
            }
            if (sqlAllowed != 3) {
                this.generateSetupNestedSessionContext((ActivationClassBuilder)expressionClassBuilder, methodBuilder, this.routineInfo.hasDefinersRights(), this.routineDefiner);
            }
            if (b) {
                fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, "short");
                methodBuilder.callMethod((short)185, null, "getSQLAllowed", "short", 0);
                methodBuilder.setField(fieldDeclaration);
            }
            methodBuilder.push(sqlAllowed);
            methodBuilder.push(false);
            methodBuilder.callMethod((short)185, null, "setSQLAllowed", "void", 2);
        }
        if (this.routineInfo != null && !this.hasVarargs()) {
            final int n3 = this.methodParameterTypes.length - this.methodParms.length;
            if (n3 != 0) {
                final int maxDynamicResultSets = this.routineInfo.getMaxDynamicResultSets();
                if (maxDynamicResultSets > 0) {
                    final MethodBuilder methodBuilder2 = expressionClassBuilder.getClassBuilder().newMethodBuilder(1, "int", "getMaxDynamicResults");
                    methodBuilder2.push(maxDynamicResultSets);
                    methodBuilder2.methodReturn();
                    methodBuilder2.complete();
                }
                final MethodBuilder methodBuilder3 = expressionClassBuilder.getClassBuilder().newMethodBuilder(1, "java.sql.ResultSet[][]", "getDynamicResults");
                final MethodBuilder constructor = expressionClassBuilder.getConstructor();
                final LocalField fieldDeclaration2 = expressionClassBuilder.newFieldDeclaration(2, "java.sql.ResultSet[][]");
                methodBuilder3.getField(fieldDeclaration2);
                constructor.pushNewArray("java.sql.ResultSet[]", n3);
                constructor.setField(fieldDeclaration2);
                for (int j = 0; j < n3; ++j) {
                    methodBuilder.pushNewArray("java.sql.ResultSet", 1);
                    methodBuilder.dup();
                    methodBuilder.getField(fieldDeclaration2);
                    methodBuilder.swap();
                    methodBuilder.setArrayElement(j);
                }
                methodBuilder3.methodReturn();
                methodBuilder3.complete();
                generateParameters += n3;
            }
        }
        final String javaTypeName = this.getJavaTypeName();
        MethodBuilder generatedFun = null;
        MethodBuilder methodBuilder4 = methodBuilder;
        if (this.returnsNullOnNullState != null) {
            generatedFun = expressionClassBuilder.newGeneratedFun(javaTypeName, 2, this.methodParameterTypes);
            final Class<?>[] exceptionTypes = ((Method)this.method).getExceptionTypes();
            for (int k = 0; k < exceptionTypes.length; ++k) {
                generatedFun.addThrownException(exceptionTypes[k].getName());
            }
            generatedFun.getField(this.returnsNullOnNullState);
            generatedFun.conditionalIf();
            generatedFun.pushNull(javaTypeName);
            generatedFun.startElseCode();
            if (!this.actualMethodReturnType.equals(javaTypeName)) {
                generatedFun.pushNewStart(javaTypeName);
            }
            for (int l = 0; l < generateParameters; ++l) {
                generatedFun.getParameter(l);
            }
            methodBuilder4 = generatedFun;
        }
        methodBuilder4.callMethod((short)184, this.method.getDeclaringClass().getName(), this.methodName, this.actualMethodReturnType, generateParameters);
        if (this.returnsNullOnNullState != null) {
            if (!this.actualMethodReturnType.equals(javaTypeName)) {
                if (this.actualMethodReturnType.equals("short") && javaTypeName.equals("java.lang.Integer")) {
                    generatedFun.upCast("int");
                }
                generatedFun.pushNewComplete(1);
            }
            generatedFun.completeConditional();
            generatedFun.methodReturn();
            generatedFun.complete();
            methodBuilder.callMethod((short)182, expressionClassBuilder.getClassBuilder().getFullName(), generatedFun.getName(), javaTypeName, generateParameters);
        }
        if (this.routineInfo != null) {
            if (fieldDeclaration != null) {
                expressionClassBuilder.pushThisAsActivation(methodBuilder);
                methodBuilder.callMethod((short)185, null, "getLanguageConnectionContext", "org.apache.derby.iapi.sql.conn.LanguageConnectionContext", 0);
                methodBuilder.callMethod((short)185, null, "getStatementContext", "org.apache.derby.iapi.sql.conn.StatementContext", 0);
                methodBuilder.getField(fieldDeclaration);
                methodBuilder.push(true);
                methodBuilder.callMethod((short)185, null, "setSQLAllowed", "void", 2);
            }
            if (this.outParamArrays != null) {
                final MethodBuilder constructor2 = expressionClassBuilder.getConstructor();
                expressionClassBuilder.pushThisAsActivation(constructor2);
                constructor2.callMethod((short)185, null, "getParameterValueSet", "org.apache.derby.iapi.sql.ParameterValueSet", 0);
                expressionClassBuilder.pushThisAsActivation(methodBuilder);
                methodBuilder.callMethod((short)185, null, "getParameterValueSet", "org.apache.derby.iapi.sql.ParameterValueSet", 0);
                final int[] parameterModes = this.routineInfo.getParameterModes();
                for (int n4 = 0; n4 < this.outParamArrays.length; ++n4) {
                    final int n5 = parameterModes[this.getRoutineArgIdx(n4)];
                    if (n5 != 1) {
                        final ValueNode sqlValueNode = ((SQLToJavaValueNode)this.methodParms[n4]).getSQLValueNode();
                        final int n6 = this.applicationParameterNumbers[n4];
                        constructor2.dup();
                        constructor2.push(n6);
                        constructor2.push(n5);
                        constructor2.callMethod((short)185, null, "setParameterMode", "void", 2);
                        final LocalField localField = this.outParamArrays[n4];
                        methodBuilder.dup();
                        methodBuilder.push(n6);
                        methodBuilder.callMethod((short)185, null, "getParameter", "org.apache.derby.iapi.types.DataValueDescriptor", 1);
                        final DataTypeDescriptor typeServices = sqlValueNode.getTypeServices();
                        final boolean numericTypeId = typeServices.getTypeId().isNumericTypeId();
                        final boolean ansiUDT = typeServices.getTypeId().getBaseTypeId().isAnsiUDT();
                        Class<?> clazz = ((Method)this.method).getParameterTypes()[this.getRoutineArgIdx(n4)].getComponentType();
                        if (this.isVararg(n4)) {
                            clazz = (Class<?>)clazz.getComponentType();
                        }
                        final boolean primitive = clazz.isPrimitive();
                        if (numericTypeId) {
                            if (!primitive) {
                                methodBuilder.cast("org.apache.derby.iapi.types.NumberDataValue");
                            }
                        }
                        else if (typeServices.getTypeId().isBooleanTypeId() && !primitive) {
                            methodBuilder.cast("org.apache.derby.iapi.types.BooleanDataValue");
                        }
                        if (typeServices.getTypeId().variableLength()) {
                            methodBuilder.dup();
                        }
                        methodBuilder.getField(localField);
                        methodBuilder.getArrayElement(0);
                        if (numericTypeId && !primitive) {
                            methodBuilder.upCast("java.lang.Number");
                        }
                        if (ansiUDT) {
                            methodBuilder.upCast("java.lang.Object");
                        }
                        methodBuilder.callMethod((short)185, null, "setValue", "void", 1);
                        if (typeServices.getTypeId().variableLength()) {
                            methodBuilder.push(numericTypeId ? typeServices.getPrecision() : typeServices.getMaximumWidth());
                            methodBuilder.push(typeServices.getScale());
                            methodBuilder.push(numericTypeId);
                            methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.VariableSizeDataValue", "setWidth", "void", 3);
                        }
                    }
                }
                constructor2.endStatement();
                methodBuilder.endStatement();
            }
        }
    }
    
    int getPrivType() {
        return 6;
    }
}
