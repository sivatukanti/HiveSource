// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientHandler;

public abstract class Filterable
{
    private final ClientHandler root;
    private ClientHandler head;
    
    protected Filterable(final ClientHandler root) {
        this.head = root;
        this.root = root;
    }
    
    protected Filterable(final Filterable that) {
        this.root = that.root;
        this.head = that.head;
    }
    
    public void addFilter(final ClientFilter f) {
        f.setNext(this.head);
        this.head = f;
    }
    
    public void removeFilter(final ClientFilter f) {
        if (this.head == this.root) {
            return;
        }
        if (this.head == f) {
            this.head = f.getNext();
            return;
        }
        ClientFilter e;
        for (e = (ClientFilter)this.head; e.getNext() != f; e = (ClientFilter)e.getNext()) {
            if (e.getNext() == this.root) {
                return;
            }
        }
        e.setNext(f.getNext());
    }
    
    public boolean isFilterPreset(final ClientFilter f) {
        if (this.head == this.root) {
            return false;
        }
        if (this.head == f) {
            return true;
        }
        for (ClientFilter e = (ClientFilter)this.head; e.getNext() != f; e = (ClientFilter)e.getNext()) {
            if (e.getNext() == this.root) {
                return false;
            }
        }
        return true;
    }
    
    public void removeAllFilters() {
        this.head = this.root;
    }
    
    public ClientHandler getHeadHandler() {
        return this.head;
    }
}
