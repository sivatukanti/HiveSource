// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

class SerialVersionUIDAdder$Item implements Comparable
{
    final String name;
    final int access;
    final String desc;
    
    SerialVersionUIDAdder$Item(final String name, final int access, final String desc) {
        this.name = name;
        this.access = access;
        this.desc = desc;
    }
    
    public int compareTo(final Object o) {
        final SerialVersionUIDAdder$Item serialVersionUIDAdder$Item = (SerialVersionUIDAdder$Item)o;
        int n = this.name.compareTo(serialVersionUIDAdder$Item.name);
        if (n == 0) {
            n = this.desc.compareTo(serialVersionUIDAdder$Item.desc);
        }
        return n;
    }
}
