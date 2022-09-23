// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.interpol;

import java.util.HashMap;
import org.apache.commons.lang3.ClassUtils;
import java.lang.reflect.Field;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.Map;

public class ConstantLookup implements Lookup
{
    private static final char FIELD_SEPRATOR = '.';
    private static Map<String, Object> constantCache;
    private final Log log;
    
    public ConstantLookup() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public Object lookup(final String var) {
        if (var == null) {
            return null;
        }
        Object result;
        synchronized (ConstantLookup.constantCache) {
            result = ConstantLookup.constantCache.get(var);
        }
        if (result != null) {
            return result;
        }
        final int fieldPos = var.lastIndexOf(46);
        if (fieldPos < 0) {
            return null;
        }
        try {
            final Object value = this.resolveField(var.substring(0, fieldPos), var.substring(fieldPos + 1));
            if (value != null) {
                synchronized (ConstantLookup.constantCache) {
                    ConstantLookup.constantCache.put(var, value);
                }
                result = value;
            }
        }
        catch (Exception ex) {
            this.log.warn("Could not obtain value for variable " + var, ex);
        }
        return result;
    }
    
    public static void clear() {
        synchronized (ConstantLookup.constantCache) {
            ConstantLookup.constantCache.clear();
        }
    }
    
    protected Object resolveField(final String className, final String fieldName) throws Exception {
        final Class<?> clazz = this.fetchClass(className);
        final Field field = clazz.getField(fieldName);
        return field.get(null);
    }
    
    protected Class<?> fetchClass(final String className) throws ClassNotFoundException {
        return ClassUtils.getClass(className);
    }
    
    static {
        ConstantLookup.constantCache = new HashMap<String, Object>();
    }
}
