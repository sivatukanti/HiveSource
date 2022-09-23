// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import org.apache.thrift.TException;

public class TTransportException extends TException
{
    private static final long serialVersionUID = 1L;
    public static final int UNKNOWN = 0;
    public static final int NOT_OPEN = 1;
    public static final int ALREADY_OPEN = 2;
    public static final int TIMED_OUT = 3;
    public static final int END_OF_FILE = 4;
    protected int type_;
    
    public TTransportException() {
        this.type_ = 0;
    }
    
    public TTransportException(final int type) {
        this.type_ = 0;
        this.type_ = type;
    }
    
    public TTransportException(final int type, final String message) {
        super(message);
        this.type_ = 0;
        this.type_ = type;
    }
    
    public TTransportException(final String message) {
        super(message);
        this.type_ = 0;
    }
    
    public TTransportException(final int type, final Throwable cause) {
        super(cause);
        this.type_ = 0;
        this.type_ = type;
    }
    
    public TTransportException(final Throwable cause) {
        super(cause);
        this.type_ = 0;
    }
    
    public TTransportException(final String message, final Throwable cause) {
        super(message, cause);
        this.type_ = 0;
    }
    
    public TTransportException(final int type, final String message, final Throwable cause) {
        super(message, cause);
        this.type_ = 0;
        this.type_ = type;
    }
    
    public int getType() {
        return this.type_;
    }
}
