// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec;

public class PrematureChannelClosureException extends Exception
{
    private static final long serialVersionUID = 233460005724966593L;
    
    public PrematureChannelClosureException() {
    }
    
    public PrematureChannelClosureException(final String msg) {
        super(msg);
    }
    
    public PrematureChannelClosureException(final String msg, final Throwable t) {
        super(msg, t);
    }
    
    public PrematureChannelClosureException(final Throwable t) {
        super(t);
    }
}
