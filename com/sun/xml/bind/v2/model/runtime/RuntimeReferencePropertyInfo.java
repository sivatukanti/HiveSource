// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.runtime;

import java.util.Set;
import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.core.ReferencePropertyInfo;

public interface RuntimeReferencePropertyInfo extends ReferencePropertyInfo<Type, Class>, RuntimePropertyInfo
{
    Set<? extends RuntimeElement> getElements();
}
