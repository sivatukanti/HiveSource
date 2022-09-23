// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

import org.datanucleus.util.ClassUtils;
import org.datanucleus.ClassConstants;
import org.datanucleus.NucleusContext;

public class OIDFactory
{
    private OIDFactory() {
    }
    
    public static OID getInstance(final NucleusContext nucleusCtx, final String className, final Object value) {
        final Class oidClass = nucleusCtx.getDatastoreIdentityClass();
        OID oid;
        if (oidClass == ClassConstants.OID_IMPL) {
            oid = new OIDImpl(className, value);
        }
        else {
            oid = (OID)ClassUtils.newInstance(oidClass, new Class[] { String.class, Object.class }, new Object[] { className, value });
        }
        return oid;
    }
    
    public static OID getInstance(final NucleusContext nucleusCtx, final long value) {
        final Class oidClass = nucleusCtx.getDatastoreIdentityClass();
        OID oid;
        if (oidClass == DatastoreUniqueOID.class) {
            oid = new DatastoreUniqueOID(value);
        }
        else {
            oid = (OID)ClassUtils.newInstance(oidClass, new Class[] { Long.class }, new Object[] { value });
        }
        return oid;
    }
    
    public static OID getInstance(final NucleusContext nucleusCtx, final String oidString) {
        final Class oidClass = nucleusCtx.getDatastoreIdentityClass();
        OID oid;
        if (oidClass == ClassConstants.OID_IMPL) {
            oid = new OIDImpl(oidString);
        }
        else {
            oid = (OID)ClassUtils.newInstance(oidClass, new Class[] { String.class }, new Object[] { oidString });
        }
        return oid;
    }
}
