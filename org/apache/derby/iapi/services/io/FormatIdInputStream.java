// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.loader.ClassFactoryContext;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import org.apache.derby.iapi.error.StandardException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.io.IOException;
import java.io.DataInput;
import java.io.InputStream;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.Resetable;
import java.io.DataInputStream;

public final class FormatIdInputStream extends DataInputStream implements ErrorObjectInput, Resetable, CloneableStream
{
    protected ClassFactory cf;
    private ErrorInfo errorInfo;
    private Exception myNestedException;
    
    public FormatIdInputStream(final InputStream in) {
        super(in);
    }
    
    public Object readObject() throws IOException, ClassNotFoundException {
        this.setErrorInfo(null);
        final int formatIdInteger = FormatIdUtil.readFormatIdInteger(this);
        if (formatIdInteger == 0) {
            return null;
        }
        if (formatIdInteger == 1) {
            return this.readUTF();
        }
        try {
            if (formatIdInteger == 2) {
                final ObjectInputStream objectStream = this.getObjectStream();
                try {
                    return objectStream.readObject();
                }
                catch (IOException ex) {
                    this.setErrorInfo((ErrorInfo)objectStream);
                    throw ex;
                }
                catch (ClassNotFoundException ex2) {
                    this.setErrorInfo((ErrorInfo)objectStream);
                    throw ex2;
                }
                catch (LinkageError linkageError) {
                    this.setErrorInfo((ErrorInfo)objectStream);
                    throw linkageError;
                }
                catch (ClassCastException ex3) {
                    this.setErrorInfo((ErrorInfo)objectStream);
                    throw ex3;
                }
            }
            try {
                final Formatable formatable = (Formatable)Monitor.newInstanceFromIdentifier(formatIdInteger);
                if (formatable instanceof Storable && this.readBoolean()) {
                    final Storable storable = (Storable)formatable;
                    storable.restoreToNull();
                    return storable;
                }
                formatable.readExternal(this);
                return formatable;
            }
            catch (StandardException ex4) {
                throw new ClassNotFoundException(ex4.toString());
            }
        }
        catch (ClassCastException cause) {
            final StreamCorruptedException ex5 = new StreamCorruptedException(cause.toString());
            ex5.initCause(cause);
            throw ex5;
        }
    }
    
    public void setInput(final InputStream in) {
        this.in = in;
    }
    
    public InputStream getInputStream() {
        return this.in;
    }
    
    public String getErrorInfo() {
        if (this.errorInfo == null) {
            return "";
        }
        return this.errorInfo.getErrorInfo();
    }
    
    public Exception getNestedException() {
        if (this.myNestedException != null) {
            return null;
        }
        if (this.errorInfo == null) {
            return null;
        }
        return this.errorInfo.getNestedException();
    }
    
    private void setErrorInfo(final ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }
    
    ClassFactory getClassFactory() {
        if (this.cf == null) {
            final ClassFactoryContext classFactoryContext = (ClassFactoryContext)ContextService.getContextOrNull("ClassFactoryContext");
            if (classFactoryContext != null) {
                this.cf = classFactoryContext.getClassFactory();
            }
        }
        return this.cf;
    }
    
    private ObjectInputStream getObjectStream() throws IOException {
        return (this.getClassFactory() == null) ? new ObjectInputStream(this) : new ApplicationObjectInputStream(this, this.cf);
    }
    
    public void resetStream() throws IOException, StandardException {
        ((Resetable)this.in).resetStream();
    }
    
    public void initStream() throws StandardException {
        ((Resetable)this.in).initStream();
    }
    
    public void closeStream() {
        ((Resetable)this.in).closeStream();
    }
    
    public InputStream cloneStream() {
        return new FormatIdInputStream(((CloneableStream)this.in).cloneStream());
    }
}
