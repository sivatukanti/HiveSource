// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.lang.reflect.Modifier;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;
import java.lang.reflect.Member;

public final class StaticClassFieldReferenceNode extends JavaValueNode
{
    private String fieldName;
    private String javaClassName;
    private boolean classNameDelimitedIdentifier;
    private Member field;
    
    public void init(final Object o, final Object o2, final Object o3) {
        this.fieldName = (String)o2;
        this.javaClassName = (String)o;
        this.classNameDelimitedIdentifier = (boolean)o3;
    }
    
    public JavaValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        final ClassInspector classInspector = this.getClassFactory().getClassInspector();
        if ((this.getCompilerContext().getReliability() & 0x400) != 0x0 || !this.javaClassName.startsWith("java.sql.")) {
            throw StandardException.newException("42X01", this.javaClassName + "::" + this.fieldName);
        }
        this.verifyClassExist(this.javaClassName);
        this.field = classInspector.findPublicField(this.javaClassName, this.fieldName, true);
        this.setJavaTypeName(classInspector.getType(this.field));
        return this;
    }
    
    public void preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
    }
    
    public boolean categorize(final JBitSet set, final boolean b) {
        return true;
    }
    
    public JavaValueNode remapColumnReferencesToExpressions() throws StandardException {
        return this;
    }
    
    protected int getOrderableVariantType() {
        if (Modifier.isFinal(this.field.getModifiers())) {
            return 3;
        }
        return 0;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        methodBuilder.getStaticField(this.field.getDeclaringClass().getName(), this.fieldName, this.getJavaTypeName());
    }
}
