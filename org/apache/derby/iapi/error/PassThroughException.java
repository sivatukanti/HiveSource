// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.error;

public final class PassThroughException extends RuntimeException
{
    public PassThroughException(final Throwable cause) {
        super(cause);
    }
}
