// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.bind.v2.util.QNameMap;
import javax.xml.namespace.QName;

public interface StructureLoaderBuilder
{
    public static final QName TEXT_HANDLER = new QName("\u0000", "text");
    public static final QName CATCH_ALL = new QName("\u0000", "catchAll");
    
    void buildChildElementUnmarshallers(final UnmarshallerChain p0, final QNameMap<ChildLoader> p1);
}
