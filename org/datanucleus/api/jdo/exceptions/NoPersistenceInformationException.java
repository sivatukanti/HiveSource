// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.exceptions;

import javax.jdo.JDOUserException;

public class NoPersistenceInformationException extends JDOUserException
{
    public NoPersistenceInformationException(final String msg, final Exception nested) {
        super(msg, nested);
    }
}
