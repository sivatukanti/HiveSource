// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

public interface ComponentContext
{
    AccessibleObject getAccesibleObject();
    
    Annotation[] getAnnotations();
}
