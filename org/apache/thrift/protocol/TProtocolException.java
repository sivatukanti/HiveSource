// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.protocol;

import org.apache.thrift.TException;

public class TProtocolException extends TException
{
    private static final long serialVersionUID = 1L;
    public static final int UNKNOWN = 0;
    public static final int INVALID_DATA = 1;
    public static final int NEGATIVE_SIZE = 2;
    public static final int SIZE_LIMIT = 3;
    public static final int BAD_VERSION = 4;
    public static final int NOT_IMPLEMENTED = 5;
    public static final int DEPTH_LIMIT = 6;
    protected int type_;
    
    public TProtocolException() {
        this.type_ = 0;
    }
    
    public TProtocolException(final int type) {
        this.type_ = 0;
        this.type_ = type;
    }
    
    public TProtocolException(final int type, final String message) {
        super(message);
        this.type_ = 0;
        this.type_ = type;
    }
    
    public TProtocolException(final String message) {
        super(message);
        this.type_ = 0;
    }
    
    public TProtocolException(final int type, final Throwable cause) {
        super(cause);
        this.type_ = 0;
        this.type_ = type;
    }
    
    public TProtocolException(final Throwable cause) {
        super(cause);
        this.type_ = 0;
    }
    
    public TProtocolException(final String message, final Throwable cause) {
        super(message, cause);
        this.type_ = 0;
    }
    
    public TProtocolException(final int type, final String message, final Throwable cause) {
        super(message, cause);
        this.type_ = 0;
        this.type_ = type;
    }
    
    public int getType() {
        return this.type_;
    }
}
