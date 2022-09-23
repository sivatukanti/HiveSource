// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.txw2;

final class EndTag extends Content
{
    @Override
    boolean concludesPendingStartTag() {
        return true;
    }
    
    @Override
    void accept(final ContentVisitor visitor) {
        visitor.onEndTag();
    }
}
