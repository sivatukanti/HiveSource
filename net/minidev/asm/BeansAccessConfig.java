// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.asm;

import java.util.LinkedHashSet;
import java.util.HashMap;

public class BeansAccessConfig
{
    protected static HashMap<Class<?>, LinkedHashSet<Class<?>>> classMapper;
    protected static HashMap<Class<?>, HashMap<String, String>> classFiledNameMapper;
    
    static {
        BeansAccessConfig.classMapper = new HashMap<Class<?>, LinkedHashSet<Class<?>>>();
        BeansAccessConfig.classFiledNameMapper = new HashMap<Class<?>, HashMap<String, String>>();
        addTypeMapper(Object.class, DefaultConverter.class);
        addTypeMapper(Object.class, ConvertDate.class);
    }
    
    public static void addTypeMapper(final Class<?> clz, final Class<?> mapper) {
        synchronized (BeansAccessConfig.classMapper) {
            LinkedHashSet<Class<?>> h = BeansAccessConfig.classMapper.get(clz);
            if (h == null) {
                h = new LinkedHashSet<Class<?>>();
                BeansAccessConfig.classMapper.put(clz, h);
            }
            h.add(mapper);
        }
        // monitorexit(BeansAccessConfig.classMapper)
    }
}
