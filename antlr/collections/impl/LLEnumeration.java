// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections.impl;

import java.util.NoSuchElementException;
import java.util.Enumeration;

final class LLEnumeration implements Enumeration
{
    LLCell cursor;
    LList list;
    
    public LLEnumeration(final LList list) {
        this.list = list;
        this.cursor = this.list.head;
    }
    
    public boolean hasMoreElements() {
        return this.cursor != null;
    }
    
    public Object nextElement() {
        if (!this.hasMoreElements()) {
            throw new NoSuchElementException();
        }
        final LLCell cursor = this.cursor;
        this.cursor = this.cursor.next;
        return cursor.data;
    }
}
