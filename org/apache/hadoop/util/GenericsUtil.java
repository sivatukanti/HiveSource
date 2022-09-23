// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.Logger;
import org.slf4j.impl.Log4jLoggerAdapter;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Array;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GenericsUtil
{
    public static <T> Class<T> getClass(final T t) {
        final Class<T> clazz = (Class<T>)t.getClass();
        return clazz;
    }
    
    public static <T> T[] toArray(final Class<T> c, final List<T> list) {
        final T[] ta = (T[])Array.newInstance(c, list.size());
        for (int i = 0; i < list.size(); ++i) {
            ta[i] = list.get(i);
        }
        return ta;
    }
    
    public static <T> T[] toArray(final List<T> list) {
        return toArray((Class<T>)getClass((T)list.get(0)), list);
    }
    
    public static boolean isLog4jLogger(final Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        final Logger log = LoggerFactory.getLogger(clazz);
        return log instanceof Log4jLoggerAdapter;
    }
}
