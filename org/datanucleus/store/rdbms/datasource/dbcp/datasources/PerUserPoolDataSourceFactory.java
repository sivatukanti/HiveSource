// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.datasources;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.io.IOException;
import javax.naming.RefAddr;
import java.util.Map;
import javax.naming.Reference;

public class PerUserPoolDataSourceFactory extends InstanceKeyObjectFactory
{
    private static final String PER_USER_POOL_CLASSNAME;
    
    @Override
    protected boolean isCorrectClass(final String className) {
        return PerUserPoolDataSourceFactory.PER_USER_POOL_CLASSNAME.equals(className);
    }
    
    @Override
    protected InstanceKeyDataSource getNewInstance(final Reference ref) throws IOException, ClassNotFoundException {
        final PerUserPoolDataSource pupds = new PerUserPoolDataSource();
        RefAddr ra = ref.get("defaultMaxActive");
        if (ra != null && ra.getContent() != null) {
            pupds.setDefaultMaxActive(Integer.parseInt(ra.getContent().toString()));
        }
        ra = ref.get("defaultMaxIdle");
        if (ra != null && ra.getContent() != null) {
            pupds.setDefaultMaxIdle(Integer.parseInt(ra.getContent().toString()));
        }
        ra = ref.get("defaultMaxWait");
        if (ra != null && ra.getContent() != null) {
            pupds.setDefaultMaxWait(Integer.parseInt(ra.getContent().toString()));
        }
        ra = ref.get("perUserDefaultAutoCommit");
        if (ra != null && ra.getContent() != null) {
            final byte[] serialized = (byte[])ra.getContent();
            pupds.perUserDefaultAutoCommit = (Map)InstanceKeyObjectFactory.deserialize(serialized);
        }
        ra = ref.get("perUserDefaultTransactionIsolation");
        if (ra != null && ra.getContent() != null) {
            final byte[] serialized = (byte[])ra.getContent();
            pupds.perUserDefaultTransactionIsolation = (Map)InstanceKeyObjectFactory.deserialize(serialized);
        }
        ra = ref.get("perUserMaxActive");
        if (ra != null && ra.getContent() != null) {
            final byte[] serialized = (byte[])ra.getContent();
            pupds.perUserMaxActive = (Map)InstanceKeyObjectFactory.deserialize(serialized);
        }
        ra = ref.get("perUserMaxIdle");
        if (ra != null && ra.getContent() != null) {
            final byte[] serialized = (byte[])ra.getContent();
            pupds.perUserMaxIdle = (Map)InstanceKeyObjectFactory.deserialize(serialized);
        }
        ra = ref.get("perUserMaxWait");
        if (ra != null && ra.getContent() != null) {
            final byte[] serialized = (byte[])ra.getContent();
            pupds.perUserMaxWait = (Map)InstanceKeyObjectFactory.deserialize(serialized);
        }
        ra = ref.get("perUserDefaultReadOnly");
        if (ra != null && ra.getContent() != null) {
            final byte[] serialized = (byte[])ra.getContent();
            pupds.perUserDefaultReadOnly = (Map)InstanceKeyObjectFactory.deserialize(serialized);
        }
        return pupds;
    }
    
    static {
        PER_USER_POOL_CLASSNAME = PerUserPoolDataSource.class.getName();
    }
}
