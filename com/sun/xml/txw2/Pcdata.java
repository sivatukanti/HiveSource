// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.txw2;

final class Pcdata extends Text
{
    Pcdata(final Document document, final NamespaceResolver nsResolver, final Object obj) {
        super(document, nsResolver, obj);
    }
    
    @Override
    void accept(final ContentVisitor visitor) {
        visitor.onPcdata(this.buffer);
    }
}
