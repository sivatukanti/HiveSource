// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.exceptions;

import javax.jdo.JDOUserException;

public class ClassNotPersistenceCapableException extends JDOUserException
{
    public ClassNotPersistenceCapableException(final String msg) {
        super(msg);
    }
    
    public ClassNotPersistenceCapableException(final String msg, final Exception nested) {
        super(msg, nested);
    }
}
