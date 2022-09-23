// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.cglib.reflect;

import java.lang.reflect.Member;

public abstract class $FastMember
{
    protected $FastClass fc;
    protected Member member;
    protected int index;
    
    protected $FastMember(final $FastClass fc, final Member member, final int index) {
        this.fc = fc;
        this.member = member;
        this.index = index;
    }
    
    public abstract Class[] getParameterTypes();
    
    public abstract Class[] getExceptionTypes();
    
    public int getIndex() {
        return this.index;
    }
    
    public String getName() {
        return this.member.getName();
    }
    
    public Class getDeclaringClass() {
        return this.fc.getJavaClass();
    }
    
    public int getModifiers() {
        return this.member.getModifiers();
    }
    
    public String toString() {
        return this.member.toString();
    }
    
    public int hashCode() {
        return this.member.hashCode();
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof $FastMember && this.member.equals((($FastMember)o).member);
    }
}
