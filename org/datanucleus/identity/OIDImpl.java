// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

import org.datanucleus.ClassConstants;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public class OIDImpl implements Serializable, OID, Comparable
{
    protected static final transient Localiser LOCALISER;
    private static final transient String oidSeparator = "[OID]";
    public final Object oid;
    public final String pcClass;
    public final String toString;
    public final int hashCode;
    
    public OIDImpl() {
        this.oid = null;
        this.pcClass = null;
        this.toString = null;
        this.hashCode = -1;
    }
    
    public OIDImpl(final String pcClass, final Object object) {
        this.pcClass = pcClass;
        this.oid = object;
        final StringBuilder s = new StringBuilder();
        s.append(this.oid.toString());
        s.append("[OID]");
        s.append(this.pcClass);
        this.toString = s.toString();
        this.hashCode = this.toString.hashCode();
    }
    
    public OIDImpl(final String str) throws IllegalArgumentException {
        if (str.length() < 2) {
            throw new IllegalArgumentException(OIDImpl.LOCALISER.msg("038000", str));
        }
        if (str.indexOf("[OID]") < 0) {
            throw new IllegalArgumentException(OIDImpl.LOCALISER.msg("038000", str));
        }
        int start = 0;
        final int end = str.indexOf("[OID]", start);
        final String oidStr = str.substring(start, end);
        Object oidValue = null;
        try {
            oidValue = Long.valueOf(oidStr);
        }
        catch (NumberFormatException nfe) {
            oidValue = oidStr;
        }
        this.oid = oidValue;
        start = end + "[OID]".length();
        this.pcClass = str.substring(start, str.length());
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
        return obj != null && (obj == this || (obj.getClass().getName().equals(ClassNameConstants.OIDImpl) && this.hashCode() == obj.hashCode() && ((OID)obj).toString().equals(this.toString)));
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof OIDImpl) {
            final OIDImpl c = (OIDImpl)o;
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
