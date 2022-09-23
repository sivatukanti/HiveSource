// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

class MemberTable
{
    protected Vector entries;
    private Hashtable hashtable;
    private MemberTableHash mutableMTH;
    
    public MemberTable(final int initialCapacity) {
        this.mutableMTH = null;
        this.entries = new Vector(initialCapacity);
        this.hashtable = new Hashtable((initialCapacity > 50) ? initialCapacity : 50);
        this.mutableMTH = new MemberTableHash(null, null);
    }
    
    void addEntry(final ClassMember e) {
        final MemberTableHash memberTableHash = new MemberTableHash(e.getName(), e.getDescriptor(), this.entries.size());
        this.entries.add(e);
        this.hashtable.put(memberTableHash, memberTableHash);
    }
    
    ClassMember find(final String name, final String descriptor) {
        this.mutableMTH.name = name;
        this.mutableMTH.descriptor = descriptor;
        this.mutableMTH.setHashCode();
        final MemberTableHash memberTableHash = this.hashtable.get(this.mutableMTH);
        if (memberTableHash == null) {
            return null;
        }
        return (ClassMember)this.entries.get(memberTableHash.index);
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        final Vector entries = this.entries;
        for (int size = entries.size(), i = 0; i < size; ++i) {
            entries.get(i).put(classFormatOutput);
        }
    }
    
    int size() {
        return this.entries.size();
    }
    
    int classFileSize() {
        int n = 0;
        final Vector entries = this.entries;
        for (int size = entries.size(), i = 0; i < size; ++i) {
            n += entries.get(i).classFileSize();
        }
        return n;
    }
}
