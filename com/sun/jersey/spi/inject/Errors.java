// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.inject;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.ArrayList;

public final class Errors
{
    private final ArrayList<ErrorMessage> messages;
    private int mark;
    private int stack;
    private boolean fieldReporting;
    private static final Logger LOGGER;
    private static ThreadLocal<Errors> errors;
    
    public Errors() {
        this.messages = new ArrayList<ErrorMessage>(0);
        this.mark = -1;
        this.stack = 0;
        this.fieldReporting = true;
    }
    
    private void _mark() {
        this.mark = this.messages.size();
    }
    
    private void _unmark() {
        this.mark = -1;
    }
    
    private void _reset() {
        if (this.mark >= 0 && this.mark < this.messages.size()) {
            this.messages.subList(this.mark, this.messages.size()).clear();
            this._unmark();
        }
    }
    
    private void preProcess() {
        ++this.stack;
    }
    
    private void postProcess(final boolean throwException) {
        --this.stack;
        this.fieldReporting = true;
        if (this.stack == 0) {
            try {
                if (!this.messages.isEmpty()) {
                    processErrorMessages(throwException, this.messages);
                }
            }
            finally {
                Errors.errors.remove();
            }
        }
    }
    
    private static void processErrorMessages(final boolean throwException, final List<ErrorMessage> messages) {
        final StringBuilder sb = new StringBuilder();
        boolean isFatal = false;
        for (final ErrorMessage em : messages) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append("  ");
            if (em.isFatal) {
                sb.append("SEVERE: ");
            }
            else {
                sb.append("WARNING: ");
            }
            isFatal |= em.isFatal;
            sb.append(em.message);
        }
        final String message = sb.toString();
        if (isFatal) {
            Errors.LOGGER.severe("The following errors and warnings have been detected with resource and/or provider classes:\n" + message);
            if (throwException) {
                throw new ErrorMessagesException((List)new ArrayList(messages));
            }
        }
        else {
            Errors.LOGGER.warning("The following warnings have been detected with resource and/or provider classes:\n" + message);
        }
    }
    
    public static <T> T processWithErrors(final Closure<T> c) {
        Errors e = Errors.errors.get();
        if (e == null) {
            e = new Errors();
            Errors.errors.set(e);
        }
        e.preProcess();
        RuntimeException caught = null;
        try {
            return c.f();
        }
        catch (RuntimeException re) {
            caught = re;
        }
        finally {
            e.postProcess(caught == null);
        }
        throw caught;
    }
    
    private static Errors getInstance() {
        final Errors e = Errors.errors.get();
        if (e == null) {
            throw new IllegalStateException("There is no error processing in scope");
        }
        if (e.stack == 0) {
            Errors.errors.remove();
            throw new IllegalStateException("There is no error processing in scope");
        }
        return e;
    }
    
    public static void mark() {
        getInstance()._mark();
    }
    
    public static void unmark() {
        getInstance()._unmark();
    }
    
    public static void reset() {
        getInstance()._reset();
    }
    
    public static void error(final String message) {
        error(message, true);
    }
    
    public static void error(final String message, final boolean isFatal) {
        final ErrorMessage em = new ErrorMessage(message, isFatal);
        getInstance().messages.add(em);
    }
    
    public int numberOfErrors() {
        return getInstance().messages.size();
    }
    
    public static void innerClass(final Class c) {
        error("The inner class " + c.getName() + " is not a static inner class and cannot be instantiated.");
    }
    
    public static void nonPublicClass(final Class c) {
        error("The class " + c.getName() + " is a not a public class and cannot be instantiated.");
    }
    
    public static void nonPublicConstructor(final Class c) {
        error("The class " + c.getName() + " does not have a public constructor and cannot be instantiated.");
    }
    
    public static void abstractClass(final Class c) {
        error("The class " + c.getName() + " is an abstract class and cannot be instantiated.");
    }
    
    public static void interfaceClass(final Class c) {
        error("The class " + c.getName() + " is an interface and cannot be instantiated.");
    }
    
    public static void missingDependency(final Constructor ctor, final int i) {
        error("Missing dependency for constructor " + ctor + " at parameter index " + i);
    }
    
    public static void setReportMissingDependentFieldOrMethod(final boolean fieldReporting) {
        getInstance().fieldReporting = fieldReporting;
    }
    
    public static boolean getReportMissingDependentFieldOrMethod() {
        return getInstance().fieldReporting;
    }
    
    public static void missingDependency(final Field f) {
        if (getReportMissingDependentFieldOrMethod()) {
            error("Missing dependency for field: " + f.toGenericString());
        }
    }
    
    public static void missingDependency(final Method m, final int i) {
        if (getReportMissingDependentFieldOrMethod()) {
            error("Missing dependency for method " + m + " at parameter at index " + i);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(Errors.class.getName());
        Errors.errors = new ThreadLocal<Errors>();
    }
    
    public static class ErrorMessagesException extends RuntimeException
    {
        public final List<ErrorMessage> messages;
        
        private ErrorMessagesException(final List<ErrorMessage> messages) {
            this.messages = messages;
        }
    }
    
    public static class ErrorMessage
    {
        final String message;
        final boolean isFatal;
        
        private ErrorMessage(final String message, final boolean isFatal) {
            this.message = message;
            this.isFatal = isFatal;
        }
        
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + ((this.message != null) ? this.message.hashCode() : 0);
            hash = 37 * hash + (this.isFatal ? 1 : 0);
            return hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final ErrorMessage other = (ErrorMessage)obj;
            if (this.message == null) {
                if (other.message == null) {
                    return this.isFatal == other.isFatal;
                }
            }
            else if (this.message.equals(other.message)) {
                return this.isFatal == other.isFatal;
            }
            return false;
        }
    }
    
    public interface Closure<T>
    {
        T f();
    }
}
