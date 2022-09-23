// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

import org.apache.derby.iapi.services.classfile.ClassHolder;
import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.iapi.services.compiler.ClassBuilder;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.cache.CacheFactory;
import java.util.Properties;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.cache.CacheableFactory;
import org.apache.derby.iapi.services.compiler.JavaFactory;

public class BCJava implements JavaFactory, CacheableFactory, ModuleControl
{
    private CacheManager vmTypeIdCache;
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.vmTypeIdCache = ((CacheFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.cache.CacheFactory")).newCacheManager(this, "VMTypeIdCache", 64, 256);
    }
    
    public void stop() {
    }
    
    public ClassBuilder newClassBuilder(final ClassFactory classFactory, final String s, final int n, final String s2, final String s3) {
        return new BCClass(classFactory, s, n, s2, s3, this);
    }
    
    public Cacheable newCacheable(final CacheManager cacheManager) {
        return new VMTypeIdCacheable();
    }
    
    Type type(final String s) {
        try {
            final VMTypeIdCacheable vmTypeIdCacheable = (VMTypeIdCacheable)this.vmTypeIdCache.find(s);
            final Type type = (Type)vmTypeIdCacheable.descriptor();
            this.vmTypeIdCache.release(vmTypeIdCacheable);
            return type;
        }
        catch (StandardException ex) {
            return new Type(s, ClassHolder.convertToInternalDescriptor(s));
        }
    }
    
    String vmType(final BCMethodDescriptor bcMethodDescriptor) {
        String s;
        try {
            final VMTypeIdCacheable vmTypeIdCacheable = (VMTypeIdCacheable)this.vmTypeIdCache.find(bcMethodDescriptor);
            s = vmTypeIdCacheable.descriptor().toString();
            this.vmTypeIdCache.release(vmTypeIdCacheable);
        }
        catch (StandardException ex) {
            s = bcMethodDescriptor.buildMethodDescriptor();
        }
        return s;
    }
    
    static short vmTypeId(final String s) {
        switch (s.charAt(0)) {
            case 'L': {
                return 7;
            }
            case 'B': {
                return 0;
            }
            case 'C': {
                return 6;
            }
            case 'D': {
                return 5;
            }
            case 'F': {
                return 4;
            }
            case 'I': {
                return 2;
            }
            case 'J': {
                return 3;
            }
            case 'S': {
                return 1;
            }
            case 'Z': {
                return 2;
            }
            case '[': {
                return 7;
            }
            case 'V': {
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
}
