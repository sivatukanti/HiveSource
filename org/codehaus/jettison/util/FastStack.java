// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.util;

import java.util.EmptyStackException;
import java.util.ArrayList;

public class FastStack extends ArrayList
{
    public void push(final Object o) {
        this.add(o);
    }
    
    public Object pop() {
        if (this.empty()) {
            throw new EmptyStackException();
        }
        return this.remove(this.size() - 1);
    }
    
    public boolean empty() {
        return this.size() == 0;
    }
    
    public Object peek() {
        if (this.empty()) {
            throw new EmptyStackException();
        }
        return this.get(this.size() - 1);
    }
}
