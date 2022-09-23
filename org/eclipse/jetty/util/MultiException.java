// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class MultiException extends Exception
{
    private List<Throwable> nested;
    
    public MultiException() {
        super("Multiple exceptions");
    }
    
    public void add(final Throwable e) {
        if (e == null) {
            throw new IllegalArgumentException();
        }
        if (this.nested == null) {
            this.initCause(e);
            this.nested = new ArrayList<Throwable>();
        }
        else {
            this.addSuppressed(e);
        }
        if (e instanceof MultiException) {
            final MultiException me = (MultiException)e;
            this.nested.addAll(me.nested);
        }
        else {
            this.nested.add(e);
        }
    }
    
    public int size() {
        return (this.nested == null) ? 0 : this.nested.size();
    }
    
    public List<Throwable> getThrowables() {
        if (this.nested == null) {
            return Collections.emptyList();
        }
        return this.nested;
    }
    
    public Throwable getThrowable(final int i) {
        return this.nested.get(i);
    }
    
    public void ifExceptionThrow() throws Exception {
        if (this.nested == null) {
            return;
        }
        switch (this.nested.size()) {
            case 0: {
                return;
            }
            case 1: {
                final Throwable th = this.nested.get(0);
                if (th instanceof Error) {
                    throw (Error)th;
                }
                if (th instanceof Exception) {
                    throw (Exception)th;
                }
                break;
            }
        }
        throw this;
    }
    
    public void ifExceptionThrowRuntime() throws Error {
        if (this.nested == null) {
            return;
        }
        switch (this.nested.size()) {
            case 0: {}
            case 1: {
                final Throwable th = this.nested.get(0);
                if (th instanceof Error) {
                    throw (Error)th;
                }
                if (th instanceof RuntimeException) {
                    throw (RuntimeException)th;
                }
                throw new RuntimeException(th);
            }
            default: {
                throw new RuntimeException(this);
            }
        }
    }
    
    public void ifExceptionThrowMulti() throws MultiException {
        if (this.nested == null) {
            return;
        }
        if (this.nested.size() > 0) {
            throw this;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(MultiException.class.getSimpleName());
        if (this.nested == null || this.nested.size() <= 0) {
            str.append("[]");
        }
        else {
            str.append(this.nested);
        }
        return str.toString();
    }
}
