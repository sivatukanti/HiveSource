// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.sql.ResultSet;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.sql.compile.TypeCompilerFactory;
import java.util.StringTokenizer;
import org.apache.derby.catalog.types.UserDefinedTypeIdImpl;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.catalog.types.TypeDescriptorImpl;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.Iterator;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;
import java.lang.reflect.Member;
import org.apache.derby.iapi.types.JSQLType;
import org.apache.derby.catalog.types.RoutineAliasInfo;

abstract class MethodCallNode extends JavaValueNode
{
    String methodName;
    String javaClassName;
    RoutineAliasInfo routineInfo;
    boolean internalCall;
    private String[] procedurePrimitiveArrayType;
    protected JSQLType[] signature;
    protected JavaValueNode[] methodParms;
    protected Member method;
    protected String actualMethodReturnType;
    String[] methodParameterTypes;
    
    public void init(final Object o) {
        this.methodName = (String)o;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public String getJavaClassName() {
        return this.javaClassName;
    }
    
    public Member getResolvedMethod() {
        return this.method;
    }
    
    public RoutineAliasInfo getRoutineInfo() {
        return this.routineInfo;
    }
    
    public void addParms(final List list) throws StandardException {
        this.methodParms = new JavaValueNode[list.size()];
        for (int size = list.size(), i = 0; i < size; ++i) {
            QueryTreeNode queryTreeNode = list.get(i);
            if (!(queryTreeNode instanceof JavaValueNode)) {
                queryTreeNode = (QueryTreeNode)this.getNodeFactory().getNode(28, queryTreeNode, this.getContextManager());
            }
            this.methodParms[i] = (JavaValueNode)queryTreeNode;
        }
    }
    
    public Class[] getMethodParameterClasses() {
        final ClassInspector classInspector = this.getClassFactory().getClassInspector();
        final Class[] array = new Class[this.methodParms.length];
        for (int i = 0; i < this.methodParms.length; ++i) {
            final String s = this.methodParameterTypes[i];
            try {
                array[i] = classInspector.getClass(s);
            }
            catch (ClassNotFoundException ex) {
                return null;
            }
        }
        return array;
    }
    
    void getCorrelationTables(final JBitSet set) throws StandardException {
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class);
        this.accept(collectNodesVisitor);
        for (final ColumnReference columnReference : collectNodesVisitor.getList()) {
            if (columnReference.getCorrelated()) {
                set.set(columnReference.getTableNumber());
            }
        }
    }
    
    public void printSubNodes(final int n) {
    }
    
    public String toString() {
        return "";
    }
    
    final void bindParameters(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        if (this.methodParms != null) {
            final int length = this.methodParms.length;
            if (this.signature == null) {
                this.signature = new JSQLType[length];
            }
            for (int i = 0; i < length; ++i) {
                if (this.methodParms[i] != null) {
                    this.methodParms[i] = this.methodParms[i].bindExpression(list, list2, list3);
                    if (this.routineInfo == null) {
                        this.signature[i] = this.methodParms[i].getJSQLType();
                    }
                }
            }
        }
    }
    
    protected boolean areParametersQueryInvariant() throws StandardException {
        return this.getVariantTypeOfParams() == 2;
    }
    
    void throwNoMethodFound(final String s, final String[] array, final String[] array2) throws StandardException {
        final StringBuffer sb = new StringBuffer();
        this.hasVarargs();
        this.getFirstVarargIdx();
        for (int length = this.signature.length, i = 0; i < length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            final boolean vararg = this.isVararg(i);
            String varargTypeName = array[i];
            if (array[i].length() == 0) {
                varargTypeName = "UNTYPED";
            }
            else if (vararg) {
                varargTypeName = this.getVarargTypeName(varargTypeName);
            }
            sb.append(varargTypeName);
            if (array2 != null && !array2[i].equals(array[i])) {
                String varargTypeName2 = array2[i];
                if (vararg) {
                    varargTypeName2 = this.getVarargTypeName(varargTypeName2);
                }
                sb.append("(" + varargTypeName2 + ")");
            }
        }
        throw StandardException.newException("42X50", s, this.methodName, sb);
    }
    
    private String getVarargTypeName(final String s) {
        return this.stripOneArrayLevel(s) + "...";
    }
    
    public void preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        if (this.methodParms != null) {
            for (int i = 0; i < this.methodParms.length; ++i) {
                if (this.methodParms[i] != null) {
                    this.methodParms[i].preprocess(n, list, list2, list3);
                }
            }
        }
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        if (b) {
            return false;
        }
        boolean b2 = true;
        if (this.methodParms != null) {
            for (int i = 0; i < this.methodParms.length; ++i) {
                if (this.methodParms[i] != null) {
                    b2 = (this.methodParms[i].categorize(set, b) && b2);
                }
            }
        }
        return b2;
    }
    
    public JavaValueNode remapColumnReferencesToExpressions() throws StandardException {
        if (this.methodParms != null) {
            for (int i = 0; i < this.methodParms.length; ++i) {
                if (this.methodParms[i] != null) {
                    this.methodParms[i] = this.methodParms[i].remapColumnReferencesToExpressions();
                }
            }
        }
        return this;
    }
    
    public boolean hasVarargs() {
        return this.routineInfo != null && this.routineInfo.hasVarargs();
    }
    
    public int getFirstVarargIdx() {
        return this.signature.length - 1;
    }
    
    public boolean isVararg(final int n) {
        return this.hasVarargs() && n >= this.getFirstVarargIdx();
    }
    
    public int generateParameters(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int n = this.hasVarargs() ? (this.routineInfo.getParameterCount() - 1) : this.methodParms.length;
        final int n2 = this.hasVarargs() ? (n + 1) : n;
        for (int i = 0; i < n; ++i) {
            this.generateAndCastOneParameter(expressionClassBuilder, methodBuilder, i, this.methodParameterTypes[i]);
        }
        if (this.hasVarargs()) {
            this.generateVarargs(expressionClassBuilder, methodBuilder);
        }
        return n2;
    }
    
    private void generateAndCastOneParameter(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final int n, final String s) throws StandardException {
        this.getClassFactory().getClassInspector();
        this.generateOneParameter(expressionClassBuilder, methodBuilder, n);
        if (!s.equals(getParameterTypeName(this.methodParms[n]))) {
            if (ClassInspector.primitiveType(s)) {
                methodBuilder.cast(s);
            }
            else {
                if (this.routineInfo != null) {
                    return;
                }
                methodBuilder.upCast(s);
            }
        }
    }
    
    private void generateVarargs(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int firstVarargIdx = this.getFirstVarargIdx();
        final String s = this.methodParameterTypes[firstVarargIdx];
        final String stripOneArrayLevel;
        final String s2 = stripOneArrayLevel = this.stripOneArrayLevel(s);
        if (this.routineInfo != null && this.routineInfo.getParameterModes()[firstVarargIdx] != 1) {
            this.stripOneArrayLevel(stripOneArrayLevel);
        }
        int n = this.methodParms.length - firstVarargIdx;
        if (n < 0) {
            n = 0;
        }
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, s);
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        constructor.pushNewArray(s2, n);
        constructor.setField(fieldDeclaration);
        for (int i = 0; i < n; ++i) {
            methodBuilder.getField(fieldDeclaration);
            this.generateAndCastOneParameter(expressionClassBuilder, methodBuilder, i + firstVarargIdx, s2);
            methodBuilder.setArrayElement(i);
        }
        methodBuilder.getField(fieldDeclaration);
    }
    
    protected int getRoutineArgIdx(final int n) {
        if (this.routineInfo == null) {
            return n;
        }
        return this.getRoutineArgIdx(this.routineInfo, n);
    }
    
    protected int getRoutineArgIdx(final RoutineAliasInfo routineAliasInfo, final int n) {
        if (!routineAliasInfo.hasVarargs()) {
            return n;
        }
        final int n2 = routineAliasInfo.getParameterCount() - 1;
        return (n2 < n) ? n2 : n;
    }
    
    public static String getParameterTypeName(final JavaValueNode javaValueNode) throws StandardException {
        String s;
        if (javaValueNode.isPrimitiveType()) {
            s = javaValueNode.getPrimitiveTypeName();
        }
        else {
            s = javaValueNode.getJavaTypeName();
        }
        return s;
    }
    
    public void generateOneParameter(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final int n) throws StandardException {
        this.methodParms[n].generateExpression(expressionClassBuilder, methodBuilder);
    }
    
    public void setNullParameterInfo(final String[] array) throws StandardException {
        for (int i = 0; i < this.methodParms.length; ++i) {
            if (this.methodParms[i].getJavaTypeName().equals("")) {
                ((SQLToJavaValueNode)this.methodParms[i]).value.setType(DataTypeDescriptor.getSQLDataTypeDescriptor(array[i]));
                this.methodParms[i].setJavaTypeName(array[i]);
                this.signature[i] = this.methodParms[i].getJSQLType();
            }
        }
    }
    
    protected void resolveMethodCall(final String s, final boolean b) throws StandardException {
        if (this.routineInfo == null && !this.internalCall && (this.getCompilerContext().getReliability() & 0x400) != 0x0) {
            throw StandardException.newException("42X01", s + (b ? "::" : ".") + this.methodName);
        }
        final int length = this.signature.length;
        final ClassInspector classInspector = this.getClassFactory().getClassInspector();
        String[] primitiveSignature = null;
        final boolean[] isParam = this.getIsParam();
        boolean b2 = !this.hasVarargs() && (this.routineInfo != null && length != 0 && length != this.methodParms.length);
        final int index = this.methodName.indexOf(40);
        String[] array;
        if (index != -1) {
            array = this.parseValidateSignature(this.methodName, index, b2);
            this.methodName = this.methodName.substring(0, index);
            b2 = false;
        }
        else {
            array = this.getObjectSignature();
        }
        if (this.hasVarargs()) {
            array[length - 1] += "[]";
        }
        try {
            this.method = classInspector.findPublicMethod(s, this.methodName, array, null, isParam, b, b2, this.hasVarargs());
            if (index == -1 && this.routineInfo == null && this.method == null) {
                primitiveSignature = this.getPrimitiveSignature(false);
                this.method = classInspector.findPublicMethod(s, this.methodName, array, primitiveSignature, isParam, b, b2, this.hasVarargs());
            }
        }
        catch (ClassNotFoundException ex) {
            this.method = null;
        }
        if (this.method == null) {
            this.throwNoMethodFound(s, array, primitiveSignature);
        }
        String type = classInspector.getType(this.method);
        this.actualMethodReturnType = type;
        if (this.routineInfo == null) {
            if (type.equals("void") && !this.forCallStatement) {
                throw StandardException.newException("42Y09");
            }
        }
        else {
            String correspondingJavaTypeName = null;
            final TypeDescriptorImpl typeDescriptorImpl = (TypeDescriptorImpl)this.routineInfo.getReturnType();
            String s2;
            if (typeDescriptorImpl == null) {
                s2 = "void";
            }
            else {
                final TypeId builtInTypeId = TypeId.getBuiltInTypeId(typeDescriptorImpl.getJDBCTypeId());
                if (typeDescriptorImpl.isRowMultiSet() && this.routineInfo.getParameterStyle() == 1) {
                    s2 = ResultSet.class.getName();
                }
                else if (typeDescriptorImpl.getTypeId().userType()) {
                    s2 = ((UserDefinedTypeIdImpl)typeDescriptorImpl.getTypeId()).getClassName();
                }
                else {
                    s2 = builtInTypeId.getCorrespondingJavaTypeName();
                    if (!s2.equals(type)) {
                        switch (typeDescriptorImpl.getJDBCTypeId()) {
                            case -5:
                            case 4:
                            case 5:
                            case 7:
                            case 8:
                            case 16: {
                                s2 = this.getTypeCompiler(builtInTypeId).getCorrespondingPrimitiveTypeName();
                                if (!this.routineInfo.calledOnNullInput() && this.routineInfo.getParameterCount() != 0) {
                                    correspondingJavaTypeName = builtInTypeId.getCorrespondingJavaTypeName();
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            boolean b3;
            if (ResultSet.class.getName().equals(s2)) {
                try {
                    b3 = ResultSet.class.isAssignableFrom(classInspector.getClass(type));
                }
                catch (ClassNotFoundException ex2) {
                    b3 = false;
                }
            }
            else {
                b3 = s2.equals(type);
            }
            if (!b3) {
                this.throwNoMethodFound(s2 + " " + s, array, primitiveSignature);
            }
            if (correspondingJavaTypeName != null) {
                type = correspondingJavaTypeName;
            }
            if (this.routineInfo.getReturnType() != null) {
                this.setCollationType(this.routineInfo.getReturnType().getCollationType());
            }
        }
        this.setJavaTypeName(type);
        this.methodParameterTypes = classInspector.getParameterTypes(this.method);
        String s3 = null;
        for (int i = 0; i < this.methodParameterTypes.length; ++i) {
            s3 = this.methodParameterTypes[i];
            if (this.routineInfo != null && i < this.routineInfo.getParameterCount()) {
                switch (this.routineInfo.getParameterModes()[this.getRoutineArgIdx(i)]) {
                    case 2: {
                        s3 = this.stripOneArrayLevel(s3);
                        break;
                    }
                    case 4: {
                        continue;
                    }
                }
            }
            if (this.hasVarargs() && i >= this.getFirstVarargIdx()) {
                s3 = this.stripOneArrayLevel(s3);
            }
            if (ClassInspector.primitiveType(s3) && i < this.methodParms.length) {
                this.methodParms[i].castToPrimitive(true);
            }
        }
        if (this.hasVarargs()) {
            final int firstVarargIdx = this.getFirstVarargIdx();
            for (int n = this.methodParms.length - firstVarargIdx, j = 1; j < n; ++j) {
                if (ClassInspector.primitiveType(s3)) {
                    this.methodParms[j + firstVarargIdx].castToPrimitive(true);
                }
            }
        }
        if (this.someParametersAreNull()) {
            this.setNullParameterInfo(this.methodParameterTypes);
        }
        final DataTypeDescriptor sqlDataTypeDescriptor = DataTypeDescriptor.getSQLDataTypeDescriptor(type);
        if (this.getCompilerContext().getReturnParameterFlag()) {
            this.getCompilerContext().getParameterTypes()[0] = sqlDataTypeDescriptor;
        }
    }
    
    protected String stripOneArrayLevel(final String s) {
        return s.substring(0, s.length() - 2);
    }
    
    private String[] parseValidateSignature(final String s, final int n, final boolean b) throws StandardException {
        final int length = s.length();
        if (n + 1 == length || s.charAt(length - 1) != ')') {
            throw StandardException.newException("46J01");
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s.substring(n + 1, length - 1), ",", true);
        String[] array = new String[this.signature.length];
        int n2 = 0;
        int n3 = 0;
        while (stringTokenizer.hasMoreTokens()) {
            final String trim = stringTokenizer.nextToken().trim();
            if (",".equals(trim)) {
                if (n2 == 0) {
                    throw StandardException.newException("46J01");
                }
                n2 = 0;
            }
            else {
                if (trim.length() == 0) {
                    throw StandardException.newException("46J01");
                }
                n2 = 1;
                if (++n3 > this.signature.length) {
                    if (!b) {
                        throw StandardException.newException("46J02", Integer.toString(n3), Integer.toString(this.signature.length));
                    }
                    final String correspondingJavaTypeName = this.signature[this.signature.length - 1].getSQLType().getTypeId().getCorrespondingJavaTypeName();
                    if (!trim.equals(correspondingJavaTypeName)) {
                        throw StandardException.newException("22005", trim, correspondingJavaTypeName);
                    }
                    if (array.length == this.signature.length) {
                        final String[] array2 = new String[stringTokenizer.countTokens()];
                        System.arraycopy(array, 0, array2, 0, array.length);
                        array = array2;
                    }
                    array[n3 - 1] = trim;
                }
                else {
                    final TypeId typeId = this.signature[n3 - 1].getSQLType().getTypeId();
                    if (trim.equals(typeId.getCorrespondingJavaTypeName())) {
                        array[n3 - 1] = trim;
                    }
                    else {
                        if (((typeId.isNumericTypeId() || typeId.isDecimalTypeId()) && !typeId.isBooleanTypeId()) || !trim.equals(this.getTypeCompiler(typeId).getCorrespondingPrimitiveTypeName())) {
                            throw StandardException.newException("22005", trim, typeId.getSQLTypeName());
                        }
                        array[n3 - 1] = trim;
                    }
                }
            }
        }
        if (n3 != 0 && n2 == 0) {
            throw StandardException.newException("46J01");
        }
        if (n3 >= array.length) {
            return array;
        }
        if (b && n3 == this.signature.length - 1) {
            final String[] array3 = new String[n3];
            System.arraycopy(array, 0, array3, 0, n3);
            return array3;
        }
        throw StandardException.newException("46J02", Integer.toString(n3), Integer.toString(this.signature.length));
    }
    
    protected boolean someParametersAreNull() {
        for (int length = this.signature.length, i = 0; i < length; ++i) {
            if (this.signature[i] == null) {
                return true;
            }
        }
        return false;
    }
    
    protected String[] getObjectSignature() throws StandardException {
        final int length = this.signature.length;
        final String[] array = new String[length];
        final TypeCompilerFactory typeCompilerFactory = (this.routineInfo == null) ? null : this.getCompilerContext().getTypeCompilerFactory();
        for (int i = 0; i < length; ++i) {
            array[i] = getObjectTypeName(this.signature[i], typeCompilerFactory);
        }
        return array;
    }
    
    protected boolean[] getIsParam() {
        if (this.methodParms == null) {
            return new boolean[0];
        }
        final boolean[] array = new boolean[this.methodParms.length];
        for (int i = 0; i < this.methodParms.length; ++i) {
            if (this.methodParms[i] instanceof SQLToJavaValueNode && ((SQLToJavaValueNode)this.methodParms[i]).value.requiresTypeFromContext()) {
                array[i] = true;
            }
        }
        return array;
    }
    
    static String getObjectTypeName(final JSQLType jsqlType, final TypeCompilerFactory typeCompilerFactory) throws StandardException {
        if (jsqlType != null) {
            switch (jsqlType.getCategory()) {
                case 0: {
                    final TypeId mapToTypeID = JavaValueNode.mapToTypeID(jsqlType);
                    if (mapToTypeID == null) {
                        return null;
                    }
                    switch (mapToTypeID.getJDBCTypeId()) {
                        case -5:
                        case 4:
                        case 5:
                        case 7:
                        case 8:
                        case 16: {
                            if (typeCompilerFactory != null) {
                                return typeCompilerFactory.getTypeCompiler(mapToTypeID).getCorrespondingPrimitiveTypeName();
                            }
                            break;
                        }
                    }
                    return mapToTypeID.getCorrespondingJavaTypeName();
                }
                case 1: {
                    return jsqlType.getJavaClassName();
                }
                case 2: {
                    return JSQLType.getPrimitiveName(jsqlType.getPrimitiveKind());
                }
            }
        }
        return "";
    }
    
    String[] getPrimitiveSignature(final boolean b) throws StandardException {
        final int length = this.signature.length;
        final String[] array = new String[length];
        for (int i = 0; i < length; ++i) {
            final JSQLType jsqlType = this.signature[i];
            if (jsqlType == null) {
                array[i] = "";
            }
            else {
                switch (jsqlType.getCategory()) {
                    case 0: {
                        if (this.procedurePrimitiveArrayType != null && i < this.procedurePrimitiveArrayType.length && this.procedurePrimitiveArrayType[i] != null) {
                            array[i] = this.procedurePrimitiveArrayType[i];
                            break;
                        }
                        final TypeId mapToTypeID = JavaValueNode.mapToTypeID(jsqlType);
                        if ((mapToTypeID.isNumericTypeId() && !mapToTypeID.isDecimalTypeId()) || mapToTypeID.isBooleanTypeId()) {
                            array[i] = this.getTypeCompiler(mapToTypeID).getCorrespondingPrimitiveTypeName();
                            if (b) {
                                this.methodParms[i].castToPrimitive(true);
                            }
                        }
                        else {
                            array[i] = mapToTypeID.getCorrespondingJavaTypeName();
                        }
                        break;
                    }
                    case 1: {
                        array[i] = jsqlType.getJavaClassName();
                        break;
                    }
                    case 2: {
                        array[i] = JSQLType.getPrimitiveName(jsqlType.getPrimitiveKind());
                        if (b) {
                            this.methodParms[i].castToPrimitive(true);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        return array;
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return this.getVariantTypeOfParams();
    }
    
    private int getVariantTypeOfParams() throws StandardException {
        int n = 2;
        if (this.methodParms != null) {
            for (int i = 0; i < this.methodParms.length; ++i) {
                if (this.methodParms[i] != null) {
                    final int orderableVariantType = this.methodParms[i].getOrderableVariantType();
                    if (orderableVariantType < n) {
                        n = orderableVariantType;
                    }
                }
                else {
                    n = 0;
                }
            }
        }
        return n;
    }
    
    public DataTypeDescriptor getDataType() throws StandardException {
        if (this.routineInfo != null) {
            final TypeDescriptor returnType = this.routineInfo.getReturnType();
            if (returnType != null) {
                return DataTypeDescriptor.getType(returnType);
            }
        }
        return super.getDataType();
    }
    
    public JavaValueNode[] getMethodParms() {
        return this.methodParms;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        for (int n = 0; !visitor.stopTraversal() && n < this.methodParms.length; ++n) {
            if (this.methodParms[n] != null) {
                this.methodParms[n] = (JavaValueNode)this.methodParms[n].accept(visitor);
            }
        }
    }
}
