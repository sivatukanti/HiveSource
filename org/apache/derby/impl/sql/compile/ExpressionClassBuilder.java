// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.impl.sql.execute.IndexColumnOrder;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.compiler.JavaFactory;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.iapi.services.compiler.ClassBuilder;
import org.apache.derby.iapi.sql.compile.ExpressionClassBuilderInterface;

abstract class ExpressionClassBuilder implements ExpressionClassBuilderInterface
{
    protected static final String currentDatetimeFieldName = "cdt";
    protected ClassBuilder cb;
    protected GeneratedClass gc;
    protected int nextExprNum;
    protected int nextNonFastExpr;
    protected int nextFieldNum;
    protected MethodBuilder constructor;
    CompilerContext myCompCtx;
    MethodBuilder executeMethod;
    protected LocalField cdtField;
    private String currentRowScanResultSetName;
    private Object getDVF;
    private Object getRSF;
    private Object getEF;
    
    ExpressionClassBuilder(final String s, String uniqueClassName, final CompilerContext myCompCtx) throws StandardException {
        final int n = 17;
        this.myCompCtx = myCompCtx;
        final JavaFactory javaFactory = this.myCompCtx.getJavaFactory();
        if (uniqueClassName == null) {
            uniqueClassName = this.myCompCtx.getUniqueClassName();
        }
        this.cb = javaFactory.newClassBuilder(this.myCompCtx.getClassFactory(), this.getPackageName(), n, uniqueClassName, s);
        this.beginConstructor();
    }
    
    abstract String getPackageName();
    
    abstract int getRowCount() throws StandardException;
    
    abstract void setNumSubqueries() throws StandardException;
    
    abstract String getBaseClassName();
    
    MethodBuilder getConstructor() {
        return this.constructor;
    }
    
    ClassBuilder getClassBuilder() {
        return this.cb;
    }
    
    MethodBuilder getExecuteMethod() {
        if (this.executeMethod == null) {
            (this.executeMethod = this.cb.newMethodBuilder(4, "void", "reinit")).addThrownException("org.apache.derby.iapi.error.StandardException");
        }
        return this.executeMethod;
    }
    
    private final void beginConstructor() {
        final MethodBuilder constructorBuilder = this.cb.newConstructorBuilder(1);
        constructorBuilder.callSuper();
        constructorBuilder.methodReturn();
        constructorBuilder.complete();
        (this.constructor = this.cb.newMethodBuilder(1, "void", "postConstructor")).addThrownException("org.apache.derby.iapi.error.StandardException");
    }
    
    void finishConstructor() throws StandardException {
        this.setNumSubqueries();
        final int rowCount = this.getRowCount();
        if (rowCount >= 1) {
            this.addNewArrayOfRows(rowCount);
        }
        this.constructor.methodReturn();
        this.constructor.complete();
    }
    
    private void addNewArrayOfRows(final int n) {
        this.constructor.pushThis();
        this.constructor.pushNewArray("org.apache.derby.iapi.sql.execute.ExecRow", n);
        this.constructor.putField("org.apache.derby.impl.sql.execute.BaseActivation", "row", "org.apache.derby.iapi.sql.execute.ExecRow[]");
        this.constructor.endStatement();
    }
    
    LocalField newFieldDeclaration(final int n, final String s, final String s2) {
        return this.cb.addField(s, s2, n);
    }
    
    LocalField newFieldDeclaration(final int n, final String s) {
        return this.cb.addField(s, this.newFieldName(), n);
    }
    
    MethodBuilder newGeneratedFun(final String s, final int n) {
        return this.newGeneratedFun(s, n, null);
    }
    
    MethodBuilder newGeneratedFun(final String s, final int n, final String[] array) {
        return this.newGeneratedFun("g".concat(Integer.toString(this.nextNonFastExpr++)), s, n, array);
    }
    
    private MethodBuilder newGeneratedFun(final String s, final String s2, final int n, final String[] array) {
        MethodBuilder methodBuilder;
        if (array == null) {
            methodBuilder = this.cb.newMethodBuilder(n, s2, s);
        }
        else {
            methodBuilder = this.cb.newMethodBuilder(n, s2, s, array);
        }
        methodBuilder.addThrownException("org.apache.derby.iapi.error.StandardException");
        return methodBuilder;
    }
    
    MethodBuilder newExprFun() {
        return this.newGeneratedFun("e".concat(Integer.toString(this.nextExprNum++)), "java.lang.Object", 1, null);
    }
    
    void pushMethodReference(final MethodBuilder methodBuilder, final MethodBuilder methodBuilder2) {
        methodBuilder.pushThis();
        methodBuilder.push(methodBuilder2.getName());
        methodBuilder.callMethod((short)185, "org.apache.derby.iapi.services.loader.GeneratedByteCode", "getMethod", "org.apache.derby.iapi.services.loader.GeneratedMethod", 1);
    }
    
    MethodBuilder newUserExprFun() {
        final MethodBuilder exprFun = this.newExprFun();
        exprFun.addThrownException("java.lang.Exception");
        return exprFun;
    }
    
    void getCurrentDateExpression(final MethodBuilder methodBuilder) {
        methodBuilder.getField(this.getCurrentSetup());
        methodBuilder.callMethod((short)182, null, "getCurrentDate", "java.sql.Date", 0);
    }
    
    void getCurrentTimeExpression(final MethodBuilder methodBuilder) {
        methodBuilder.getField(this.getCurrentSetup());
        methodBuilder.callMethod((short)182, null, "getCurrentTime", "java.sql.Time", 0);
    }
    
    void getCurrentTimestampExpression(final MethodBuilder methodBuilder) {
        methodBuilder.getField(this.getCurrentSetup());
        methodBuilder.callMethod((short)182, null, "getCurrentTimestamp", "java.sql.Timestamp", 0);
    }
    
    FormatableArrayHolder getColumnOrdering(final ResultColumnList list) {
        final int n = (list == null) ? 0 : list.size();
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            if (!list.getResultColumn(i + 1).isGeneratedForUnmatchedColumnInInsert()) {
                ++n2;
            }
        }
        final IndexColumnOrder[] array = new IndexColumnOrder[n2];
        int j = 0;
        int n3 = 0;
        while (j < n) {
            if (!list.getResultColumn(j + 1).isGeneratedForUnmatchedColumnInInsert()) {
                array[n3] = new IndexColumnOrder(j);
                ++n3;
            }
            ++j;
        }
        return new FormatableArrayHolder(array);
    }
    
    FormatableArrayHolder addColumnToOrdering(final FormatableArrayHolder formatableArrayHolder, final int n) {
        final ColumnOrdering[] array = (ColumnOrdering[])formatableArrayHolder.getArray(ColumnOrdering.class);
        final int length = array.length;
        for (int i = 0; i < length; ++i) {
            if (array[i].getColumnId() == n) {
                return formatableArrayHolder;
            }
        }
        final IndexColumnOrder[] array2 = new IndexColumnOrder[length + 1];
        System.arraycopy(array, 0, array2, 0, length);
        array2[length] = new IndexColumnOrder(n);
        return new FormatableArrayHolder(array2);
    }
    
    FormatableArrayHolder getColumnOrdering(final OrderedColumnList list) {
        if (((list == null) ? 0 : list.size()) == 0) {
            return new FormatableArrayHolder(new IndexColumnOrder[0]);
        }
        return new FormatableArrayHolder(list.getColumnOrdering());
    }
    
    int addItem(final Object o) {
        return this.myCompCtx.addSavedObject(o);
    }
    
    void pushDataValueFactory(final MethodBuilder methodBuilder) {
        if (this.getDVF == null) {
            this.getDVF = methodBuilder.describeMethod((short)182, this.getBaseClassName(), "getDataValueFactory", "org.apache.derby.iapi.types.DataValueFactory");
        }
        methodBuilder.pushThis();
        methodBuilder.callMethod(this.getDVF);
    }
    
    void pushGetResultSetFactoryExpression(final MethodBuilder methodBuilder) {
        if (this.getRSF == null) {
            this.getRSF = methodBuilder.describeMethod((short)182, this.getBaseClassName(), "getResultSetFactory", "org.apache.derby.iapi.sql.execute.ResultSetFactory");
        }
        methodBuilder.pushThis();
        methodBuilder.callMethod(this.getRSF);
    }
    
    void pushGetExecutionFactoryExpression(final MethodBuilder methodBuilder) {
        if (this.getEF == null) {
            this.getEF = methodBuilder.describeMethod((short)182, this.getBaseClassName(), "getExecutionFactory", "org.apache.derby.iapi.sql.execute.ExecutionFactory");
        }
        methodBuilder.pushThis();
        methodBuilder.callMethod(this.getEF);
    }
    
    void pushColumnReference(final MethodBuilder methodBuilder, final int n, final int n2) {
        methodBuilder.pushThis();
        methodBuilder.push(n);
        methodBuilder.push(n2);
        methodBuilder.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "getColumnFromRow", "org.apache.derby.iapi.types.DataValueDescriptor", 2);
    }
    
    void pushPVSReference(final MethodBuilder methodBuilder) {
        methodBuilder.pushThis();
        methodBuilder.getField("org.apache.derby.impl.sql.execute.BaseActivation", "pvs", "org.apache.derby.iapi.sql.ParameterValueSet");
    }
    
    protected LocalField getCurrentSetup() {
        if (this.cdtField != null) {
            return this.cdtField;
        }
        this.cdtField = this.newFieldDeclaration(2, "org.apache.derby.impl.sql.execute.CurrentDatetime", "cdt");
        this.constructor.pushNewStart("org.apache.derby.impl.sql.execute.CurrentDatetime");
        this.constructor.pushNewComplete(0);
        this.constructor.setField(this.cdtField);
        return this.cdtField;
    }
    
    private String newFieldName() {
        return "e".concat(Integer.toString(this.nextFieldNum++));
    }
    
    protected TypeCompiler getTypeCompiler(final TypeId typeId) {
        return this.myCompCtx.getTypeCompilerFactory().getTypeCompiler(typeId);
    }
    
    GeneratedClass getGeneratedClass(final ByteArray byteArray) throws StandardException {
        if (this.gc != null) {
            return this.gc;
        }
        if (byteArray != null) {
            final ByteArray classBytecode = this.cb.getClassBytecode();
            byteArray.setBytes(classBytecode.getArray());
            byteArray.setLength(classBytecode.getLength());
        }
        return this.gc = this.cb.getGeneratedClass();
    }
    
    void pushThisAsActivation(final MethodBuilder methodBuilder) {
        methodBuilder.pushThis();
        methodBuilder.upCast("org.apache.derby.iapi.sql.Activation");
    }
    
    void generateNull(final MethodBuilder methodBuilder, final TypeCompiler typeCompiler, final int n) {
        this.pushDataValueFactory(methodBuilder);
        methodBuilder.pushNull(typeCompiler.interfaceName());
        typeCompiler.generateNull(methodBuilder, n);
    }
    
    void generateNullWithExpress(final MethodBuilder methodBuilder, final TypeCompiler typeCompiler, final int n) {
        this.pushDataValueFactory(methodBuilder);
        methodBuilder.swap();
        methodBuilder.cast(typeCompiler.interfaceName());
        typeCompiler.generateNull(methodBuilder, n);
    }
    
    void generateDataValue(final MethodBuilder methodBuilder, final TypeCompiler typeCompiler, final int n, final LocalField localField) {
        this.pushDataValueFactory(methodBuilder);
        methodBuilder.swap();
        typeCompiler.generateDataValue(methodBuilder, n, localField);
    }
    
    String newRowLocationScanResultSetName() {
        return this.currentRowScanResultSetName = this.newFieldName();
    }
    
    String getRowLocationScanResultSetName() {
        return this.currentRowScanResultSetName;
    }
}
