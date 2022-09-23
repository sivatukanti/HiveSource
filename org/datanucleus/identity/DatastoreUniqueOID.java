// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

import java.io.Serializable;

public class DatastoreUniqueOID implements Serializable, OID, Comparable
{
    public final long key;
    
    public DatastoreUniqueOID() {
        this.key = -1L;
    }
    
    public DatastoreUniqueOID(final long key) {
        this.key = key;
    }
    
    public DatastoreUniqueOID(final String str) throws IllegalArgumentException {
        this.key = Long.parseLong(str);
    }
    
    @Override
    public Object getKeyValue() {
        return this.key;
    }
    
    public long getKey() {
        return this.key;
    }
    
    @Override
    public String getPcClass() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass().equals(this.getClass()) && this.key == ((DatastoreUniqueOID)obj).key);
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof DatastoreUniqueOID) {
            final DatastoreUniqueOID c = (DatastoreUniqueOID)o;
            return (int)(this.key - c.key);
        }
        if (o == null) {
            throw new ClassCastException("object is null");
        }
        throw new ClassCastException(this.getClass().getName() + " != " + o.getClass().getName());
    }
    
    @Override
    public int hashCode() {
        return (int)this.key;
    }
    
    @Override
    public String toString() {
        return Long.toString(this.key);
    }
}
