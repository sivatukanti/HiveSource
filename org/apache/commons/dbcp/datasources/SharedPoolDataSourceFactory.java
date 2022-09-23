// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.datasources;

import java.io.IOException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;

public class SharedPoolDataSourceFactory extends InstanceKeyObjectFactory
{
    private static final String SHARED_POOL_CLASSNAME;
    
    @Override
    protected boolean isCorrectClass(final String className) {
        return SharedPoolDataSourceFactory.SHARED_POOL_CLASSNAME.equals(className);
    }
    
    @Override
    protected InstanceKeyDataSource getNewInstance(final Reference ref) {
        final SharedPoolDataSource spds = new SharedPoolDataSource();
        RefAddr ra = ref.get("maxActive");
        if (ra != null && ra.getContent() != null) {
            spds.setMaxActive(Integer.parseInt(ra.getContent().toString()));
        }
        ra = ref.get("maxIdle");
        if (ra != null && ra.getContent() != null) {
            spds.setMaxIdle(Integer.parseInt(ra.getContent().toString()));
        }
        ra = ref.get("maxWait");
        if (ra != null && ra.getContent() != null) {
            spds.setMaxWait(Integer.parseInt(ra.getContent().toString()));
        }
        return spds;
    }
    
    static {
        SHARED_POOL_CLASSNAME = SharedPoolDataSource.class.getName();
    }
}
