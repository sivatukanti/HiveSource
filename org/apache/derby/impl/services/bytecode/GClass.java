// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.services.compiler.ClassBuilder;

public abstract class GClass implements ClassBuilder
{
    protected ByteArray bytecode;
    protected final ClassFactory cf;
    protected final String qualifiedName;
    
    public GClass(final ClassFactory cf, final String qualifiedName) {
        this.cf = cf;
        this.qualifiedName = qualifiedName;
    }
    
    public String getFullName() {
        return this.qualifiedName;
    }
    
    public GeneratedClass getGeneratedClass() throws StandardException {
        return this.cf.loadGeneratedClass(this.qualifiedName, this.getClassBytecode());
    }
    
    protected void writeClassFile(final String s, final boolean b, final Throwable t) throws StandardException {
    }
    
    final void validateType(final String s) {
    }
}
