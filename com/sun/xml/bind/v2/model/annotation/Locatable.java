// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.annotation;

import com.sun.xml.bind.v2.runtime.Location;

public interface Locatable
{
    Locatable getUpstream();
    
    Location getLocation();
}
