// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.AbstractList;

public class Cat extends AbstractList
{
    protected List elements;
    
    public Iterator iterator() {
        return super.iterator();
    }
    
    public Object get(final int index) {
        return this.elements.get(index);
    }
    
    public int size() {
        return this.elements.size();
    }
    
    public Cat(final List attributes) {
        this.elements = new ArrayList();
        for (int i = 0; i < attributes.size(); ++i) {
            Object attribute = attributes.get(i);
            attribute = ASTExpr.convertAnythingIteratableToIterator(attribute);
            if (attribute instanceof Iterator) {
                final Iterator it = (Iterator)attribute;
                while (it.hasNext()) {
                    final Object o = it.next();
                    this.elements.add(o);
                }
            }
            else {
                this.elements.add(attribute);
            }
        }
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.elements.size(); ++i) {
            final Object o = this.elements.get(i);
            buf.append(o);
        }
        return buf.toString();
    }
}
