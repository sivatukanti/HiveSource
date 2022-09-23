// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas;

import java.util.Iterator;
import java.util.Enumeration;
import java.security.Principal;
import java.util.HashSet;
import java.security.acl.Group;

public class JAASGroup implements Group
{
    public static final String ROLES = "__roles__";
    private String _name;
    private HashSet<Principal> _members;
    
    public JAASGroup(final String n) {
        this._name = null;
        this._members = null;
        this._name = n;
        this._members = new HashSet<Principal>();
    }
    
    public synchronized boolean addMember(final Principal principal) {
        return this._members.add(principal);
    }
    
    public synchronized boolean removeMember(final Principal principal) {
        return this._members.remove(principal);
    }
    
    public boolean isMember(final Principal principal) {
        return this._members.contains(principal);
    }
    
    public Enumeration<? extends Principal> members() {
        class MembersEnumeration implements Enumeration<Principal>
        {
            private Iterator<? extends Principal> itor;
            
            public MembersEnumeration() {
                this.itor = itor;
            }
            
            public boolean hasMoreElements() {
                return this.itor.hasNext();
            }
            
            public Principal nextElement() {
                return (Principal)this.itor.next();
            }
        }
        return new MembersEnumeration(this._members.iterator());
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        return object instanceof JAASGroup && ((JAASGroup)object).getName().equals(this.getName());
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    public String getName() {
        return this._name;
    }
}
