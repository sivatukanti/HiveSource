// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.io.PrintWriter;
import java.io.PrintStream;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class InvalidAnnotationException extends NucleusUserException
{
    protected String messageKey;
    protected Throwable cause;
    
    public InvalidAnnotationException() {
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key, final Throwable cause) {
        this(localiser, key, "", "", "");
        this.cause = cause;
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key, final Object param1, final Throwable cause) {
        this(localiser, key, param1, "", "");
        this.cause = cause;
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key, final Object param1, final Object param2, final Throwable cause) {
        this(localiser, key, param1, param2, "");
        this.cause = cause;
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key, final Object param1, final Object param2, final Object param3, final Throwable cause) {
        super(localiser.msg(key, param1, param2, param3));
        this.messageKey = key;
        this.cause = cause;
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key) {
        this(localiser, key, "", "", "");
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key, final Object param1) {
        this(localiser, key, param1, "", "");
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key, final Object param1, final Object param2) {
        this(localiser, key, param1, param2, "");
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key, final Object param1, final Object param2, final Object param3) {
        super(localiser.msg(key, param1, param2, param3));
        this.messageKey = key;
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key, final Object param1, final Object param2, final Object param3, final Object param4) {
        super(localiser.msg(key, param1, param2, param3, param4));
        this.messageKey = key;
        this.setFatal();
    }
    
    public InvalidAnnotationException(final Localiser localiser, final String key, final Object param1, final Object param2, final Object param3, final Object param4, final Object param5) {
        super(localiser.msg(key, param1, param2, param3, param4, param5));
        this.messageKey = key;
        this.setFatal();
    }
    
    public String getMessageKey() {
        return this.messageKey;
    }
    
    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (this.cause != null) {
            this.cause.printStackTrace();
        }
    }
    
    @Override
    public void printStackTrace(final PrintStream s) {
        super.printStackTrace(s);
        if (this.cause != null) {
            this.cause.printStackTrace(s);
        }
    }
    
    @Override
    public void printStackTrace(final PrintWriter s) {
        super.printStackTrace(s);
        if (this.cause != null) {
            this.cause.printStackTrace(s);
        }
    }
}
