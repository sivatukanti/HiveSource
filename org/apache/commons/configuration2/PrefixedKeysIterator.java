// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.NoSuchElementException;
import java.util.Iterator;

class PrefixedKeysIterator implements Iterator<String>
{
    private final Iterator<String> iterator;
    private final String prefix;
    private String nextElement;
    private boolean nextElementSet;
    
    public PrefixedKeysIterator(final Iterator<String> wrappedIterator, final String keyPrefix) {
        this.iterator = wrappedIterator;
        this.prefix = keyPrefix;
    }
    
    @Override
    public boolean hasNext() {
        return this.nextElementSet || this.setNextElement();
    }
    
    @Override
    public String next() {
        if (!this.nextElementSet && !this.setNextElement()) {
            throw new NoSuchElementException();
        }
        this.nextElementSet = false;
        return this.nextElement;
    }
    
    @Override
    public void remove() {
        if (this.nextElementSet) {
            throw new IllegalStateException("remove() cannot be called");
        }
        this.iterator.remove();
    }
    
    private boolean setNextElement() {
        while (this.iterator.hasNext()) {
            final String key = this.iterator.next();
            if (key.startsWith(this.prefix + ".") || key.equals(this.prefix)) {
                this.nextElement = key;
                return this.nextElementSet = true;
            }
        }
        return false;
    }
}
