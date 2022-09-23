// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.monitor;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;

class ProtocolKey
{
    protected Class factoryInterface;
    protected String identifier;
    
    protected ProtocolKey(final Class factoryInterface, final String identifier) {
        this.factoryInterface = factoryInterface;
        this.identifier = identifier;
    }
    
    static ProtocolKey create(final String className, final String s) throws StandardException {
        ClassNotFoundException ex;
        try {
            return new ProtocolKey(Class.forName(className), s);
        }
        catch (ClassNotFoundException ex2) {
            ex = ex2;
        }
        catch (IllegalArgumentException ex3) {
            ex = (ClassNotFoundException)ex3;
        }
        catch (LinkageError linkageError) {
            ex = (ClassNotFoundException)linkageError;
        }
        throw Monitor.exceptionStartingModule(ex);
    }
    
    protected Class getFactoryInterface() {
        return this.factoryInterface;
    }
    
    protected String getIdentifier() {
        return this.identifier;
    }
    
    public int hashCode() {
        return this.factoryInterface.hashCode() + ((this.identifier == null) ? 0 : this.identifier.hashCode());
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof ProtocolKey)) {
            return false;
        }
        final ProtocolKey protocolKey = (ProtocolKey)o;
        if (this.factoryInterface != protocolKey.factoryInterface) {
            return false;
        }
        if (this.identifier == null) {
            if (protocolKey.identifier != null) {
                return false;
            }
        }
        else {
            if (protocolKey.identifier == null) {
                return false;
            }
            if (!this.identifier.equals(protocolKey.identifier)) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        return this.factoryInterface.getName() + " (" + this.identifier + ")";
    }
}
