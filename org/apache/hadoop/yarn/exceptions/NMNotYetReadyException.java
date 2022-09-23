// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

public class NMNotYetReadyException extends YarnException
{
    private static final long serialVersionUID = 1L;
    
    public NMNotYetReadyException(final String msg) {
        super(msg);
    }
}
