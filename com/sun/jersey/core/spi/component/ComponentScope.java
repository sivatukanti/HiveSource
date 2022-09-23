// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public enum ComponentScope
{
    Singleton, 
    PerRequest, 
    Undefined;
    
    public static final List<ComponentScope> UNDEFINED_SINGLETON;
    public static final List<ComponentScope> PERREQUEST_UNDEFINED_SINGLETON;
    public static final List<ComponentScope> PERREQUEST_UNDEFINED;
    
    static {
        UNDEFINED_SINGLETON = Collections.unmodifiableList((List<? extends ComponentScope>)Arrays.asList(ComponentScope.Undefined, ComponentScope.Singleton));
        PERREQUEST_UNDEFINED_SINGLETON = Collections.unmodifiableList((List<? extends ComponentScope>)Arrays.asList(ComponentScope.PerRequest, ComponentScope.Undefined, ComponentScope.Singleton));
        PERREQUEST_UNDEFINED = Collections.unmodifiableList((List<? extends ComponentScope>)Arrays.asList(ComponentScope.PerRequest, ComponentScope.Undefined));
    }
}
