// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.txw2;

abstract class Text extends Content
{
    protected final StringBuilder buffer;
    
    protected Text(final Document document, final NamespaceResolver nsResolver, final Object obj) {
        document.writeValue(obj, nsResolver, this.buffer = new StringBuilder());
    }
    
    @Override
    boolean concludesPendingStartTag() {
        return false;
    }
}
