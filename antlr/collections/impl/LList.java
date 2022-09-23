// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections.impl;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import antlr.collections.Stack;
import antlr.collections.List;

public class LList implements List, Stack
{
    protected LLCell head;
    protected LLCell tail;
    protected int length;
    
    public LList() {
        this.head = null;
        this.tail = null;
        this.length = 0;
    }
    
    public void add(final Object o) {
        this.append(o);
    }
    
    public void append(final Object o) {
        final LLCell llCell = new LLCell(o);
        if (this.length == 0) {
            final LLCell llCell2 = llCell;
            this.tail = llCell2;
            this.head = llCell2;
            this.length = 1;
        }
        else {
            this.tail.next = llCell;
            this.tail = llCell;
            ++this.length;
        }
    }
    
    protected Object deleteHead() throws NoSuchElementException {
        if (this.head == null) {
            throw new NoSuchElementException();
        }
        final Object data = this.head.data;
        this.head = this.head.next;
        --this.length;
        return data;
    }
    
    public Object elementAt(final int n) throws NoSuchElementException {
        int n2 = 0;
        for (LLCell llCell = this.head; llCell != null; llCell = llCell.next) {
            if (n == n2) {
                return llCell.data;
            }
            ++n2;
        }
        throw new NoSuchElementException();
    }
    
    public Enumeration elements() {
        return new LLEnumeration(this);
    }
    
    public int height() {
        return this.length;
    }
    
    public boolean includes(final Object obj) {
        for (LLCell llCell = this.head; llCell != null; llCell = llCell.next) {
            if (llCell.data.equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
    protected void insertHead(final Object o) {
        final LLCell head = this.head;
        this.head = new LLCell(o);
        this.head.next = head;
        ++this.length;
        if (this.tail == null) {
            this.tail = this.head;
        }
    }
    
    public int length() {
        return this.length;
    }
    
    public Object pop() throws NoSuchElementException {
        return this.deleteHead();
    }
    
    public void push(final Object o) {
        this.insertHead(o);
    }
    
    public Object top() throws NoSuchElementException {
        if (this.head == null) {
            throw new NoSuchElementException();
        }
        return this.head.data;
    }
}
