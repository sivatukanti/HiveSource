// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.Hashtable;

public class LazyHashtable extends Hashtable
{
    protected boolean initAllDone;
    
    public LazyHashtable() {
        this.initAllDone = false;
    }
    
    protected void initAll() {
        if (this.initAllDone) {
            return;
        }
        this.initAllDone = true;
    }
    
    @Override
    public Enumeration elements() {
        this.initAll();
        return super.elements();
    }
    
    @Override
    public boolean isEmpty() {
        this.initAll();
        return super.isEmpty();
    }
    
    @Override
    public int size() {
        this.initAll();
        return super.size();
    }
    
    @Override
    public boolean contains(final Object value) {
        this.initAll();
        return super.contains(value);
    }
    
    @Override
    public boolean containsKey(final Object value) {
        this.initAll();
        return super.containsKey(value);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.contains(value);
    }
    
    @Override
    public Enumeration keys() {
        this.initAll();
        return super.keys();
    }
}
