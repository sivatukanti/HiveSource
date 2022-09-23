// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class DiskValidatorFactory
{
    @VisibleForTesting
    static final ConcurrentHashMap<Class<? extends DiskValidator>, DiskValidator> INSTANCES;
    
    private DiskValidatorFactory() {
    }
    
    public static DiskValidator getInstance(final Class<? extends DiskValidator> clazz) {
        DiskValidator diskValidator;
        if (DiskValidatorFactory.INSTANCES.containsKey(clazz)) {
            diskValidator = DiskValidatorFactory.INSTANCES.get(clazz);
        }
        else {
            diskValidator = ReflectionUtils.newInstance(clazz, null);
            final DiskValidator diskValidatorRet = DiskValidatorFactory.INSTANCES.putIfAbsent(clazz, diskValidator);
            if (diskValidatorRet != null) {
                diskValidator = diskValidatorRet;
            }
        }
        return diskValidator;
    }
    
    public static DiskValidator getInstance(final String diskValidator) throws DiskChecker.DiskErrorException {
        Class clazz;
        if (diskValidator.equalsIgnoreCase("basic")) {
            clazz = BasicDiskValidator.class;
        }
        else if (diskValidator.equalsIgnoreCase("read-write")) {
            clazz = ReadWriteDiskValidator.class;
        }
        else {
            try {
                clazz = Class.forName(diskValidator);
            }
            catch (ClassNotFoundException cnfe) {
                throw new DiskChecker.DiskErrorException(diskValidator + " DiskValidator class not found.", cnfe);
            }
        }
        return getInstance(clazz);
    }
    
    static {
        INSTANCES = new ConcurrentHashMap<Class<? extends DiskValidator>, DiskValidator>();
    }
}
