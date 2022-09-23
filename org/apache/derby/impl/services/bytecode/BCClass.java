// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.compiler.ClassBuilder;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.classfile.ClassHolder;

class BCClass extends GClass
{
    String limitMsg;
    protected ClassHolder classHold;
    protected String superClassName;
    protected String name;
    BCJava factory;
    final Type classType;
    
    public LocalField addField(final String s, final String s2, final int n) {
        final Type type = this.factory.type(s);
        return new BCLocalField(type, this.classHold.addFieldReference(this.classHold.addMember(s2, type.vmName(), n)));
    }
    
    public ByteArray getClassBytecode() throws StandardException {
        if (this.bytecode != null) {
            return this.bytecode;
        }
        try {
            this.bytecode = this.classHold.getFileFormat();
        }
        catch (IOException ex) {
            throw StandardException.newException("XBCM1.S", ex, this.getFullName());
        }
        this.classHold = null;
        if (this.limitMsg != null) {
            throw StandardException.newException("XBCM4.S", this.getFullName(), this.limitMsg);
        }
        return this.bytecode;
    }
    
    public String getName() {
        return this.name;
    }
    
    public MethodBuilder newMethodBuilder(final int n, final String s, final String s2) {
        return this.newMethodBuilder(n, s, s2, null);
    }
    
    public MethodBuilder newMethodBuilder(final int n, final String s, final String s2, final String[] array) {
        return new BCMethod(this, s, s2, n, array, this.factory);
    }
    
    public MethodBuilder newConstructorBuilder(final int n) {
        return new BCMethod(this, "void", "<init>", n, null, this.factory);
    }
    
    String getSuperClassName() {
        return this.superClassName;
    }
    
    ClassHolder modify() {
        return this.classHold;
    }
    
    BCClass(final ClassFactory classFactory, final String s, final int n, final String s2, String superClassName, final BCJava factory) {
        super(classFactory, s.concat(s2));
        this.name = s2;
        if (superClassName == null) {
            superClassName = "java.lang.Object";
        }
        this.superClassName = superClassName;
        this.classType = factory.type(this.getFullName());
        this.classHold = new ClassHolder(this.qualifiedName, factory.type(superClassName).vmNameSimple, n);
        this.factory = factory;
    }
    
    ClassFactory getClassFactory() {
        return this.cf;
    }
    
    void addLimitExceeded(final BCMethod bcMethod, final String str, final int i, final int j) {
        final StringBuffer sb = new StringBuffer();
        if (this.limitMsg != null) {
            sb.append(this.limitMsg);
            sb.append(", ");
        }
        sb.append("method:");
        sb.append(bcMethod.getName());
        sb.append(" ");
        sb.append(str);
        sb.append(" (");
        sb.append(j);
        sb.append(" > ");
        sb.append(i);
        sb.append(")");
        this.limitMsg = sb.toString();
    }
    
    void addLimitExceeded(final String s) {
        if (this.limitMsg != null) {
            this.limitMsg = this.limitMsg + ", " + s;
        }
        else {
            this.limitMsg = s;
        }
    }
}
