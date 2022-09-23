// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import org.apache.derby.iapi.services.loader.ClassFactory;
import java.io.ObjectInputStream;

class ApplicationObjectInputStream extends ObjectInputStream implements ErrorObjectInput
{
    protected ClassFactory cf;
    protected ObjectStreamClass initialClass;
    
    ApplicationObjectInputStream(final InputStream in, final ClassFactory cf) throws IOException {
        super(in);
        this.cf = cf;
    }
    
    protected Class resolveClass(final ObjectStreamClass initialClass) throws IOException, ClassNotFoundException {
        if (this.initialClass == null) {
            this.initialClass = initialClass;
        }
        if (this.cf != null) {
            return this.cf.loadApplicationClass(initialClass);
        }
        throw new ClassNotFoundException(initialClass.getName());
    }
    
    public String getErrorInfo() {
        if (this.initialClass == null) {
            return "";
        }
        return this.initialClass.getName() + " (serialVersionUID=" + this.initialClass.getSerialVersionUID() + ")";
    }
    
    public Exception getNestedException() {
        return null;
    }
}
