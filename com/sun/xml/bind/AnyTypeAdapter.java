// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class AnyTypeAdapter extends XmlAdapter<Object, Object>
{
    @Override
    public Object unmarshal(final Object v) {
        return v;
    }
    
    @Override
    public Object marshal(final Object v) {
        return v;
    }
}
