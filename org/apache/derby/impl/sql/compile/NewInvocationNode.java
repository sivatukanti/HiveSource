// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.lang.reflect.Member;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class NewInvocationNode extends MethodCallNode
{
    private boolean singleInstantiation;
    private boolean delimitedIdentifier;
    private boolean isBuiltinVTI;
    
    public NewInvocationNode() {
        this.singleInstantiation = false;
        this.isBuiltinVTI = false;
    }
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        super.init("<init>");
        this.addParms((List)o2);
        this.javaClassName = (String)o;
        this.delimitedIdentifier = (boolean)o3;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        super.init("<init>");
        this.addParms((List)o3);
        TableName tableName = (TableName)o;
        TableDescriptor tableDescriptor = (TableDescriptor)o2;
        final boolean b = o != null;
        if (b) {
            tableDescriptor = new TableDescriptor(this.getDataDictionary(), tableName.getTableName(), this.getSchemaDescriptor(tableName.getSchemaName()), 5, 'R');
        }
        this.javaClassName = this.getDataDictionary().getVTIClass(tableDescriptor, b);
        this.isBuiltinVTI = (this.getDataDictionary().getBuiltinVTIClass(tableDescriptor, b) != null);
        if (this.javaClassName == null) {
            if (!b) {
                tableName = this.makeTableName(tableDescriptor.getSchemaName(), tableDescriptor.getDescriptorName());
            }
            throw StandardException.newException(b ? "42Y03.S.0" : "42X05", tableName.getFullTableName());
        }
        this.delimitedIdentifier = (boolean)o4;
    }
    
    public boolean isBuiltinVTI() {
        return this.isBuiltinVTI;
    }
    
    void setSingleInstantiation() {
        this.singleInstantiation = true;
    }
    
    public JavaValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindParameters(list, list2, list3);
        this.verifyClassExist(this.javaClassName);
        final String[] objectSignature = this.getObjectSignature();
        final boolean[] isParam = this.getIsParam();
        final ClassInspector classInspector = this.getClassFactory().getClassInspector();
        try {
            this.method = classInspector.findPublicConstructor(this.javaClassName, objectSignature, null, isParam);
            if (this.method == null) {
                this.method = classInspector.findPublicConstructor(this.javaClassName, objectSignature, this.getPrimitiveSignature(false), isParam);
            }
        }
        catch (ClassNotFoundException ex) {
            this.method = null;
        }
        if (this.method == null) {
            String s = "";
            for (int i = 0; i < objectSignature.length; ++i) {
                if (i != 0) {
                    s += ", ";
                }
                s += ((objectSignature[i].length() != 0) ? objectSignature[i] : MessageService.getTextMessage("42Z01.U"));
            }
            throw StandardException.newException("42X75", this.javaClassName, s);
        }
        this.methodParameterTypes = classInspector.getParameterTypes(this.method);
        for (int j = 0; j < this.methodParameterTypes.length; ++j) {
            if (ClassInspector.primitiveType(this.methodParameterTypes[j])) {
                this.methodParms[j].castToPrimitive(true);
            }
        }
        if (this.someParametersAreNull()) {
            this.setNullParameterInfo(this.methodParameterTypes);
        }
        this.setJavaTypeName(this.javaClassName);
        if (this.routineInfo != null) {
            final TypeDescriptor returnType = this.routineInfo.getReturnType();
            if (returnType != null) {
                this.setCollationType(returnType.getCollationType());
            }
        }
        return this;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        return !b && true && super.categorize(set, b);
    }
    
    protected boolean assignableTo(final String s) throws StandardException {
        return this.getClassFactory().getClassInspector().assignableTo(this.javaClassName, s);
    }
    
    protected Member findPublicMethod(final String s, final boolean b) throws StandardException {
        final String[] objectSignature = this.getObjectSignature();
        final boolean[] isParam = this.getIsParam();
        final ClassInspector classInspector = this.getClassFactory().getClassInspector();
        Member member;
        try {
            member = classInspector.findPublicMethod(this.javaClassName, s, objectSignature, null, isParam, b, false, this.hasVarargs());
            if (member == null) {
                member = classInspector.findPublicMethod(this.javaClassName, s, objectSignature, this.getPrimitiveSignature(false), isParam, b, false, this.hasVarargs());
            }
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
        return member;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        LocalField fieldDeclaration = null;
        if (this.singleInstantiation) {
            fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, this.javaClassName);
            methodBuilder.getField(fieldDeclaration);
            methodBuilder.conditionalIfNull();
        }
        methodBuilder.pushNewStart(this.javaClassName);
        methodBuilder.pushNewComplete(this.generateParameters(expressionClassBuilder, methodBuilder));
        if (this.singleInstantiation) {
            methodBuilder.putField(fieldDeclaration);
            methodBuilder.startElseCode();
            methodBuilder.getField(fieldDeclaration);
            methodBuilder.completeConditional();
        }
    }
}
