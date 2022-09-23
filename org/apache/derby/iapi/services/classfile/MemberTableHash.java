// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

class MemberTableHash
{
    String name;
    String descriptor;
    int index;
    int hashCode;
    
    MemberTableHash(final String name, final String descriptor, final int index) {
        this.name = name;
        this.descriptor = descriptor;
        this.index = index;
        if (name != null && descriptor != null) {
            this.setHashCode();
        }
    }
    
    MemberTableHash(final String s, final String s2) {
        this(s, s2, -1);
    }
    
    void setHashCode() {
        this.hashCode = this.name.hashCode() + this.descriptor.hashCode();
    }
    
    public boolean equals(final Object o) {
        final MemberTableHash memberTableHash = (MemberTableHash)o;
        return o != null && (this.name.equals(memberTableHash.name) && this.descriptor.equals(memberTableHash.descriptor));
    }
    
    public int hashCode() {
        return this.hashCode;
    }
}
