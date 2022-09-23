// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public class OIDImplKodo implements Serializable, OID, Comparable
{
    protected static final transient Localiser LOCALISER;
    private static final transient String oidSeparator = "-";
    public final Object oid;
    public final String pcClass;
    public final String toString;
    public final int hashCode;
    
    public OIDImplKodo() {
        this.oid = null;
        this.pcClass = null;
        this.toString = null;
        this.hashCode = -1;
    }
    
    public OIDImplKodo(final String pcClass, final Object object) {
        this.pcClass = pcClass;
        this.oid = object;
        final StringBuilder s = new StringBuilder();
        s.append(this.pcClass);
        s.append("-");
        s.append(this.oid.toString());
        this.toString = s.toString();
        this.hashCode = this.toString.hashCode();
    }
    
    public OIDImplKodo(final String str) throws IllegalArgumentException {
        if (str.length() < 2) {
            throw new IllegalArgumentException(OIDImplKodo.LOCALISER.msg("038000", str));
        }
        final int separatorPosition = str.indexOf("-");
        this.pcClass = str.substring(0, separatorPosition);
        final String oidStr = str.substring(separatorPosition + 1);
        Object oidValue = null;
        try {
            oidValue = Long.valueOf(oidStr);
        }
        catch (NumberFormatException nfe) {
            oidValue = oidStr;
        }
        this.oid = oidValue;
        this.toString = str;
        this.hashCode = this.toString.hashCode();
    }
    
    @Override
    public Object getKeyValue() {
        return this.oid;
    }
    
    @Override
    public String getPcClass() {
        return this.pcClass;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && (obj == this || (obj.getClass().getName().equals(OIDImplKodo.class.getName()) && this.hashCode() == obj.hashCode() && ((OID)obj).toString().equals(this.toString)));
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof OIDImplKodo) {
            final OIDImplKodo c = (OIDImplKodo)o;
            return this.toString.compareTo(c.toString);
        }
        if (o == null) {
            throw new ClassCastException("object is null");
        }
        throw new ClassCastException(this.getClass().getName() + " != " + o.getClass().getName());
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public String toString() {
        return this.toString;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
