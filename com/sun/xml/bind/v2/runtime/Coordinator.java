// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.bind.ValidationEventLocator;
import com.sun.xml.bind.v2.ClassFactory;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import javax.xml.bind.ValidationEventHandler;
import org.xml.sax.ErrorHandler;

public abstract class Coordinator implements ErrorHandler, ValidationEventHandler
{
    private final HashMap<Class<? extends XmlAdapter>, XmlAdapter> adapters;
    private Object old;
    private Object[] table;
    public Exception guyWhoSetTheTableToNull;
    private static final ThreadLocal<Object[]> activeTable;
    public static boolean debugTableNPE;
    
    public Coordinator() {
        this.adapters = new HashMap<Class<? extends XmlAdapter>, XmlAdapter>();
    }
    
    public final XmlAdapter putAdapter(final Class<? extends XmlAdapter> c, final XmlAdapter a) {
        if (a == null) {
            return this.adapters.remove(c);
        }
        return this.adapters.put(c, a);
    }
    
    public final <T extends XmlAdapter> T getAdapter(final Class<T> key) {
        T v = key.cast(this.adapters.get(key));
        if (v == null) {
            v = ClassFactory.create(key);
            this.putAdapter(key, v);
        }
        return v;
    }
    
    public <T extends XmlAdapter> boolean containsAdapter(final Class<T> type) {
        return this.adapters.containsKey(type);
    }
    
    protected final void setThreadAffinity() {
        this.table = Coordinator.activeTable.get();
        assert this.table != null;
    }
    
    protected final void resetThreadAffinity() {
        if (Coordinator.debugTableNPE) {
            this.guyWhoSetTheTableToNull = new Exception();
        }
        this.table = null;
    }
    
    protected final void pushCoordinator() {
        this.old = this.table[0];
        this.table[0] = this;
    }
    
    protected final void popCoordinator() {
        assert this.table[0] == this;
        this.table[0] = this.old;
        this.old = null;
    }
    
    public static Coordinator _getInstance() {
        return (Coordinator)Coordinator.activeTable.get()[0];
    }
    
    protected abstract ValidationEventLocator getLocation();
    
    public final void error(final SAXParseException exception) throws SAXException {
        this.propagateEvent(1, exception);
    }
    
    public final void warning(final SAXParseException exception) throws SAXException {
        this.propagateEvent(0, exception);
    }
    
    public final void fatalError(final SAXParseException exception) throws SAXException {
        this.propagateEvent(2, exception);
    }
    
    private void propagateEvent(final int severity, final SAXParseException saxException) throws SAXException {
        final ValidationEventImpl ve = new ValidationEventImpl(severity, saxException.getMessage(), this.getLocation());
        final Exception e = saxException.getException();
        if (e != null) {
            ve.setLinkedException(e);
        }
        else {
            ve.setLinkedException(saxException);
        }
        final boolean result = this.handleEvent(ve);
        if (!result) {
            throw saxException;
        }
    }
    
    static {
        activeTable = new ThreadLocal<Object[]>() {
            public Object[] initialValue() {
                return new Object[1];
            }
        };
        try {
            Coordinator.debugTableNPE = Boolean.getBoolean(Coordinator.class.getName() + ".debugTableNPE");
        }
        catch (SecurityException ex) {}
    }
}
