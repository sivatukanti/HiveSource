// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.List;

public class MultiException extends Exception
{
    private Object nested;
    
    public MultiException() {
        super("Multiple exceptions");
    }
    
    public void add(final Throwable e) {
        if (e instanceof MultiException) {
            final MultiException me = (MultiException)e;
            for (int i = 0; i < LazyList.size(me.nested); ++i) {
                this.nested = LazyList.add(this.nested, LazyList.get(me.nested, i));
            }
        }
        else {
            this.nested = LazyList.add(this.nested, e);
        }
    }
    
    public int size() {
        return LazyList.size(this.nested);
    }
    
    public List getThrowables() {
        return LazyList.getList(this.nested);
    }
    
    public Throwable getThrowable(final int i) {
        return (Throwable)LazyList.get(this.nested, i);
    }
    
    public void ifExceptionThrow() throws Exception {
        switch (LazyList.size(this.nested)) {
            case 0: {
                return;
            }
            case 1: {
                final Throwable th = (Throwable)LazyList.get(this.nested, 0);
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
        switch (LazyList.size(this.nested)) {
            case 0: {}
            case 1: {
                final Throwable th = (Throwable)LazyList.get(this.nested, 0);
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
        if (LazyList.size(this.nested) > 0) {
            throw this;
        }
    }
    
    public String toString() {
        if (LazyList.size(this.nested) > 0) {
            return "org.mortbay.util.MultiException" + LazyList.getList(this.nested);
        }
        return "org.mortbay.util.MultiException[]";
    }
    
    public void printStackTrace() {
        super.printStackTrace();
        for (int i = 0; i < LazyList.size(this.nested); ++i) {
            ((Throwable)LazyList.get(this.nested, i)).printStackTrace();
        }
    }
    
    public void printStackTrace(final PrintStream out) {
        super.printStackTrace(out);
        for (int i = 0; i < LazyList.size(this.nested); ++i) {
            ((Throwable)LazyList.get(this.nested, i)).printStackTrace(out);
        }
    }
    
    public void printStackTrace(final PrintWriter out) {
        super.printStackTrace(out);
        for (int i = 0; i < LazyList.size(this.nested); ++i) {
            ((Throwable)LazyList.get(this.nested, i)).printStackTrace(out);
        }
    }
}
