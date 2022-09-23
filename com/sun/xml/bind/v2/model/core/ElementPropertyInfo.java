// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.core;

import javax.xml.namespace.QName;
import java.util.List;

public interface ElementPropertyInfo<T, C> extends PropertyInfo<T, C>
{
    List<? extends TypeRef<T, C>> getTypes();
    
    QName getXmlName();
    
    boolean isCollectionRequired();
    
    boolean isCollectionNillable();
    
    boolean isValueList();
    
    boolean isRequired();
    
    Adapter<T, C> getAdapter();
}
