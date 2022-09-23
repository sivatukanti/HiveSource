// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.nativeio;

import org.apache.hadoop.util.Shell;
import java.io.IOException;

public class NativeIOException extends IOException
{
    private static final long serialVersionUID = 1L;
    private Errno errno;
    private int errorCode;
    
    public NativeIOException(final String msg, final Errno errno) {
        super(msg);
        this.errno = errno;
        this.errorCode = 0;
    }
    
    public NativeIOException(final String msg, final int errorCode) {
        super(msg);
        this.errorCode = errorCode;
        this.errno = Errno.UNKNOWN;
    }
    
    public long getErrorCode() {
        return this.errorCode;
    }
    
    public Errno getErrno() {
        return this.errno;
    }
    
    @Override
    public String toString() {
        if (Shell.WINDOWS) {
            return this.errorCode + ": " + super.getMessage();
        }
        return this.errno.toString() + ": " + super.getMessage();
    }
}
