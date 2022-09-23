// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.util;

import com.google.inject.internal.Errors;
import com.google.inject.internal.util.$Sets;
import java.util.Iterator;
import com.google.inject.internal.util.$ImmutableSet;
import java.util.Set;
import java.lang.annotation.Annotation;
import com.google.inject.Key;

class Node
{
    private final Key<?> key;
    private int appliedScope;
    private Node effectiveScopeDependency;
    private int effectiveScope;
    private Class<? extends Annotation> appliedScopeAnnotation;
    private Set<Node> users;
    
    Node(final Key<?> key) {
        this.appliedScope = Integer.MAX_VALUE;
        this.effectiveScope = Integer.MIN_VALUE;
        this.users = (Set<Node>)$ImmutableSet.of();
        this.key = key;
    }
    
    void setScopeRank(final int rank, final Class<? extends Annotation> annotation) {
        this.appliedScope = rank;
        this.effectiveScope = rank;
        this.appliedScopeAnnotation = annotation;
    }
    
    private void setEffectiveScope(final int effectiveScope, final Node effectiveScopeDependency) {
        if (this.effectiveScope >= effectiveScope) {
            return;
        }
        this.effectiveScope = effectiveScope;
        this.effectiveScopeDependency = effectiveScopeDependency;
        this.pushScopeToUsers();
    }
    
    void pushScopeToUsers() {
        for (final Node user : this.users) {
            user.setEffectiveScope(this.effectiveScope, this);
        }
    }
    
    boolean isScopedCorrectly() {
        return this.appliedScope >= this.effectiveScope;
    }
    
    boolean isEffectiveScopeAppliedScope() {
        return this.appliedScope == this.effectiveScope;
    }
    
    Node effectiveScopeDependency() {
        return this.effectiveScopeDependency;
    }
    
    public void addUser(final Node node) {
        if (this.users.isEmpty()) {
            this.users = (Set<Node>)$Sets.newHashSet();
        }
        this.users.add(node);
    }
    
    @Override
    public String toString() {
        return (this.appliedScopeAnnotation != null) ? (Errors.convert(this.key) + " in @" + this.appliedScopeAnnotation.getSimpleName()) : Errors.convert(this.key).toString();
    }
}
