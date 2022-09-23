// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.datasources;

import java.util.HashMap;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import java.io.IOException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.util.Iterator;
import java.util.Map;
import javax.naming.spi.ObjectFactory;

abstract class InstanceKeyObjectFactory implements ObjectFactory
{
    private static final Map instanceMap;
    
    static synchronized String registerNewInstance(final InstanceKeyDataSource ds) {
        int max = 0;
        for (final Object obj : InstanceKeyObjectFactory.instanceMap.keySet()) {
            if (obj instanceof String) {
                try {
                    max = Math.max(max, Integer.valueOf((String)obj));
                }
                catch (NumberFormatException ex) {}
            }
        }
        final String instanceKey = String.valueOf(max + 1);
        InstanceKeyObjectFactory.instanceMap.put(instanceKey, ds);
        return instanceKey;
    }
    
    static void removeInstance(final String key) {
        InstanceKeyObjectFactory.instanceMap.remove(key);
    }
    
    public static void closeAll() throws Exception {
        final Iterator instanceIterator = InstanceKeyObjectFactory.instanceMap.entrySet().iterator();
        while (instanceIterator.hasNext()) {
            instanceIterator.next().getValue().close();
        }
        InstanceKeyObjectFactory.instanceMap.clear();
    }
    
    @Override
    public Object getObjectInstance(final Object refObj, final Name name, final Context context, final Hashtable env) throws IOException, ClassNotFoundException {
        Object obj = null;
        if (refObj instanceof Reference) {
            final Reference ref = (Reference)refObj;
            if (this.isCorrectClass(ref.getClassName())) {
                final RefAddr ra = ref.get("instanceKey");
                if (ra != null && ra.getContent() != null) {
                    obj = InstanceKeyObjectFactory.instanceMap.get(ra.getContent());
                }
                else {
                    String key = null;
                    if (name != null) {
                        key = name.toString();
                        obj = InstanceKeyObjectFactory.instanceMap.get(key);
                    }
                    if (obj == null) {
                        final InstanceKeyDataSource ds = this.getNewInstance(ref);
                        this.setCommonProperties(ref, ds);
                        obj = ds;
                        if (key != null) {
                            InstanceKeyObjectFactory.instanceMap.put(key, ds);
                        }
                    }
                }
            }
        }
        return obj;
    }
    
    private void setCommonProperties(final Reference ref, final InstanceKeyDataSource ikds) throws IOException, ClassNotFoundException {
        RefAddr ra = ref.get("dataSourceName");
        if (ra != null && ra.getContent() != null) {
            ikds.setDataSourceName(ra.getContent().toString());
        }
        ra = ref.get("defaultAutoCommit");
        if (ra != null && ra.getContent() != null) {
            ikds.setDefaultAutoCommit(Boolean.valueOf(ra.getContent().toString()));
        }
        ra = ref.get("defaultReadOnly");
        if (ra != null && ra.getContent() != null) {
            ikds.setDefaultReadOnly(Boolean.valueOf(ra.getContent().toString()));
        }
        ra = ref.get("description");
        if (ra != null && ra.getContent() != null) {
            ikds.setDescription(ra.getContent().toString());
        }
        ra = ref.get("jndiEnvironment");
        if (ra != null && ra.getContent() != null) {
            final byte[] serialized = (byte[])ra.getContent();
            ikds.jndiEnvironment = (Properties)deserialize(serialized);
        }
        ra = ref.get("loginTimeout");
        if (ra != null && ra.getContent() != null) {
            ikds.setLoginTimeout(Integer.parseInt(ra.getContent().toString()));
        }
        ra = ref.get("testOnBorrow");
        if (ra != null && ra.getContent() != null) {
            ikds.setTestOnBorrow(Boolean.valueOf(ra.getContent().toString()));
        }
        ra = ref.get("testOnReturn");
        if (ra != null && ra.getContent() != null) {
            ikds.setTestOnReturn(Boolean.valueOf(ra.getContent().toString()));
        }
        ra = ref.get("timeBetweenEvictionRunsMillis");
        if (ra != null && ra.getContent() != null) {
            ikds.setTimeBetweenEvictionRunsMillis(Integer.parseInt(ra.getContent().toString()));
        }
        ra = ref.get("numTestsPerEvictionRun");
        if (ra != null && ra.getContent() != null) {
            ikds.setNumTestsPerEvictionRun(Integer.parseInt(ra.getContent().toString()));
        }
        ra = ref.get("minEvictableIdleTimeMillis");
        if (ra != null && ra.getContent() != null) {
            ikds.setMinEvictableIdleTimeMillis(Integer.parseInt(ra.getContent().toString()));
        }
        ra = ref.get("testWhileIdle");
        if (ra != null && ra.getContent() != null) {
            ikds.setTestWhileIdle(Boolean.valueOf(ra.getContent().toString()));
        }
        ra = ref.get("validationQuery");
        if (ra != null && ra.getContent() != null) {
            ikds.setValidationQuery(ra.getContent().toString());
        }
    }
    
    protected abstract boolean isCorrectClass(final String p0);
    
    protected abstract InstanceKeyDataSource getNewInstance(final Reference p0) throws IOException, ClassNotFoundException;
    
    protected static final Object deserialize(final byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(data));
            return in.readObject();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    static {
        instanceMap = new HashMap();
    }
}
