// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.util.Comparator;

public class PriorityUtil
{
    public static final int DEFAULT_PRIORITY = 100;
    public static final InstanceComparator INSTANCE_COMPARATOR;
    
    static {
        INSTANCE_COMPARATOR = new InstanceComparator();
    }
    
    public static final class InstanceComparator implements Comparator
    {
        @Override
        public int compare(final Object o1, final Object o2) {
            return this.priorityOf(o2) - this.priorityOf(o1);
        }
        
        private int priorityOf(final Object o) {
            final Priority priorityAnnotation = o.getClass().getAnnotation(Priority.class);
            return (priorityAnnotation == null) ? 100 : priorityAnnotation.value();
        }
    }
    
    public static class TypeComparator implements Comparator<Class<?>>
    {
        @Override
        public int compare(final Class<?> o1, final Class<?> o2) {
            return this.priorityOf(o2) - this.priorityOf(o1);
        }
        
        private int priorityOf(final Class<?> o) {
            final Priority priorityAnnotation = o.getAnnotation(Priority.class);
            return (priorityAnnotation == null) ? 100 : priorityAnnotation.value();
        }
    }
}
