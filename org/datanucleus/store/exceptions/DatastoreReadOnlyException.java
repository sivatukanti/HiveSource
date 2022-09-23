// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.exceptions;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.NucleusUserException;

public class DatastoreReadOnlyException extends NucleusUserException
{
    ClassLoaderResolver clr;
    
    public DatastoreReadOnlyException(final String msg, final ClassLoaderResolver clr) {
        super(msg);
        this.clr = clr;
        this.setFatal();
    }
    
    public ClassLoaderResolver getClassLoaderResolver() {
        return this.clr;
    }
}
