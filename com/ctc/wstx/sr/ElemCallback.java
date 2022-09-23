// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import com.ctc.wstx.util.BaseNsContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

public abstract class ElemCallback
{
    public abstract Object withStartElement(final Location p0, final QName p1, final BaseNsContext p2, final ElemAttrs p3, final boolean p4);
}
